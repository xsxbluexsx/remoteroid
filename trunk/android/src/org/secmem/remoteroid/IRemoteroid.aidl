package org.secmem.remoteroid;
import java.util.List;

interface IRemoteroid{
	String getConnectionStatus();
	boolean isConnected();
	void connect(String ipAddress, String password);
	void disconnect();
	void onNotificationCatched(String notificationText, long when);
	void onSendFile(in List<String> pathlist);
	
}