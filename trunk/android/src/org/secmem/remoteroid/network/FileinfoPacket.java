package org.secmem.remoteroid.network;

import java.io.File;

import org.secmem.remoteroid.network.Packet.OpCode;

public class FileinfoPacket{
	
	private static final int MAX_FILEINFO_LENGTH = Packet.MAX_FILENAME_LENGTH+Packet.MAX_FILESIZE_LENGTH;
	
	/**
	 * Generates file info packet.
	 * @param file
	 * @return
	 */
	public static byte[] getPacket(File file){
		if(file==null)
			throw new IllegalStateException();
		
		byte[] payload = new byte[MAX_FILEINFO_LENGTH];
		
		byte[] fileName = file.getName().getBytes();
		byte[] fileSize = String.valueOf(file.length()).getBytes();
		
		System.arraycopy(fileName, 0, payload, 0, fileName.length);
		System.arraycopy(fileSize, 0, payload, Packet.MAX_FILENAME_LENGTH, fileSize.length);
		
		return Packet.generatePacket(OpCode.SEND_FILEINFO, payload, payload.length);
	}	
}
