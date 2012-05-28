package org.secmem.remoteroid.network;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import org.secmem.remoteroid.data.NativeKeyCode;
import org.secmem.remoteroid.network.PacketHeader.OpCode;

import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

public class Tranceiver  implements PacketListener{
	private static final int PORT = 50000;
	
	private Socket socket;
	private OutputStream sendStream;
	private InputStream recvStream;
	
	private PacketReceiver packetReceiver;	
	private FileTranceiver fileTransReceiver;
	private ScreenSender screenSender;

	// Event listeners
	private FileTransmissionListener mFileTransListener;
	private VirtualEventListener mVirtEventListener;
	private ScreenTransmissionListener mScreenTransListener;
	private ServerConnectionListener mServerConnectionListener;
	
	public Tranceiver(ServerConnectionListener listener){
		mServerConnectionListener = listener;
	}
	
	/**
	 * Check whether connected to server or not.
	 * @return true if connected to server, false otherwise
	 */
	public boolean isConnected(){
		return socket!=null ? socket.isConnected() : false;		
	}
	
	public void setFileTransmissionListener(FileTransmissionListener listener){
		mFileTransListener = listener;
	}
	
	public void setVirtualEventListener(VirtualEventListener listener){
		mVirtEventListener = listener;
	}
	public void setFrameBufferListener(ScreenTransmissionListener listener){
		mScreenTransListener = listener;
	}
	
	/**
	 * Connect to specified host.
	 * @param ipAddr ip address
	 * @throws IOException
	 */
	public synchronized void connect(String ipAddr){
		try{
			socket = new Socket();
	
			socket.connect(new InetSocketAddress(ipAddr, PORT), 5000); // Set timeout to 5 seconds
	
			// Open outputStream
			sendStream = socket.getOutputStream();
			
			// Open inputStream
			recvStream = socket.getInputStream();		
			
			fileTransReceiver = new FileTranceiver(sendStream, mFileTransListener);
			
			//Connect udp socket
			screenSender = new ScreenSender(sendStream);
			
			// Create and start packet receiver
			packetReceiver = new PacketReceiver(recvStream);
			packetReceiver.setPacketListener(this);
			packetReceiver.start();	
			
			mServerConnectionListener.onServerConnected(ipAddr);
		}catch(IOException e){
			e.printStackTrace();
			mServerConnectionListener.onServerConnectionFailed();
		}
	}
	
	/**
	 * Disconnect from host.
	 * @throws IOException
	 */
	public synchronized void disconnect(){
		if(socket!=null){
			try{				
				recvStream.close();
				sendStream.close();
				packetReceiver = null;	
				mScreenTransListener.onScreenTransferStopRequested();
				socket.close();			
			}catch(IOException e){
				e.printStackTrace();
			} finally{
				mServerConnectionListener.onServerDisconnected();
			}
		}
	}
	
	private synchronized void cleanup(){
		if(socket!=null){
			try{
				recvStream.close();
				packetReceiver = null;
				socket.close();
				socket=null;
			
			}catch(IOException e){
				
			}
		}
	}
	
	public void sendFile(ArrayList<File> fileList){
		try{
			fileTransReceiver.sendFileList(fileList);
		}catch(IOException e){
			e.printStackTrace();
			mFileTransListener.onFileTransferInterrupted();
		}
	}
	
	//Send notification to Host
	public void sendNotification(String str){
		try{
			fileTransReceiver.send(
					new Packet(OpCode.NOTIFICATION_SEND, str.getBytes(), str.getBytes().length));
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	//Send frameBuffer to host by udp
	public void screenTransmission(byte[] jpgData){
		try{
			screenSender.screenTransmission(jpgData);
		}catch(IOException e){
			e.printStackTrace();
			mScreenTransListener.onScreenTransferInterrupted();
		}
	}
	
	
	//width, heigth resolution send to host 
	public void sendDeviceInfo(DisplayMetrics dm){
		try{
			fileTransReceiver.send(new DeviceInfoPacket(dm));
		}catch(IOException e){
			e.printStackTrace();
			onInterrupt();
		}
	}

	@Override
	public void onPacketReceived(Packet packet) {
		switch(packet.getOpcode()){		

		case OpCode.FILEINFO_RECEIVED:		
			fileTransReceiver.receiveFileInfo(packet);
			break;			
		case OpCode.FILEDATA_RECEIVED:			
			fileTransReceiver.receiveFileData(packet);
			break;			
		case OpCode.FILEDATA_REQUESTED:			
			fileTransReceiver.sendFileData();
			break;			
		case OpCode.FILEINFO_REQUESTED:			
			fileTransReceiver.sendFileInfo();
			break;
		case OpCode.EVENT_RECEIVED:			
			parseVirtualEventPacket(packet);
			break;	
		case OpCode.SCREEN_SEND_REQUESTED:			
			mScreenTransListener.onScreenTransferRequested();
			break;
		case OpCode.SCREEN_STOP_REQUESTED:			
			mScreenTransListener.onScreenTransferStopRequested();
			break;
		}
	}
		
	@Override
	public void onInterrupt() {
		
		//If server was closed, throw an IOException	
		//If file is open, Shoud be closed
		fileTransReceiver.closeFile();		
		cleanup();
		mServerConnectionListener.onServerConnectionInterrupted();
	}
	
	private void parseVirtualEventPacket(Packet packet){
		EventPacket eventPacket = EventPacket.parse(packet);
		
		switch(eventPacket.GetEventCode()){
		case EventPacket.SETCOORDINATES:
			mVirtEventListener.onSetCoordinates(eventPacket.GetXPosition(), eventPacket.GetYPosition());
			break;
		case EventPacket.TOUCHDOWN:
			mVirtEventListener.onSetCoordinates(eventPacket.GetXPosition(), eventPacket.GetYPosition());
			mVirtEventListener.onTouchDown();
			break;
		case EventPacket.TOUCHUP:
			mVirtEventListener.onTouchUp();
			break;
		case EventPacket.KEYDOWN:
			mVirtEventListener.onKeyDown(eventPacket.GetKeyCode());
			break;
		case EventPacket.KEYUP:
			mVirtEventListener.onKeyUp(eventPacket.GetKeyCode());
			break;
		case EventPacket.BACK:
			mVirtEventListener.onKeyStroke(NativeKeyCode.KEY_BACK);
			break;
		case EventPacket.MENU:
			mVirtEventListener.onKeyStroke(NativeKeyCode.KEY_MENU);
			break;
		case EventPacket.HOME:
			if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1)
				mVirtEventListener.onKeyStroke(NativeKeyCode.KEY_MOVE_HOME);
			else
				mVirtEventListener.onKeyStroke(NativeKeyCode.KEY_HOME);
		}
	}


	
}
