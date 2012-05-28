package org.secmem.remoteroid.network;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.secmem.remoteroid.network.PacketHeader.OpCode;

import android.util.Log;


/**
 * Send screen shot data to host (UDP socket)
 * @author ssm
 */
public class ScreenSender extends PacketSender{
	
	private static final int UDP_PORT = 50001;
	private static final int MAXDATASIZE = 4090;
	private String ipAddr;
	private InetAddress serverAddress;
	private DatagramSocket datagramSocket;
	private byte[] sendBuffer = new byte[MAXDATASIZE];
	
	public ScreenSender(String ipAddr, OutputStream out){
		super(out);
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
	
	public void screenTransmission(byte[] jpgData) throws IOException{
		int jpgTotalSize = jpgData.length;
		int transmittedSize = 0;		
		
		//First send jpg size information to host
		byte [] jpgSizeInfo = String.valueOf(jpgTotalSize).getBytes();		
		Packet jpgInfoPacket = new Packet(OpCode.JPGINFO_SEND, jpgSizeInfo, jpgSizeInfo.length);				
		//sendUdp(jpgInfoPacket);
		send(jpgInfoPacket);
		
		Log.i("qwe", ""+jpgTotalSize);
		//Next send jpg data to host
		while(jpgTotalSize > transmittedSize){
			int CurTransSize = (jpgTotalSize-transmittedSize) > MAXDATASIZE ? 
					MAXDATASIZE : (jpgTotalSize-transmittedSize);
			System.arraycopy(jpgData, transmittedSize, sendBuffer, 0, CurTransSize);
			transmittedSize += CurTransSize;
			
			Packet jpgDataPacket = new Packet(OpCode.JPGDATA_SEND, sendBuffer, CurTransSize);
			//sendUdp(jpgDataPacket);
			send(jpgDataPacket);
		}		
	}
}
