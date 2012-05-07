package org.secmem.remoteroid.network;

import java.io.*;
import java.net.*;

import android.util.*;

public class NetworkModule {
	private static NetworkModule 	netModInstance 	= null;
	private Socket 					socket			= null;
	private String 					strIP 			= null;
	private int 					iPortNum 		= CONS.HOST.PORT;
	private OutputStream 			out 			= null;
	private InputStream 			in 				= null;
	private packetmakeable 			packetMaker 	= null;
	private iFileSendable 			fileSender		= null;
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
	 * @throws IOException			(알 수 없는 오류)
	 * @throws UnknownHostException  (IP, PORT번호 확인 요망)
	 * ip와 port번호를 넘겨주면 소켓 연결을 하고 바이트스트림을 얻는다.
	 */
	public void ConnectSocket(String strIP) throws IOException, UnknownHostException{
		this.strIP = strIP;	
		socket = new Socket();
		
		socket.connect(new InetSocketAddress(strIP, iPortNum));
		
		out = socket.getOutputStream();
		in = socket.getInputStream();
		
		packetMaker = new PacketMaker(out);
		fileSender = new FileSender(packetMaker);
		packetReceiver = new PacketReceiver(in);

		Thread thread = new Thread(packetReceiver);
		thread.start();
	}	
	
	public void SendFileInfo(File file){		
		try {
			fileSender.SendFileInfo(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i("exception", "SendFileInfo : "+e.getMessage());
		}		
	}
	
	public void SendFileData(File file){		
		try {
			fileSender.SendFileData(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i("exception", "SendFileData : "+e.getMessage());
		}		
	}
	
	
	/**
	 * 연결 종료시 호출해야함
	 */
	public void CloseSocket(){
		try {
			Log.i("qqq", "접속 종료");
			out.close();
			in.close();
			socket.close();			
		} catch (IOException e) {
		}
		strIP = null;		
	}
}
