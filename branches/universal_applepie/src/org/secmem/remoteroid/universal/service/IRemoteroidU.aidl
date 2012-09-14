package org.secmem.remoteroid.universal.service;

interface IRemoteroidU{
	boolean isConnected();
	void connect(in String ipAddress);
	void disconnect();
	void onNotification(in int notificationType, in String[] args);
}