package org.secmem.remoteroid.network;

import java.io.*;
import java.net.*;


/**
 * Send screen shot data to host (UDP socket)
 * @author ssm
 */
public class FrameUdpSender{
	
	private static final int UDP_PORT = 50001;
	private static final int MAXDATASIZE = 500;
	private String ipAddr;
	private InetAddress serverAddress;
	private DatagramSocket datagramSocket;
	private byte[] sendBuffer = new byte[MAXDATASIZE];
	
	public FrameUdpSender(String ipAddr){
		this.ipAddr = ipAddr;		
	}
	
	public void connectUdpSocket() throws IOException{		
		datagramSocket = new DatagramSocket();
		serverAddress = InetAddress.getByName(ipAddr);
	}
	
	private void sendUdp(Packet packet) throws IOException{
		DatagramPacket out = new DatagramPacket(packet.asByteArray(),			
				packet.getHeader().getPacketLength(), serverAddress, UDP_PORT);
		
		datagramSocket.send(out);
	}
	
	public void sendFrameBuffer(byte[] jpgData) throws IOException{
		int jpgTotalSize = jpgData.length;
		int transmittedSize = 0;
		
		//First send jpg size information to host
		byte [] jpgSizeInfo = String.valueOf(jpgTotalSize).getBytes();		
		Packet jpgInfoPacket = new Packet(PacketHeader.OpCode.JPGINFO_SEND, jpgSizeInfo, jpgSizeInfo.length);				
		sendUdp(jpgInfoPacket);
		
		//Next send jpg data to host
		while(jpgTotalSize > transmittedSize){
			int CurTransSize = (jpgTotalSize-transmittedSize) > MAXDATASIZE ? 
					MAXDATASIZE : (jpgTotalSize-transmittedSize);
			System.arraycopy(jpgData, transmittedSize, sendBuffer, 0, CurTransSize);
			transmittedSize += CurTransSize;
			
			Packet jpgDataPacket = new Packet(PacketHeader.OpCode.JPGDATA_SEND, sendBuffer, CurTransSize);
			sendUdp(jpgDataPacket);
		}		
	}
}
