package org.secmem.remoteroid.network;

import java.io.*;

import android.util.*;

public class PacketReceiver implements Runnable{
	private byte [] 		recvBuffer 			= new byte[CONS.MAXPACKETSIZE * 2];
	private byte [] 		header 				= new byte[CONS.HEADERSIZE];
	private byte [] 		data 				= new byte[CONS.MAXPACKETSIZE-CONS.HEADERSIZE];
	private byte [] 		bPacketSize 		= new byte[CONS.PACKETSIZE];
	private byte [] 		bOPCode 			= new byte[CONS.OPCODESIZE];
	private FileReceiver	fileReceiver		= new FileReceiver();

	private int 		iCurrentBufferPos 	= 0;
	private InputStream in 					= null;
	
	public PacketReceiver(InputStream in){
		this.in = in;
	}

	
	private int ByteToInt(byte [] data){
		int result = 0;
		for(int i=0; i<data.length; i++){
			if(data[i] == ' ')
				continue;
			result = result * 10 + (data[i]-'0');
		}
		return result;
	}
	
	/**
	 * 패킷 헤더로부터 가변길이 패킷의 크기를 구한다
	 * @return
	 */
	private int GetPacketSize(byte [] packet){
		 System.arraycopy(packet, CONS.OPCODESIZE, bPacketSize, 0, CONS.PACKETSIZE);
				 
		 return ByteToInt(bPacketSize);
	}
	
	private int GetOPCode(byte [] packet){
		System.arraycopy(packet, 0, bOPCode, 0, CONS.OPCODESIZE);
		
		return ByteToInt(bOPCode);
	}
	
	/**
	 * input stream에서 수신 버퍼로 최대 4096씩 read 
	 * 연결 종료시 IOException 발생
	 */
	private int RecvBuffer() throws IOException{		
		int iRecvLen = in.read(recvBuffer, iCurrentBufferPos, CONS.MAXPACKETSIZE);		
		
		if(iRecvLen > 0){
			iCurrentBufferPos += iRecvLen;			
		}
		return iRecvLen;
	}
	
	private boolean GetPacket(){
		
		//헤더 크기만큼 수신하지 못하면 불완전한 패킷
		if(iCurrentBufferPos < CONS.HEADERSIZE){			
			return false;
		}
		
		int iPacketSize = GetPacketSize(recvBuffer);		
		
		//헤더에 기록된 패킷의 총 크기보가 수신받지 못하면...
		if(iPacketSize > iCurrentBufferPos){
			return false;
		}		
		
		//수신 버퍼에서 헤더와 data 복사		
		System.arraycopy(recvBuffer, 0, header, 0, CONS.HEADERSIZE);		
		System.arraycopy(recvBuffer, CONS.HEADERSIZE, data, 0, iPacketSize-CONS.HEADERSIZE);
		iCurrentBufferPos -= iPacketSize;		
		System.arraycopy(recvBuffer, iPacketSize, recvBuffer, 0, iCurrentBufferPos);		
		return true;
	}
	
	public void run(){
		while(true){
			try {
				int iRecvLen = RecvBuffer();
				if(iRecvLen < 0){
					//접속 종료
					Log.i("exception", "<0");
					break;
				}
				while(GetPacket()){
					int iOPCode = GetOPCode(header);
					int iPacketSize = GetPacketSize(header);	
					
					switch(iOPCode){
					case CONS.OPCODE.OP_SENDFILEINFO:
						fileReceiver.RecvFileInfo(data);						
						break;
					case CONS.OPCODE.OP_SENDFILEDATA:
						fileReceiver.RecvFileData(data, iPacketSize);
						break;						
					}
				}
			} catch (IOException e) {	
				Log.i("exception", "recv thread : "+e.getMessage());
				NetworkModule module = NetworkModule.getInstance();
				module.CloseSocket();
				break;
			}
		}
	}	
}
