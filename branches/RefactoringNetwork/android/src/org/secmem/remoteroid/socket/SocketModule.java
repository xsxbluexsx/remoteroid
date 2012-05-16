package org.secmem.remoteroid.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.util.Log;

public class SocketModule {
	
	public Socket socket;
	public OutputStream outputStream;
	public InputStream	inputStream;
	public SocketAddress socketAddr;

	public SocketModule(){
		socket = new Socket();	
	}
	
	public SocketModule(String addr, int port){
		socketAddr = new InetSocketAddress(addr, port);
		socket = new Socket();
	}
	public void SetSocket(String addr, int port) throws IOException{
		socketAddr = new InetSocketAddress(addr, port);
		socket.connect(socketAddr);
		outputStream = socket.getOutputStream();
		inputStream = socket.getInputStream();
	}
	
	public int SendPacket(int OPCode, byte [] data, int dataSize) {
		try {
			Log.i("packet","start");
			String msg;
			String strOPCode = String.format("%2d", OPCode);
			
			int totalSize = 6+dataSize;
			String strDataSize = String.format("%4d", totalSize);
			
			msg = strOPCode + strDataSize;
			byte [] header = msg.getBytes();
			byte [] packet = new byte[totalSize];
			System.arraycopy(header, 0, packet, 0, header.length);
			System.arraycopy(data, 0, packet, header.length,dataSize);
			Log.i("packet","sendready");
			outputStream.write(packet);
			Log.i("packet","OK");
			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public int SendPacket(int OPCode, int data, int dataSize) {
		try {
			Log.i("packet","start");
			String msg;
			String strOPCode = String.format("%2d", OPCode);
			
			int totalSize = 6+dataSize;
			String strDataSize = String.format("%4d", totalSize);
			
			msg = strOPCode + strDataSize;
			byte [] header = msg.getBytes();
			byte [] packet = new byte[totalSize];
			Log.i("packet","arraycopy ready");
			System.arraycopy(header, 0, packet, 0, header.length);
			System.arraycopy(data, 0, packet, header.length,dataSize);
			Log.i("packet","sendready");
			outputStream.write(packet);
			Log.i("packet","OK");
			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public void Close(){
		try {
			if(socket != null) socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
