package org.secmem.remoteroid.network;

import java.io.*;

interface packetmakeable{
	public boolean SendPacket(int iOPCode, byte [] data, int length);
}

interface iFileSendable{
	public void SendFileInfo(File file);
	public boolean SendFileData(File file);
	}
