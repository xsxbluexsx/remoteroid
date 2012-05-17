package org.secmem.remoteroid.network;

import java.io.*;
import java.text.*;

import android.os.*;
import android.util.*;

public class FileReceiver implements FilePacketListener{
	private long totalFileSize;
	private long recvFileSize;
	private File file;
	private FileOutputStream out;
	private String strAbsoultePath = 
			Environment.getExternalStorageDirectory().getAbsolutePath()+"/Remoteroid/";
	
	private File absoultePathDir = new File(strAbsoultePath);

	public void onPacketSent() {
		// TODO Auto-generated method stub
		
	}

	public void onSendFailed() {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * Write the received data to a file
	 */
	public void onReceiveFileData(Packet packet) {
		// TODO Auto-generated method stub
		byte[] data = packet.getPayload();
		int currentRecvLen = packet.getHeader().getPayloadLength();
		try{
			out.write(data, 0, currentRecvLen);
			recvFileSize += currentRecvLen;
			if(totalFileSize <= recvFileSize){
				out.close();
				out = null;
			}
		}catch(IOException e){			
			try {
				out.close();
			} catch (IOException e1) {				
			}
			out = null;
			Log.i("exception", "RecvFileData" + e.getMessage());
		}
	}


	/**
	 * Ready to receive file with fileinfo
	 */
	public void onReceiveFileInfo(Packet packet){
		// TODO Auto-generated method stub		
		FileinfoPacket fileInfo = FileinfoPacket.parse(packet);
		
		String fileName = fileInfo.getFileName();
		totalFileSize = fileInfo.getFileSize();
		
		if(!absoultePathDir.exists()){
			absoultePathDir.mkdir();
		}
		file = new File(strAbsoultePath+fileName);
		
		int overlapCheck = 1;
		try{			
			while(!file.createNewFile()){
				//Filename duplicate cheack
				String[] list = fileName.split("\\.");
				String newfileName = list[0]+'-'+overlapCheck+"."+list[1];
				file = new File(strAbsoultePath+newfileName);
				overlapCheck++;
			}			
			out = new FileOutputStream(file);			
			recvFileSize = 0;
						
			requestFileData();
		}catch(IOException e){
			file = null;
			Log.i("exception", "createnew file : " + e.getMessage());			
		}		
	}
	
	/**
	 * After ready for receive file, Request filedata to server 
	 */
	private void requestFileData(){
		send(PacketHeader.OpCode.FILEDATA_REQUESTED, null, 0);
	}
	
	private void send(int opCode, byte[] data, int length){
		Transmitter.getInstance().send(opCode, data, length);
	}
	
	public void CloseFile(){
		if(out != null){
			try {
				out.close();
			} catch (IOException e) {}
			out = null;
		}
	}
	
}
