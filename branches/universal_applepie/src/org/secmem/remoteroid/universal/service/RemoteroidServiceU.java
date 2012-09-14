package org.secmem.remoteroid.universal.service;

import org.secmem.remoteroid.intent.RemoteroidIntent;
import org.secmem.remoteroid.lib.net.CommandPacket;
import org.secmem.remoteroid.lib.net.CommandPacket.CommandFactory;
import org.secmem.remoteroid.lib.net.ConnectionManager;
import org.secmem.remoteroid.lib.net.ConnectionManager.ServerCommandListener;
import org.secmem.remoteroid.lib.net.ConnectionManager.ServerConnectionListener;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class RemoteroidServiceU extends Service {
	
	private ConnectionManager connManager;
	
	private IBinder binder = new IRemoteroidU.Stub() {

		@Override
		public boolean isCommandConnected() throws RemoteException {
			return connManager.isCommandConnected();
		}

		@Override
		public void connectCommand(String ipAddress) throws RemoteException {
			connManager.connectCommand(ipAddress);
		}
		
		@Override
		public boolean isScreenConnected() throws RemoteException {
			return connManager.isScreenConnected();
		}

		@Override
		public void connectScreen(String ipAddress) throws RemoteException {
			connManager.connectScreen(ipAddress);
		}
		
		@Override
		public void disconnectScreen() throws RemoteException {
			connManager.disconnectScreen();
		}

		@Override
		public void disconnect() throws RemoteException {
			connManager.disconnect();
		}

		@Override
		public void onNotification(int notificationType, String[] args)
				throws RemoteException {
			CommandPacket command = CommandFactory.notification(notificationType, args);
			connManager.sendCommand(command);
		}

	};
	
	@Override
	public IBinder onBind(Intent intent) {
		connManager = new ConnectionManager();
		connManager.setServerCommandListener(commListener);
		connManager.setServerConnectionListener(connListener);
		
		return binder;
	}
	
	private ServerConnectionListener connListener = new ServerConnectionListener(){

		@Override
		public void onCommandConnected(String ipAddress) {
			sendBroadcast(
					new Intent(RemoteroidIntent.ACTION_CONNECTED)
					.putExtra(RemoteroidIntent.EXTRA_IP_ADDESS, ipAddress));
		}
		
		@Override
		public void onScreenConnected(String ipAddress) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onFailed() {
			sendBroadcast(new Intent(RemoteroidIntent.ACTION_CONNECTION_FAILED));
		}


		
	};
	
	private ServerCommandListener commListener = new ServerCommandListener(){

		@Override
		public void onCommand(CommandPacket command) {
			switch(command.getCommand()){
			
			}
		}

		@Override
		public void onDisconnected() {
			sendBroadcast(new Intent(RemoteroidIntent.ACTION_DISCONNECTED));
		}
		
	};
	
}
