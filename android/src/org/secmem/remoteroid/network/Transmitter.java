package org.secmem.remoteroid.network;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

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
	
	private PacketReceiver packetReceiver;	
	private FileTransReceiver fileTransReceiver;
	private VirtualEventGen virtualEventGen;
	
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
		
		// Open inputStream
		recvStream = socket.getInputStream();		
		
		fileTransReceiver = new FileTransReceiver(sendStream);
		
		//TODO Should VirtualEventListener implement				
		virtualEventGen = new VirtualEventGen(null);
		
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
				recvStream.close();
				sendStream.close();
				packetReceiver = null;				
				socket.close();
			}catch(IOException e){}
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


	/**
	 * Get packet from host and notify to other component via listener to response to each packet properly.
	 * @author Taeho Kim
	 *
	 */
	class PacketReceiver extends Thread{		
		
		private InputStream recvStream;
		private FilePacketListener fileListener;
		// TODO private EventPacketListener eventListener;		
		
		
		public PacketReceiver(InputStream recvStream, FilePacketListener fileListener){
			this.recvStream = recvStream;
			this.fileListener = fileListener;			
		}		
		
		
		/**
		 * Get packet from stream.
		 * @return a Packet object
		 * @throws IOException a network problem exists
		 * @throws ParseException a malformed packet received
		 */		
		private byte[] buffer = new byte[Packet.MAX_LENGTH*2];
		private int bufferOffset = 0;
		private Packet packet;
		
		public int ReadPacket() throws IOException{
//			int currentRead = Packet.MAX_LENGTH*2-bufferOffset <  Packet.MAX_LENGTH ? 
//					Packet.MAX_LENGTH*2-bufferOffset : Packet.MAX_LENGTH;				
			
			int readLen = recvStream.read(buffer, bufferOffset, Packet.MAX_LENGTH);			
				
			if(readLen>0)
				bufferOffset+=readLen;
			
			return readLen;
		}
		
		public boolean GetPacket(){
			if(bufferOffset < PacketHeader.LENGTH)
				return false; // try fetching more data from stream
			
			// Try getting header data
			PacketHeader header = PacketHeader.parse(buffer);
			
			// If read data's length is smaller than whole packet's length
			
			if(bufferOffset < header.getPacketLength())
				return false; //  try fetching more data from stream
			
			// If you reached here, all required packed data has received.
			// Now we can parse received data as Packet object.
			packet = Packet.parse(buffer);
			
			// Decrease current offset by last packet's length
			bufferOffset-=header.getPacketLength();
			
			//The remaining packets moves forward
			System.arraycopy(buffer, header.getPacketLength(), buffer, 0, bufferOffset);
			
			// Return packet object
			return true;
		}	
		

		@Override
		public void run() {			
			
			/**
			 * Run infinitely and get packet from stream before user requested to stop
			 */		
			
			while(true){
				try{
					int readLen = ReadPacket();
					if(readLen < 0){
						//when host was closed
						throw new IOException();
					}
					
					while(GetPacket()){
						switch(packet.getOpcode()){
						case OpCode.FILEINFO_RECEIVED:
							// TODO prototype						
							//fileListener.onReceiveFileInfo();
							fileTransReceiver.receiveFileInfo(packet);
							break;
							
						case OpCode.FILEDATA_RECEIVED:
							// TODO prototype
							//fileListener.onReceiveFileData();
							fileTransReceiver.receiveFileData(packet);
							break;
							
						case OpCode.FILEDATA_REQUESTED:
							//fileListener.onFileDataRequested();
							fileTransReceiver.sendFileData();
							break;
							
						case OpCode.FILEINFO_REQUESTED:
							//fileListener.onFileInfoRequested();
							fileTransReceiver.sendFileInfo();
							break;
						case OpCode.EVENT_RECEIVED:
							virtualEventGen.GenerateVirtualEvent(packet);
							break;
							
						}
					}					
				} catch(IOException e){
					e.printStackTrace();					
					//If server was closed, throw an IOException	
					//If file is open, Shoud be closed
					fileTransReceiver.closeFile();
					disconnect();
					break;
				}
			}
		}		
	}
}
