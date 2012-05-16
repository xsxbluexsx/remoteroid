package org.secmem.remoteroid.network;

import java.text.ParseException;

/**
 * Represents packet's header data.</br>
 * Header is consisted of <code>{@link OpCode opCode}</code> and {@link #payloadLength PayloadLength}.
 * @author Taeho Kim
 *
 */
public class PacketHeader {
	
	public static final int LENGTH = 6;
	public static final int OPCODE_LENGTH = 2;
	public static final int PAYLOAD_LENGTH = 4;
	
	/**
	 * An operation code for {@link PacketHeader}.
	 * @author Taeho Kim
	 *
	 */
	public class OpCode{
		public static final int INVALID = -1;
		public static final int FILEINFO_RECEIVED = 1;
		public static final int FILEDATA_RECEIVED = 2;
		public static final int FILEDATA_REQUESTED = 5;
		public static final int READY_TO_SEND = 6;
		public static final int FILEINFO_REQUESTED = 7;
	}
	
	/**
	 * An operation code.
	 */
	private int opCode = OpCode.INVALID;
	
	/**
	 * Data's length in packet
	 */
	private int payloadLength = 0;
	
	private PacketHeader(){
	}
	
	public PacketHeader(int opCode, int dataLength){
		this.opCode = opCode;
		this.payloadLength = dataLength;
	}
	
	@Override
	public String toString(){
		return String.format("%2d%4d", opCode, payloadLength+LENGTH);
	}
	
	/**
	 * Convert packet header into byte array.
	 * @return
	 */
	public byte[] asByteArray(){
		return toString().getBytes();
	}
	
	/**
	 * Parse packet header from given packet header data.
	 * @param headerStr packet header's data in String format
	 * @return PacketHeader object of given packet header
	 * @throws ParseException given packet header is not valid
	 */
	public static PacketHeader parse(String headerStr) throws ParseException{
//		if(headerStr.length()!=LENGTH)
//			throw new ParseException("Invalid header length, should be 6 but "+headerStr.length(), 0);
		
		PacketHeader header = new PacketHeader();
		
		try{
			// Step 1. parse opCode
			header.setOpCode(Integer.parseInt(headerStr.substring(0, OPCODE_LENGTH).trim()));
		}catch(NumberFormatException e){
			throw new ParseException("Could not parse opcode with given header="+headerStr, 0);
		}
		
		try{
			// Step 2. parse dataLength
			header.setPayloadLength(Integer.parseInt(headerStr.substring(OPCODE_LENGTH, LENGTH).trim())-LENGTH);
		}catch(NumberFormatException e){
			throw new ParseException("Could not parse dataLength with given header="+headerStr, 2);
		}
		
		// Packet header parsing completed.
		return header;
	}
	
	public static PacketHeader parse(byte[] rawData) throws ParseException{
		return parse(new String(rawData));
	}

	/**
	 * Get an opCode in packet header.
	 * @return
	 */
	public int getOpCode() {
		return opCode;
	}

	/**
	 * Set an opCode.
	 * @param opCode opCode to set
	 * @see OpCode
	 */
	public void setOpCode(int opCode) {
		this.opCode = opCode;
	}

	/**
	 * Get payload's data length.
	 * @return payload's data length
	 */
	public int getPayloadLength() {
		return payloadLength;
	}
	
	/**
	 * Get packet's total length, including header size
	 * @return total packet size
	 */
	public int getPacketLength() {
		return LENGTH+payloadLength;
	}

	/**
	 * Set payload's data length
	 * @param payloadLength payload's data length
	 */
	public void setPayloadLength(int payloadLength) {
		this.payloadLength = payloadLength;
	}

}
