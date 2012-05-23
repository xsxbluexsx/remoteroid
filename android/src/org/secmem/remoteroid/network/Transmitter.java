package org.secmem.remoteroid.network;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import org.secmem.remoteroid.network.PacketHeader.OpCode;

public class Transmitter implements PacketListener{
	private static final int PORT = 50000;
	
	private Socket socket;
	private OutputStream sendStream;
	private InputStream recvStream;
	
	private PacketReceiver packetReceiver;	
	private FileTranceiver fileTransReceiver;

	// Event listeners
	private FileTransmissionListener mFileTransListener;
	private VirtualEventListener mVirtEventListener;
	
	public Transmitter(){		
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
	
	/**
	 * Connect to specified host.
	 * @param ipAddr ip address
	 * @throws IOException
	 */
	public void connect(String ipAddr) throws IOException{
		socket = new Socket();
		socket.connect(new InetSocketAddress(ipAddr, PORT));
		
		// Open outputStream
		sendStream = socket.getOutputStream();
		
		// Open inputStream
		recvStream = socket.getInputStream();		
		
		fileTransReceiver = new FileTranceiver(sendStream, mFileTransListener);
		
		// Create and start packet receiver
		packetReceiver = new PacketReceiver(recvStream);
		packetReceiver.setPacketListener(this);
		packetReceiver.start();
	}
	
	/**
	 * Disconnect from host.
	 * @throws IOException
	 */
	public void disconnect(){
		if(socket!=null){
			try{				
				recvStream.close();
				sendStream.close();
				packetReceiver = null;				
				socket.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	public void sendFile(ArrayList<File> fileList){
		// TODO implement send only one file
		try{
			fileTransReceiver.sendFileList(fileList);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	//width, heigth resolutuin send to host 
	private void sendDeviceInfo(){
		try{
			fileTransReceiver.send(new DeviceInfoPacket(480, 800));
		}catch(IOException e){
			e.printStackTrace();
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
		case OpCode.DEVICEINFO_REQUESTED:
			sendDeviceInfo();
			break;
		}
	}
		
	@Override
	public void onInterrupt() {
		//If server was closed, throw an IOException	
		//If file is open, Shoud be closed
		fileTransReceiver.closeFile();
		disconnect();
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
		}
	}


	
}
