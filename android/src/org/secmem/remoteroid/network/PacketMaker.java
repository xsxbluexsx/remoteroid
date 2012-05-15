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
	 * 
	 */
	public void SendPacket(int iOPCode, byte [] data, int length) throws IOException{	
			int iTotalSize = length + CONS.HEADERSIZE;
			// calculate size of header and data, sum it into totalsize
			
			strHeader.replace(0, CONS.OPCODESIZE, String.format("%2d", iOPCode));
			strHeader.replace(CONS.OPCODESIZE, CONS.HEADERSIZE, String.format("%4d", iTotalSize));
			// make packet (opcode 2byte + totalSize 4byte)			

			byte [] header = strHeader.toString().getBytes();
			System.arraycopy(header, 0, packet, 0, header.length);			
			if(data != null)
				System.arraycopy(data, 0, packet, header.length, length);

			try{
				out.write(packet, 0, iTotalSize);				
			}catch(IOException e){
				// disconnected from server
				Log.i("exception", "packet maker : "+e.getMessage());
				throw e;
			}			
	}	
}
