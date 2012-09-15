package org.secmem.remoteroid.universal.service;

import java.io.IOException;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.activity.Main;
import org.secmem.remoteroid.intent.RemoteroidIntent;
import org.secmem.remoteroid.lib.net.CommandPacket;
import org.secmem.remoteroid.lib.net.CommandPacket.Command;
import org.secmem.remoteroid.lib.net.CommandPacket.CommandFactory;
import org.secmem.remoteroid.lib.net.ConnectionManager;
import org.secmem.remoteroid.lib.net.ConnectionManager.ServerCommandListener;
import org.secmem.remoteroid.lib.net.ConnectionManager.ServerConnectionListener;
import org.secmem.remoteroid.natives.InputHandler;
import org.secmem.remoteroid.universal.natives.FrameHandlerU;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class RemoteroidServiceU extends Service {
	private static final String TAG = "RemoteroidServiceU";
	
	private ConnectionManager connManager;
	private FrameHandlerU frameHandler;
	private InputHandler inputHandler;
	private ScreenSenderThread screenSenderThread;
	
	private IBinder binder = new IRemoteroidU.Stub() {

		@Override
		public boolean isCommandConnected() throws RemoteException {
			return connManager.isCommandConnected();
		}

		@Override
		public void connectCommand(final String ipAddress) throws RemoteException {

			new AsyncTask<Void, Void, Boolean>(){

				@Override
				protected Boolean doInBackground(Void... params) {
					boolean isOpened = inputHandler.open();
					if(!isOpened){
						inputHandler.grantUinputPermission();
						isOpened = inputHandler.openInputDeviceWithoutPermission();
					}
					return isOpened;
				}
				
				@Override
				protected void onPostExecute(Boolean result){
					super.onPostExecute(result);
					if(result==true){
						connManager.connectCommand(ipAddress);
					}else{
						Log.e(TAG, "Cannot open uinput.");
					}
				}
				
			}.execute();
			
		}
		
		@Override
		public boolean isScreenConnected() throws RemoteException {
			return connManager.isScreenConnected();
		}

		@Override
		public void connectScreen(final String ipAddress) throws RemoteException {
			frameHandler.acquireFrameBufferPermission();
			connManager.connectScreen(ipAddress);
		}
		
		@Override
		public void disconnectScreen() throws RemoteException {
			frameHandler.revertFrameBufferPermission();
			connManager.disconnectScreen();
		}

		@Override
		public void disconnect() throws RemoteException {
			new AsyncTask<Void, Void, Void>(){

				@Override
				protected Void doInBackground(Void... params) {
					inputHandler.close();
					return null;
				}
				
				@Override
				protected void onPostExecute(Void result){
					super.onPostExecute(result);
					connManager.disconnect();
				}
				
			}.execute();
			
		}

		@Override
		public void onNotification(int notificationType, String[] args)
				throws RemoteException {
			CommandPacket command = CommandFactory.notification(notificationType, args);
			connManager.sendCommand(command);
		}

	};
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate()");
		frameHandler = new FrameHandlerU(this);
		inputHandler = new InputHandler(this);
		
		connManager = new ConnectionManager();
		connManager.setServerCommandListener(commListener);
		connManager.setServerConnectionListener(connListener);
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind()");
		if(connManager.isCommandConnected()){
			sendBroadcast(new Intent(RemoteroidIntent.ACTION_CONNECTED));
		}else{
			sendBroadcast(new Intent(RemoteroidIntent.ACTION_DISCONNECTED));
		}
		return binder;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy()");
	}

	private ServerConnectionListener connListener = new ServerConnectionListener(){

		@Override
		public void onCommandConnected(String ipAddress) {
			connManager.listenCommandFromServer();
			showConnectionNotification(ipAddress);
			sendBroadcast(new Intent(RemoteroidIntent.ACTION_CONNECTED));
			
		}
		
		@Override
		public void onScreenConnected(String ipAddress) {
			
		}

		@Override
		public void onFailed() {
			inputHandler.close();
			sendBroadcast(new Intent(RemoteroidIntent.ACTION_CONNECTION_FAILED));
		}


		
	};
	
	private ServerCommandListener commListener = new ServerCommandListener(){

		@Override
		public void onCommand(CommandPacket command) {
			switch(command.getCommand()){
			case Command.SCREEN_SERVER_READY:
				// Server established screen socket.
				// Now, client should send screen data to the server.
				screenSenderThread = new ScreenSenderThread();
				screenSenderThread.start();
				break;
			
			case Command.REQUEST_DEVICE_INFO:
				// Server has requested device info.
				connManager.sendCommand(
						CommandFactory.sendDeviceInfo(frameHandler.getWidth(), frameHandler.getHeight()));
				break;
				
			case Command.TOUCH_DOWN:
				
				break;
				
			case Command.TOUCH_UP:
				
				break;
				
			case Command.KEY_DOWN:
				
				break;
				
			case Command.KEY_UP:
				
				break;
				
			}
		}

		@Override
		public void onDisconnected() {
			System.out.println("Command onDisconnected()");
			dismissNotification();
			sendBroadcast(new Intent(RemoteroidIntent.ACTION_DISCONNECTED));
		}
		
	};
	
	private class ScreenSenderThread extends Thread{
		
		public ScreenSenderThread(){
			setDaemon(true);
		}
		
		@Override
		public void run(){
			while(true){
				byte[] screen = frameHandler.readScreenBuffer();
				try{
					connManager.sendScreen(screen);
				}catch(IOException e){
					e.printStackTrace();
					break;
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private void showConnectionNotification(String ipAddress){
		Notification notification = new Notification();
		PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(this, Main.class), 0);
		notification.icon = R.drawable.ic_launcher;
		notification.tickerText = String.format(getString(R.string.connected_to_s), ipAddress);
		notification.when = System.currentTimeMillis();
		notification.setLatestEventInfo(getApplicationContext(), getString(R.string.app_name), String.format(getString(R.string.connected_to_s), ipAddress), intent);
		
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		NotificationManager notifManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		notifManager.notify(0, notification);
	}
	
	private void dismissNotification(){
		NotificationManager notifManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		notifManager.cancelAll();
	}
	
}
