package org.secmem.remoteroid.network;

import java.text.ParseException;

import android.util.*;

/**
 * Represents remoteroid's packet system.</br>
 * A packet consisted of header and payload. 
 * @see PacketHeader
 * @author Taeho Kim
 *
 */
public class Packet {

	public static final int MAX_LENGTH = 4096;
	
	/**
	 * Represents packet data as String.
	 */
	private String packetStr;
	
	/**
	 * Packet's header data
	 */
	private PacketHeader header;
	/**
	 * Packet's actual data
	 */
	private static byte[] buffer = new byte[MAX_LENGTH];
	private byte[] payload;
	
	protected Packet(){
		// Do nothing on here
	}
	
	/**
	 * Generate packet.
	 * @param opCode an operation code
	 * @param data packet data
	 * @param dataLength length of data
	 */
	public Packet(int opCode, byte[] data, int dataLength){
		header = new PacketHeader(opCode, dataLength);
		payload = data;
	}
	
	/**
	 * Parse packet with given raw packet stream.
	 * @param rawPacket packet data in byte array
	 * @return Packet object that has received
	 * @throws ParseException failed to parse packet.
	 */
	public static Packet parse(byte[] rawPacket) throws ParseException{
		Packet packet = new Packet();		
		
		// Get header
		packet.setHeader(PacketHeader.parse(rawPacket));		
		
		int payloadLength = packet.getHeader().getPayloadLength();

		// Get data (payload)
		System.arraycopy(rawPacket, PacketHeader.LENGTH, buffer, 0, payloadLength);
		packet.setPayload(buffer);
		
		// Packet parsing has done.
		return packet;
	}
	
	/**
	 * Get packet as byte array.
	 * @return
	 */
	public byte[] asByteArray(){
//		byte[] packetData = new byte[header.getPacketLength()];
		
		if(header==null)
			throw new IllegalStateException("Packet header has not been set.");
				
		// Append header
		byte[] headerData = header.asByteArray();
		System.arraycopy(headerData, 0, buffer, 0, PacketHeader.LENGTH);
		
		// Append payload
		if(payload!=null)			
			System.arraycopy(payload, 0, buffer, PacketHeader.LENGTH, payload.length);
		return buffer;
	}
	
	/**
	 * Get packet in Stirng format.
	 * @return Packet in String format
	 */
	public String getPacketStr() {
		return packetStr;
	}

	/**
	 * Set packet's data in String format.
	 * @param mDataStr Packet data in String format. </br>Use <code>new String(byte[])</code> to convert byte[] into String.
	 */
	public void setPacketStr(String mDataStr) {
		this.packetStr = mDataStr;
	}

	/**
	 * Get packet's header.
	 * @return Packet's header
	 */
	public PacketHeader getHeader() {
		return header;
	}

	/**
	 * Set packet's header
	 * @param mHeader Packet's header
	 */
	public void setHeader(PacketHeader mHeader) {
		this.header = mHeader;
	}

	/**
	 * Get packet's actual data(payload).
	 * @return packet's payload
	 */
	public byte[] getPayload() {
		return payload;
	}

	/**
	 * Set packet's payload data
	 * @param payload a data to put into packet with
	 */
	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
	
	/**
	 * Get packet's opcode
	 * @return packet's opCode
	 */
	public int getOpcode(){
		return header.getOpCode();
	}

}
