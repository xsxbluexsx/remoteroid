package org.secmem.remoteroid.network;

import java.io.*;
import java.util.*;

import org.secmem.remoteroid.data.*;

import android.os.*;
import android.util.*;

public class FileTransReceiver extends PacketSender{
	
	FileReceiver fileReceiver;
	FileSender fileSender;
	
	public FileTransReceiver(OutputStream stream){
		super(stream);
		
		fileReceiver = new FileReceiver();
		fileSender = new FileSender();
	}
	
	public void receiveFileInfo(Packet packet){
		fileReceiver.receiveFileInfo(packet);
	}
	
	public void receiveFileData(Packet packet){
		fileReceiver.receiveFileData(packet);
	}
	public void closeFile(){
		fileSender.DeleteFileList();
		fileReceiver.CloseFile();
	}
	
	public void sendFileList(ArrayList<File> fileList) throws IOException{
		fileSender.sendFileList(fileList);
	}
	
	public void sendFileInfo() throws IOException{
		fileSender.SendFileInfo();
	}
	
	public void sendFileData(){
		fileSender.SendFileData();
	}
	
	/**
	 * FileReceiver receive File Information(file name, size) and file data
	 * And store file to SDCARD
	 * @author ssm
	 */
	class FileReceiver{
		private long totalFileSize;
		private long recvFileSize;
		private File file;
		private FileOutputStream out;
		
		//private File absoultePathDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Remoteroid/");		
		
		
		/**
		 * Create file that Received information(file name, size)		   
		 * @param packet
		 */
		private void receiveFileInfo(Packet packet){
			FileinfoPacket fileInfo = FileinfoPacket.parse(packet);
			
			String fileName = fileInfo.getFileName();
			totalFileSize = fileInfo.getFileSize();
		
			File absoultePathDir = new File(CommunicateInfo.getCurrentPath());
			Log.i("asd","dir = "+absoultePathDir.getAbsolutePath());
			if(!absoultePathDir.exists()){
				absoultePathDir.mkdir();
			}
			file = new File(absoultePathDir+"/"+fileName);
			
			int overlapCheck = 1;
			try{			
				while(!file.createNewFile()){
					//Filename duplicate cheack
					String[] list = fileName.split("\\.");
					String newfileName = list[0]+'-'+overlapCheck+"."+list[1];
					file = new File(absoultePathDir+newfileName);
					overlapCheck++;
				}			
				out = new FileOutputStream(file);			
				recvFileSize = 0;
						
				//Send to host that ready for receive file
				send(new Packet(PacketHeader.OpCode.FILEDATA_REQUESTED, null, 0));
			}catch(IOException e){
				file = null;
				e.printStackTrace();							
			}		
		}
		
		
		
		/**
		 * Store file to SDCARD that received file data
		 * @param packet
		 */
		private void receiveFileData(Packet packet){
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
				e.printStackTrace();
				CloseFile();		
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
	
	
	/**
	 * FileSender send file information(file name, size) and file data to host
	 * @author ssm
	 */
	class FileSender{
		private final int 		MAXDATASIZE 			= Packet.MAX_LENGTH-PacketHeader.LENGTH;
		private byte [] 		buffer 					= new byte[MAXDATASIZE];			
		private FileInputStream	in 						= null;
		private ArrayList<File>	fileList				= null;	
		
		
		public FileSender(){		
		}	
		
		
		/**
		 * Set fileList for transmit and 
		 * Send to host that ready for send file
		 * @param fileList
		 */
		public void sendFileList(ArrayList<File> fileList) throws IOException{
			this.fileList = fileList;
			send(new Packet(PacketHeader.OpCode.READY_TO_SEND, null, 0));			
		}
		
		
		/**
		 * Send first file information(Name, Size) of FileList to Host
		 * @throws IOException
		 */
		public void SendFileInfo() throws IOException{		
			if(fileList.isEmpty()){
				return;
			}
			
			File currentFile = fileList.get(0);
			
			FileinfoPacket fileInfoPacket = new FileinfoPacket(currentFile);			
			
			send(fileInfoPacket);		
		}
		
		
		public void SendFileData(){
			new SendFileDataThread().start();		
		}
		
		
		/**
		 *Send to host that first File data of file list
		 */
		class SendFileDataThread extends Thread{
			public void run(){
				try{
					File file = fileList.remove(0);
					long fileSize = file.length();
					long sentFileSize = 0;
					
					in = new FileInputStream(file);			
					
					while(fileSize > sentFileSize){					
						int iCurrentSendSize =
								(int) ((fileSize - sentFileSize) > MAXDATASIZE ? MAXDATASIZE : (fileSize - sentFileSize));
						in.read(buffer, 0, iCurrentSendSize);	
						
						send(new Packet(PacketHeader.OpCode.FILEDATA_RECEIVED, buffer, iCurrentSendSize));
						
						sentFileSize += iCurrentSendSize;						
					}					
					SendFileInfo();
					
				}catch(FileNotFoundException e){
					e.printStackTrace();
				}catch(IOException e){
					DeleteFileList();
					e.printStackTrace();
				}finally{
					try{
						in.close();				
					}catch(IOException e){};
					in = null;
				}		
			}
		}
		
		public void DeleteFileList(){
			if(fileList==null)
				return;
			fileList.clear();		
			fileList = null;
		}
	}	
}
