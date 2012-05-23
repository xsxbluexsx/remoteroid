package org.secmem.remoteroid;

interface IRemoteroid{
	boolean getConnectionStatus();
	void connect(String ipAddress, String password);
	void disconnect();
	void onNotificationCatched(String notificationText, long when);
	void onReceiveCall(String displayedName, String number, long when);
	void onReceiveSMS(String displayedName, String number, String body, long when);
	void onSMSSent(String displayedName, String phoneNumber, long when);
}