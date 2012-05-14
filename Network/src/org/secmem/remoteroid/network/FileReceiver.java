package org.secmem.remoteroid.network;

import java.io.*;

import android.os.*;
import android.util.*;


/**
 * 파일 수신 클래스
 * @author ssm
 *
 */
public class FileReceiver {
	
	private long 				totalFileSize 		= 0;
	private long 				recvFileSize 		= 0;
	private File 				file 				= null;
	private byte [] 			fileNameBuffer 		= new byte[CONS.FILENAMESIZE];
	private byte [] 			fileSizeBuffer 		= new byte[CONS.FILESIZESIZE];
	private FileOutputStream 	out					= null;
	private String 				strAbsoultePath 		= 
			Environment.getExternalStorageDirectory().getAbsolutePath()+"/Remoteroid/";
	private File				absoultePathDir		= new File(strAbsoultePath);
	
	/*
	 * 패킷에 기록된 파일 이름과 크기정보를 추출 해서 해당되는 이름으로 파일 및 스트림 생성
	 */	
	void RecvFileInfo(byte [] data){
		System.arraycopy(data, 0, fileNameBuffer, 0, CONS.FILENAMESIZE);
		System.arraycopy(data, CONS.FILENAMESIZE, fileSizeBuffer, 0, CONS.FILESIZESIZE);
		
		String fileName = new String(fileNameBuffer).trim();		
		
		if(!absoultePathDir.exists()){
			absoultePathDir.mkdir();
		}		
		file = new File(strAbsoultePath+fileName);
	
		int overlapCheck = 1;
		try{			
			while(!file.createNewFile()){
			//파일이 이미 존재한다면 이름을 변경
				String[] list = fileName.split("\\.");
				String newfileName = list[0]+'-'+overlapCheck+"."+list[1];
				file = new File(strAbsoultePath+newfileName);
				overlapCheck++;
			}			
			out = new FileOutputStream(file);
			totalFileSize = Long.parseLong(new String(fileSizeBuffer).trim());
			recvFileSize = 0;			
			
			NetworkModule.getInstance().SendPacket(CONS.OPCODE.OP_REQFILEDATA, null, 0);			
		}catch(IOException e){
			file = null;
			Log.i("exception", "createnew file : " + e.getMessage());			
		}		
	}
	
	void RecvFileData(byte [] data, int packetSize){
		int currentRecvLen = packetSize - CONS.HEADERSIZE;
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
	
	void CloseFile(){
		if(out != null){
			try {
				out.close();
			} catch (IOException e) {}
			out = null;
		}
	}
}
