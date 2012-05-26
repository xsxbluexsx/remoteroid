package org.secmem.remoteroid.fragment;

public interface ConnectionStateListener {
	public void onConnectRequested(String ipAddress, String password);
	public void onConnectionCanceled();
	public void onConnectionFailed();
	public void onConnected(String ipAddress);
	public void onConnectionInterrupted();
	public void onDisconnectRequested();
	public void onDisconnected();
	public void onDriverInstalled();
}
