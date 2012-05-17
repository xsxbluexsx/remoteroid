package org.secmem.remoteroid.network;

import java.io.*;

public interface FilePacketListener extends PacketSendListener{
	
	public void onReceiveFileData();	
	public void onReceiveFileInfo();	
}
