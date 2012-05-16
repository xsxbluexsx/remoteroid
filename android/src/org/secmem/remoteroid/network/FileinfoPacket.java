package org.secmem.remoteroid.network;

import java.io.File;

import org.secmem.remoteroid.network.PacketHeader.OpCode;

public class FileinfoPacket extends Packet{
	
	public static final int MAX_FILENAME_LENGTH = 100;
	public static final int MAX_FILESIZE_LENGTH = 100;
	private static final int MAX_LENGTH = MAX_FILENAME_LENGTH+MAX_FILESIZE_LENGTH;
	
	protected FileinfoPacket(){
	}
	
	/**
	 * Generates file info packet.
	 * @param file File to send with
	 */
	public FileinfoPacket(File file){
		if(file==null)
			throw new IllegalStateException();
		
		byte[] payload = new byte[MAX_LENGTH];
		
		byte[] fileName = file.getName().getBytes();
		byte[] fileSize = String.valueOf(file.length()).getBytes();
		
		System.arraycopy(fileName, 0, payload, 0, fileName.length);
		System.arraycopy(fileSize, 0, payload, MAX_FILENAME_LENGTH, fileSize.length);
		
		setHeader(new PacketHeader(OpCode.FILEINFO_RECEIVED, payload.length));
		setPayload(payload);
	}	
}
