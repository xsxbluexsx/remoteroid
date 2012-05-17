package org.secmem.remoteroid.network;

import java.io.*;

public interface FilePacketListener extends PacketSendListener{
	
	public void onReceiveFileData(Packet packet);	
	public void onReceiveFileInfo(Packet packet);
	
	
	public void CloseFile();
}
