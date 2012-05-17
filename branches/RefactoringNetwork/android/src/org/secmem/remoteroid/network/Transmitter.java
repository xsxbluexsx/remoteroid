package org.secmem.remoteroid.network;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.ParseException;

import org.secmem.remoteroid.network.PacketHeader.OpCode;

import android.util.*;


public class Transmitter{
	private static final int PORT = 50000;
	
	private static Transmitter mInstance;
	
	private Socket socket;
	private OutputStream sendStream;
	private InputStream recvStream;
	
	private PacketSendListener packetListener;
	private FilePacketListener fileListener;
	
	private SendPacketTask sendPacketTask;
	private PacketReceiver packetReceiver;
	
	private Transmitter(){
		
	}
	
	public static Transmitter getInstance(){
		if(mInstance==null)
			mInstance = new Transmitter();
		return mInstance;
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
//		if(packetListener==null)
//			throw new IllegalStateException("Packet listener does not set!");
		//sendPacketTask = new SendPacketTask(sendStream, packetListener);
		
		// Open inputStream
		recvStream = socket.getInputStream();
		
		fileListener = new FileReceiver();
		
		if(fileListener==null)
			throw new IllegalStateException("File listener does not set!");
		
		// Create and start packet receiver
		packetReceiver = new PacketReceiver(recvStream, fileListener);
		packetReceiver.start();
	}
	
	/**
	 * Disconnect from host.
	 * @throws IOException
	 */
	public void disconnect(){
		if(socket!=null){
			try{
				packetReceiver.requestStop();
				recvStream.close();
			
				sendPacketTask = null;
				sendStream.close();		
				Log.i("qqqq", "socket close");
				socket.close();
			}catch(IOException e){}
		}
	}
	
	
	/**
	 *Send one packet to host. 
	 * @param opCode
	 * @param data
	 * @param length
	 */
	public void send(int opCode, byte[] data, int length){
		if(socket==null)
			return;
		
		try{
			sendStream.write(new Packet(opCode, data, length).asByteArray(),
					0, length+PacketHeader.LENGTH);
		}catch(IOException e){
			Log.i("exception", "send exception");
		}
	}
	
	/**
	 * Send packets to host.
	 * @param packets an array of packets
	 */
	public void send(Packet... packets){
		if(socket!=null){
			
		}
	}
	
	public void sendFile(File file){
		// TODO implement send only one file
	}
	
	public void sendFile(File... files){
		// TODO implement send multiple file
	}
	
	// TODO send 'ready to send' packet

	/**
	 * Get packet from host and notify to other component via listener to response to each packet properly.
	 * @author Taeho Kim
	 *
	 */
	class PacketReceiver extends Thread{
		
		private boolean isStopRequested = false;
		private InputStream recvStream;
		private FilePacketListener fileListener;
		// TODO private EventPacketListener eventListener;
		
		
		public PacketReceiver(InputStream recvStream, FilePacketListener fileListener){
			this.recvStream = recvStream;
			this.fileListener = fileListener;
		}
		
		public void requestStop(){
			isStopRequested = true;
		}
		
		/**
		 * Get packet from stream.
		 * @return a Packet object
		 * @throws IOException a network problem exists
		 * @throws ParseException a malformed packet received
		 */
		
		private byte[] buffer = new byte[Packet.MAX_LENGTH*2];
		private int bufferOffset = 0;
		
		public Packet getPacket() throws IOException, ParseException{
			
			int nRead;			
			while(true){
				nRead = recvStream.read(buffer, bufferOffset, Packet.MAX_LENGTH);
				
				
				if(nRead>0)
					bufferOffset+=nRead;
				
				// If packet size is smaller than header's length
				if(bufferOffset < PacketHeader.LENGTH)
					continue; // try fetching more data from stream
				
				// Try getting header data
				PacketHeader header = PacketHeader.parse(buffer);
				
				// If read data's length is smaller than whole packet's length
				
				if(bufferOffset < header.getPacketLength())
					continue; //  try fetching more data from stream
				
				// If you reached here, all required packed data has received.
				// Now we can parse received data as Packet object.
				Packet packet = Packet.parse(buffer);
				
				// Decrease current offset by last packet's length
				bufferOffset-=header.getPacketLength();
				
				//The remaining packets moves forward
				System.arraycopy(buffer, header.getPacketLength(), buffer, 0, bufferOffset);
				
				// Return packet object
				return packet;
			}
		}

		@Override
		public void run() {
			isStopRequested = false;
			
			/**
			 * Run infinitely and get packet from stream before user requested to stop
			 */
			while(!isStopRequested){
				try{
					Packet packet = getPacket();					
					
					switch(packet.getOpcode()){
					case OpCode.FILEINFO_RECEIVED:
						// TODO prototype
						fileListener.onReceiveFileInfo(packet);
						break;
						
					case OpCode.FILEDATA_RECEIVED:
						// TODO prototype
						fileListener.onReceiveFileData(packet);
						break;
						
					case OpCode.FILEDATA_REQUESTED:
						//fileListener.onFileDataRequested();
						break;
						
					case OpCode.FILEINFO_REQUESTED:
						//fileListener.onFileInfoRequested();
						break;
					}
					
				} catch(IOException e){
					e.printStackTrace();
					Log.i("qqqq", "ioexception");
					//If server was closed, throw an IOException					
					disconnect();
				} catch (ParseException e) {
					e.printStackTrace();
					disconnect();
				}
			}
		}
		
	}
}
