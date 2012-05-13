package org.secmem.remoteroid.network;

import java.io.*;

import android.util.*;

public class PacketMaker implements packetmakeable{	
	private byte [] 			packet 		= new byte[CONS.MAXPACKETSIZE];
	private	OutputStream 		out 		= null;	
	private StringBuilder		strHeader 	= new StringBuilder(CONS.HEADERSIZE);
	
	
	public PacketMaker(OutputStream out) {
		// TODO Auto-generated constructor stub
		this.out =out;		
	}
	
	/**
	 * opcode 와 전송할 data를 완성된 패킷으로 조립 후에 전송
	 */
	public void SendPacket(int iOPCode, byte [] data, int length) throws IOException{	
			int iTotalSize = length + CONS.HEADERSIZE;
			//헤더 싸이즈와 데이터 싸이즈를 더해 totalsize 계산
			
			strHeader.replace(0, CONS.OPCODESIZE, String.format("%2d", iOPCode));
			strHeader.replace(CONS.OPCODESIZE, CONS.HEADERSIZE, String.format("%4d", iTotalSize));
			//프로토콜에 따라 헤더 조립 (2자리수 opcode + 4자리수 totalsize)			

			byte [] header = strHeader.toString().getBytes();
			System.arraycopy(header, 0, packet, 0, header.length);			
			if(data != null)
				System.arraycopy(data, 0, packet, header.length, length);

			try{
				out.write(packet, 0, iTotalSize);				
			}catch(IOException e){
				//서버와 접속이 끈겼을 경우
				Log.i("exception", "packet maker : "+e.getMessage());
				throw e;
			}			
	}	
}
