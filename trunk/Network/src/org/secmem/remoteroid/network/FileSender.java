package org.secmem.remoteroid.network;

import java.io.*;
import java.util.*;

import android.util.*;

public class FileSender extends Thread{
	
	private final int 		maxDataSize 			= CONS.MAXPACKETSIZE-CONS.HEADERSIZE;
	private byte [] 		buffer 					= new byte[maxDataSize];
	private byte []			data					= new byte[CONS.FILENAMESIZE+CONS.FILESIZESIZE];
	private long 			fileSize 				= 0;
	private long 			sendedFileSize 			= 0;
	private String 			fileName 				= null;
	private packetmakeable 	packetMaker 			= null;
	private FileInputStream	in 						= null;
	private ArrayList<File>	fileList				= null;
	private File			currentFile				= null;
	
	
	public FileSender(packetmakeable packetMaker){
		this.packetMaker = packetMaker;
	}	
	
	public void SendFileList(ArrayList<File> fileList) throws IOException{
		this.fileList = fileList;		
		SendFileInfo();
	}
		
	/**
	 * 전송할 파일 리스트의 맨 첫번째 파일의 이름과 크기 정보를 전송
	 * @throws IOException
	 */
	public void SendFileInfo() throws IOException{		
		if(fileList.isEmpty()){
			return;
		}
		
		File currentFile = fileList.get(0);
		fileSize = currentFile.length();
		fileName = currentFile.getName();
		
		byte[] bFileName = fileName.getBytes();
		byte[] bFileSize = String.valueOf(fileSize).getBytes();
		
		for(int i=0; i<data.length; i++){
			data[i] = 0;
		}
		
		System.arraycopy(bFileName, 0, data, 0, bFileName.length);
		System.arraycopy(bFileSize, 0, data, CONS.FILENAMESIZE, bFileSize.length);
		// sendfileinfo를 위한 프로토콜 조립
		
		sendedFileSize = 0;
		SendPacket(CONS.OPCODE.OP_SENDFILEINFO, data, data.length);			
	}
	
	public void SendFileData(){
		SendFileDataThread sendThread = new SendFileDataThread();
		sendThread.start();
	}
	
	
	/**
	 * 파일 리스트의 첫번째 파일의 내용을 전송하고 전송 완료시에는 다음 파일의 info를 전송한다
	 */
	class SendFileDataThread extends Thread{
		public void run(){
			try{				
				in = new FileInputStream(fileList.remove(0));			
				
				while(fileSize > sendedFileSize){
					int iCurrentSendSize =
							(int) ((fileSize - sendedFileSize) > maxDataSize ? maxDataSize : (fileSize - sendedFileSize));
					in.read(buffer, 0, iCurrentSendSize);	
					SendPacket(CONS.OPCODE.OP_SENDFILEDATA, buffer, iCurrentSendSize);				
					sendedFileSize += iCurrentSendSize;
				}			
				SendFileInfo();
			}catch(FileNotFoundException e){
				Log.i("exception", "file not exception");
			}catch(IOException e){
				Log.i("exception", "file send thread");
			}finally{
				try{
					in.close();				
				}catch(IOException e){};
				in = null;
			}		
		}
	}	
	
	public void SendPacket(int iOPCode, byte [] data, int length) throws IOException{
		packetMaker.SendPacket(iOPCode, data, length);
	}
}
