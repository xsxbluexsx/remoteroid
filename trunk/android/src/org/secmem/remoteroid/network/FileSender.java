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
	
	
	public FileSender(packetmakeable packetMaker){
		this.packetMaker = packetMaker;
	}	
	
	/**
	 * Send message that file is ready to send
	 * @param fileList
	 * @throws IOException
	 */
	public void SendFileList(ArrayList<File> fileList) throws IOException{
		this.fileList = fileList;		
		SendPacket(CONS.OPCODE.OP_READYSEND, null, 0);
	}
		
	/**
	 * �������� 由ъ��몄� 留�泥ル�吏�������대�怨��ш린 ��낫瑜����
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
		// sendfileinfo瑜���� ������ 議곕┰
		
		sendedFileSize = 0;
		Log.i("qqqq", "sendfile info");
		SendPacket(CONS.OPCODE.OP_SENDFILEINFO, data, data.length);		
	}
	
	public void SendFileData(){
		new SendFileDataThread().start();		
	}
	
	
	/**
	 * After sending first file of the file list, then send next file's info on the list
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
					Log.i("qqqq", "sending : "+sendedFileSize+" : "+fileSize);
				}				
				Log.i("qqqq", "complete");
				SendFileInfo();
			}catch(FileNotFoundException e){
				Log.i("exception", "file not exception");
			}catch(IOException e){
				DeleteFileList();
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
	
	public void DeleteFileList(){
		fileList.clear();		
		fileList = null;
	}
}
