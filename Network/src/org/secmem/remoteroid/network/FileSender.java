package org.secmem.remoteroid.network;

import java.io.*;

import android.util.*;

public class FileSender implements iFileSendable{
	
	private final int 		maxDataSize 			= CONS.MAXPACKETSIZE-CONS.HEADERSIZE;
	private byte [] 		buffer 					= new byte[maxDataSize];
	private long 			fileSize 				= 0;
	private long 			sendedFileSize 			= 0;
	private String 			fileName 				= null;
	private packetmakeable 	packetMaker 			= null;
	FileInputStream 		in 						= null;
	
	
	public FileSender(packetmakeable packetMaker){
		this.packetMaker = packetMaker;
	}
	
	/*	  
	 *파일의 이름과  
	 */
	public void SendFileInfo(File file){
		fileSize = file.length();
		fileName = file.getName();
		
		byte[] bFileName = fileName.getBytes();
		byte[] bFileSize = String.valueOf(fileSize).getBytes();
		byte[] data = new byte[CONS.FILENAMESIZE+CONS.FILESIZESIZE];
		System.arraycopy(bFileName, 0, data, 0, bFileName.length);
		System.arraycopy(bFileSize, 0, data, CONS.FILENAMESIZE, bFileSize.length);
		// sendfileinfo를 위한 프로토콜 조립
		try{
			SendPacket(CONS.OPCODE.OP_SENDFILEINFO, data, data.length);
		}catch(IOException e){}	
	}
	
	public void SendFileData(File file){		
		try{
			in = new FileInputStream(file);
			
			while(fileSize > sendedFileSize){
				int iCurrentSendSize =
						(int) ((fileSize - sendedFileSize) > maxDataSize ? maxDataSize : (fileSize - sendedFileSize));
				in.read(buffer, 0, iCurrentSendSize);	
				SendPacket(CONS.OPCODE.OP_SENDFILEDATA, buffer, iCurrentSendSize);				
				sendedFileSize += iCurrentSendSize;
			}			
		}catch(FileNotFoundException e){
			Log.i("exception", "file not exception");			
		}catch(IOException e){
			Log.i("exception", "file sender IOException");			
		}
		finally{
			try{
				in.close();
				in = null;
			}catch(IOException e){};
		}		
	}
	
	public void SendPacket(int iOPCode, byte [] data, int length) throws IOException{
		packetMaker.SendPacket(iOPCode, data, length);
	}
}
