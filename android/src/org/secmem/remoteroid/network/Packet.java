package org.secmem.remoteroid.network;

import java.text.ParseException;
import java.util.Arrays;


public class Packet {
	private static final int HEADER_LENGTH = 6;
	private static final int OPCODE_LENGTH = 2;
	private static final int DATASIZE_LENGTH = 4;
	
	public static final int MAX_PACKET_LENGTH = 4096;
	public static final int MAX_FILENAME_LENGTH = 100;
	public static final int MAX_FILESIZE_LENGTH = 100;
	
	class OpCode{
		public static final int SEND_FILEINFO = 1;
		public static final int SEND_FILEDATA = 2;
		public static final int REQUEST_FILEDATA = 5;
		public static final int READY_TO_SEND = 6;
		public static final int REQUEST_FILEINFO = 7;
	}
	
	private int opCode;
	private byte[] data;
	
	public Packet parse(byte[] rawPacket) throws ParseException{
		Packet packet = new Packet();
		
		byte[] header = new byte[HEADER_LENGTH];
		byte[] data = new byte[MAX_PACKET_LENGTH-HEADER_LENGTH];
		
		// Get header
		System.arraycopy(rawPacket, 0, header, 0, HEADER_LENGTH);
		
		// Get data
		System.arraycopy(rawPacket, HEADER_LENGTH, data, 0, rawPacket.length-HEADER_LENGTH);

		return null;
	}
	
	private static byte[] generateHeader(int opCode, int dataLength){
		if(opCode==-1)
			throw new IllegalStateException();
		return String.format("%2d%4d", opCode, dataLength+HEADER_LENGTH).getBytes();
	}
	
	/**
	 * Generate packet as byte array.
	 * @param opCode an operation code
	 * @param data packet data
	 * @param dataLength length of data
	 * @return Packet formed as byte[]
	 */
	public static byte[] generatePacket(int opCode, byte[] data, int dataLength){
		if(data==null)
			throw new IllegalStateException();
		
		byte[] payload = new byte[MAX_PACKET_LENGTH];
		byte[] header = generateHeader(opCode, dataLength);
		
		System.arraycopy(header, 0, payload, 0, header.length);
		System.arraycopy(data, 0, payload, HEADER_LENGTH, dataLength);
		
		return payload;
	}	

}
