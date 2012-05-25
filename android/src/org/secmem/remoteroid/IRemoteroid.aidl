package org.secmem.remoteroid;
import java.util.List;

interface IRemoteroid{
	String getConnectionStatus();
	void connect(String ipAddress, String password);
	void disconnect();
	void onNotificationCatched(String notificationText, long when);
	void onReceiveCall(String displayedName, String number, long when);
	void onReceiveSMS(String displayedName, String number, String body, long when);
	void onSMSSent(String displayedName, String phoneNumber, long when);
	void onSendFile(in List<String> pathlist);
	boolean isTransmitterConnected();
}