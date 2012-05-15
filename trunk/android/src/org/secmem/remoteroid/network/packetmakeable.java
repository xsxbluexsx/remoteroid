package org.secmem.remoteroid.network;

import java.io.*;

interface packetmakeable{
	public void SendPacket(int iOPCode, byte [] data, int length) throws IOException;
}
