package org.secmem.remoteroid.network;

import java.io.*;

interface packetmakeable {
	//hyomin
	public void SendPacket(int iOPCode, byte [] data, int length) throws IOException;
}

interface packettest{
	public void sdfsd();
}
