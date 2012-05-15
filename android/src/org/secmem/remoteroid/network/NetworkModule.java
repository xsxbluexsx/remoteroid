package org.secmem.remoteroid.network;

import java.io.*;
import java.net.*;
import java.util.*;

import android.util.*;

public class NetworkModule {
	private static NetworkModule 	netModInstance 	= null;
	private Socket 					socket			= null;
	private String 					strIP 			= null;
	private int 					iPortNum 		= CONS.HOST.PORT;
	private OutputStream 			out 			= null;
	private InputStream 			in 				= null;
	private packetmakeable 			packetMaker 	= null;
	private FileSender				fileSender		= null;
	private PacketReceiver			packetReceiver 	= null;
	
	
	private NetworkModule(){		
	}
	
	
	public static NetworkModule getInstance(){
		if(netModInstance == null)
			netModInstance = new NetworkModule();
		return netModInstance;
	}
	
	/**
	 * 
	 * @param strIP
	 * @param iPortNum
	 * @throws IOException			(������� �ㅻ�)
	 * @throws UnknownHostException  (IP, PORT踰�� ��� ���)
	 */
	public void ConnectSocket(String strIP) throws IOException, UnknownHostException{
		this.strIP = strIP;	
		socket = new Socket();
		
		socket.connect(new InetSocketAddress(strIP, iPortNum));
		
		out = socket.getOutputStream();
		in = socket.getInputStream();
		
		packetMaker = new PacketMaker(out);
		fileSender = new FileSender(packetMaker);
		packetReceiver = new PacketReceiver(in, fileSender);

		Thread thread = new Thread(packetReceiver);
		thread.start();
	}	
	
	public void SendFileList(ArrayList<File> fileList){		
		try {
			fileSender.SendFileList(fileList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			fileSender.DeleteFileList();
			Log.i("exception", "SendFileInfo : "+e.getMessage());
		}		
	}	

	public void SendPacket(int iOPCode, byte [] data, int length){
		try{
			packetMaker.SendPacket(iOPCode, data, length);
		}catch(IOException e){
			Log.i("exception", "NetworkModule sendpacket");
		}
	}
	
	/**
	 * �곌껐 醫�����몄��댁���
	 */
	public void CloseSocket(){
		try {
			Log.i("qqq", "��� 醫��");
			
			out.close();
			in.close();
			socket.close();			
		} catch (IOException e) {
		}
		strIP = null;		
	}
}
