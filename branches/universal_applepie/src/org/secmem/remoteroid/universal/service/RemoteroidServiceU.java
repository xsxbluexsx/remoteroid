package org.secmem.remoteroid.universal.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.intent.RemoteroidIntent;
import org.secmem.remoteroid.lib.net.CommandPacket;
import org.secmem.remoteroid.lib.net.CommandPacket.Command;
import org.secmem.remoteroid.lib.net.CommandPacket.CommandFactory;
import org.secmem.remoteroid.lib.net.CommandPacket.Extra;
import org.secmem.remoteroid.lib.net.ConnectionManager;
import org.secmem.remoteroid.lib.net.ConnectionManager.ServerCommandListener;
import org.secmem.remoteroid.lib.net.ConnectionManager.ServerConnectionListener;
import org.secmem.remoteroid.lib.net.ScreenPacket;
import org.secmem.remoteroid.natives.InputHandler;
import org.secmem.remoteroid.universal.activity.MainU;
import org.secmem.remoteroid.universal.natives.FrameHandlerU;
import org.secmem.remoteroid.util.CommandLine;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class RemoteroidServiceU extends Service {
	private static final String TAG = "RemoteroidServiceU";
	
	private ConnectionManager connManager;
	private FrameHandlerU frameHandler;
	//private FrameHandler frameHandler;
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
			//frameHandler.acquireFrameBufferPermission();
			CommandLine.execAsRoot("chmod 664 /dev/graphics/fb0");
			CommandLine.execAsRoot("chmod 664 /dev/graphics/fb1");
			connManager.connectScreen();
		}
		
		@Override
		public void disconnectScreen() throws RemoteException {
			//frameHandler.revertFrameBufferPermission();
			CommandLine.execAsRoot("chmod 660 /dev/graphics/fb0");
			CommandLine.execAsRoot("chmod 660 /dev/graphics/fb1");
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

		@Override
		public void requestBroadcastConnectionState() throws RemoteException {
			if(connManager.isCommandConnected()){
				sendBroadcast(new Intent(RemoteroidIntent.ACTION_CONNECTED));
			}else{
				sendBroadcast(new Intent(RemoteroidIntent.ACTION_DISCONNECTED));
			}
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

	private ServerConnectionListener connListener = new ServerConnectionListener(){

		@Override
		public void onCommandConnected(String ipAddress) {
			connManager.listenCommandFromServer();
			showConnectionNotification(ipAddress);
			sendBroadcast(new Intent(RemoteroidIntent.ACTION_CONNECTED));
			
		}
		
		@Override
		public void onScreenConnected(String ipAddress) {
			// FIXME Do nothing until framebuffer issue is fixed
			/*
			screenSenderThread = new ScreenSenderThread();
			screenSenderThread.start();*/
		}

		@Override
		public void onFailed() {
			inputHandler.close();
			sendBroadcast(new Intent(RemoteroidIntent.ACTION_CONNECTION_FAILED));
		}

		@Override
		public void onScreenConnectionFailed() {
			Log.e(TAG, "Screen connection failed.");
			
		}

		@Override
		public void onScreenDisconnected() {
			// TODO Auto-generated method stub
			
		}

	};
	
	private ServerCommandListener commListener = new ServerCommandListener(){

		@Override
		public void onCommand(CommandPacket command) {
			switch(command.getCommand()){
			case Command.SCREEN_SERVER_READY:
				// Server established screen socket.
				// Now, client should send screen data to the server.
				connManager.connectScreen();
				break;
			
			case Command.REQUEST_DEVICE_INFO:
				// Server has requested device info.
				connManager.sendCommand(
						CommandFactory.sendDeviceInfo(frameHandler.getWidth(), frameHandler.getHeight()));
				
				break;
				
			case Command.TOUCH_DOWN:
				inputHandler.touchDown();
				break;
				
			case Command.TOUCH_SETPTR:
				inputHandler.touchSetPtr(
						command.getIntExtra(Extra.KEY_TOUCH_X), 
						command.getIntExtra(Extra.KEY_TOUCH_Y));
				break;
				
			case Command.TOUCH_UP:
				inputHandler.touchUp();
				break;
				
			case Command.KEY_DOWN:
				inputHandler.keyDown(command.getIntExtra(Extra.KEY_KEYCODE));
				break;
				
			case Command.KEY_UP:
				inputHandler.keyUp(command.getIntExtra(Extra.KEY_KEYCODE));
				break;
				
			}
		}

		@Override
		public void onDisconnected() {
			dismissNotification();
			connManager.disconnect();
			sendBroadcast(new Intent(RemoteroidIntent.ACTION_DISCONNECTED));
		}
		
	};
	
	private class ScreenSenderThread extends Thread{
		
		public ScreenSenderThread(){
			//setDaemon(true);
		}
		
		@Override
		public void run(){
			
			frameHandler.acquireFrameBufferPermission();
			
			while(true){
				ScreenPacket packet = new ScreenPacket();
				//byte[] screen = frameHandler.getFrameStream().toByteArray();
				byte[] screen = frameHandler.readScreenBuffer();
				Bitmap bitmap = BitmapFactory.decodeByteArray(screen, 0, screen.length);
				FileOutputStream out;
				try {
					out = new FileOutputStream(new File(Environment.getExternalStorageDirectory(), "test.jpg"));
					bitmap.compress(CompressFormat.JPEG, 100, out);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				break;
				
				//Log.d(TAG, "size="+screen.length);
				/*packet.setImageBytes(screen);
				try{
					connManager.sendScreen(packet);
				}catch(IOException e){
					e.printStackTrace();
					connListener.onScreenDisconnected();
					break;
				}*/
			}
			
			frameHandler.revertFrameBufferPermission();
		}
	}
	
	@SuppressWarnings("deprecation")
	private void showConnectionNotification(String ipAddress){
		Notification notification = new Notification();
		PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(this, MainU.class), 0);
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
