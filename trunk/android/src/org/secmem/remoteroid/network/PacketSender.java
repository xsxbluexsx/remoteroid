package org.secmem.remoteroid.network;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Provides method for sending packet via stream
 * @author Hyomin Oh
 */
public class PacketSender {
	
	private OutputStream sendStream;	
	
	public PacketSender(OutputStream stream){
		this.sendStream = stream;		
	}
	
	public void setOutputStream(OutputStream stream){
		this.sendStream = stream;
	}
	
	public void send(Packet packet) throws IOException{
		//get packet size for transmission
		int packetSize = packet.getHeader().getPacketLength();
		sendStream.write(packet.asByteArray(), 0, packetSize);		
	}
}
