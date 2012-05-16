package org.secmem.remoteroid.network;

public interface FilePacketListener extends PacketSendListener{
	public void onFileDataRequested();
	public void onFileInfoRequested();
	
	public void onReceiveFileData();
	public void onFileDataReceived();
	
	public void onReceiveFileInfo();
	public void onFileInfoReceived();
}
