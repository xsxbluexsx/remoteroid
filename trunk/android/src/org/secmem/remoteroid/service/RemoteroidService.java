/*
 * Remoteroid - A remote control solution for Android platform, including handy file transfer and notify-to-PC.
 * Copyright (C) 2012 Taeho Kim(jyte82@gmail.com), Hyomin Oh(ohmnia1112@gmail.com), Hongkyun Kim(godgjdgjd@nate.com), Yongwan Hwang(singerhwang@gmail.com)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package org.secmem.remoteroid.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.secmem.remoteroid.IRemoteroid;
import org.secmem.remoteroid.R;
import org.secmem.remoteroid.activity.Main;
import org.secmem.remoteroid.data.RDSmsMessage;
import org.secmem.remoteroid.intent.RemoteroidIntent;
import org.secmem.remoteroid.natives.InputHandler;
import org.secmem.remoteroid.network.FileTransmissionListener;
import org.secmem.remoteroid.network.Tranceiver;
import org.secmem.remoteroid.network.VirtualEventListener;
import org.secmem.remoteroid.receiver.SmsReceiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;


/**
 * A base service to communicate with PC.
 * @author Taeho Kim
 *
 */
public class RemoteroidService extends Service implements FileTransmissionListener, VirtualEventListener{
	public enum ServiceState{IDLE, CONNECTING, CONNECTED};
	
	private Tranceiver mTransmitter;
	private InputHandler mInputHandler;
	private ServiceState mState = ServiceState.IDLE;
	private boolean flag = true;
	
	private IBinder mBinder = new IRemoteroid.Stub() {
		
		@Override
		public void onNotificationCatched(String notificationText, long when)
				throws RemoteException {
			// TODO Notification hooked and notification data has been delivered. Now this data should be sent to PC.
			
		}
		

		@Override
		public void onReceiveCall(String displayedName, String number, long when)
				throws RemoteException {
			// TODO Call received.
			
		}

		@Override
		public void onReceiveSMS(String displayedName, String number,
				String body, long when) throws RemoteException {
			// TODO SMS Received.
			
		}
		
		@Override
		public void onSMSSent(String displayedName, String phoneNumber,
				long when) throws RemoteException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String getConnectionStatus() throws RemoteException {
			return mState.name();
		}

		@Override
		public void connect(String ipAddress, String password)
				throws RemoteException {
			try {
				mState = ServiceState.CONNECTING;
				
				// Start connection and receive events from server
				mTransmitter.connect(ipAddress);
				
				// Open input device
				mInputHandler.open();
				
				// Listen incoming calls
				TelephonyManager telManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
				telManager.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
				
				// TODO Start fetch frame buffer and send it to server
				
				sendBroadcast(new Intent(RemoteroidIntent.ACTION_CONNECTED).putExtra("ip", ipAddress));
				
				showConnectionNotification(ipAddress);
				
				mState = ServiceState.CONNECTED;
			} catch (IOException e) {
				e.printStackTrace();
				sendBroadcast(new Intent(RemoteroidIntent.ACTION_INTERRUPTED));
				dismissNotification();
				mState = ServiceState.IDLE;
			}
		}

		@Override
		public void disconnect() throws RemoteException {
			TelephonyManager telManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
			telManager.listen(mPhoneListener, PhoneStateListener.LISTEN_NONE);
			
			dismissNotification();
			
			mInputHandler.close();
			mTransmitter.disconnect();
			mState = ServiceState.IDLE;
			sendBroadcast(new Intent(RemoteroidIntent.ACTION_DISCONNECTED));
		}


		@Override
		public void onSendFile(List<String> pathlist) throws RemoteException {
			ArrayList<File> fileList = getFileList(pathlist);
			mTransmitter.sendFile(fileList);
		}


		@Override
		public boolean isTransmitterConnected() throws RemoteException {
			return mTransmitter.isConnected();
		}


	};

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	public ServiceState getConnectionState(){
		return mState;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mTransmitter = new Tranceiver();
		mTransmitter.setFileTransmissionListener(this);
		mTransmitter.setVirtualEventListener(this);
		mInputHandler = new InputHandler(this);
	}
	
	private PhoneStateListener mPhoneListener = new PhoneStateListener(){

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			if(state==TelephonyManager.CALL_STATE_RINGING){
				
			}
		}
		
	};


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent.getExtras()!=null && intent.getParcelableArrayListExtra(SmsReceiver.EXTRA_MSGS)!=null){
			ArrayList<RDSmsMessage> list = intent.getParcelableArrayListExtra(SmsReceiver.EXTRA_MSGS);
			
			for(RDSmsMessage msg : list){
				System.out.println(msg.toString());
				// TODO Message received..
			}
		}
		
		Thread mThread = new Thread(){

			@Override
			public void run() {
				while(flag){
				}
			}
		};
		mThread.setDaemon(true);
		mThread.start();
		
		return super.onStartCommand(intent, flags, startId);
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		flag=false;
	}


	/**
	 * Place a phone call.
	 * @param phoneNumber a Phone number
	 */
	@SuppressWarnings("unused")
	private void callPhone(String phoneNumber){
		startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:"+phoneNumber)));
	}
	
	/**
	 * Send a SMS.
	 * @param phoneNumber a Phone number
	 * @param body SMS body text
	 */
	@SuppressWarnings("unused")
	private void sendSMS(String phoneNumber, String body){
		if(!phoneNumber.matches("^\\d+$"))
			throw new NumberFormatException("Invalid phone number format.");
		SmsManager mng = SmsManager.getDefault();
		PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, new Intent(RemoteroidIntent.ACTION_SMS_SENT), 0);
		mng.sendTextMessage(phoneNumber, null, body, sentIntent, null);
	}
	
	private static final int NOTIFICATION_ID = 2012;
	
	@SuppressWarnings("deprecation")
	private void showConnectionNotification(String ipAddress){
		Notification notification = new Notification();
		PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(this, Main.class), 0);
		notification.icon = R.drawable.ic_launcher;
		notification.tickerText = String.format("Connected to %s", ipAddress);
		notification.when = System.currentTimeMillis();
		notification.setLatestEventInfo(getApplicationContext(), "Remoteroid", String.format("Connected to %s", ipAddress), intent);
		
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		NotificationManager notifManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		notifManager.notify(NOTIFICATION_ID, notification);
	}
	
	private void dismissNotification(){
		NotificationManager notifManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		notifManager.cancel(NOTIFICATION_ID);
	}


	@Override
	public void onSetCoordinates(int xPosition, int yPosition) {
		if(mInputHandler.isDeviceOpened())
			mInputHandler.touchSetPointer(xPosition, yPosition);
		
	}


	@Override
	public void onTouchDown() {
		if(mInputHandler.isDeviceOpened())
			mInputHandler.touchDown();		
	}


	@Override
	public void onTouchUp() {
		if(mInputHandler.isDeviceOpened())
			mInputHandler.touchUp();		
	}


	@Override
	public void onKeyDown(int keyCode) {
		if(mInputHandler.isDeviceOpened())
			mInputHandler.keyDown(keyCode);
	}


	@Override
	public void onKeyUp(int keyCode) {
		if(mInputHandler.isDeviceOpened())
			mInputHandler.keyUp(keyCode);
	}
	
	@Override
	public void onKeyStroke(int keyCode) {
		if(mInputHandler.isDeviceOpened())
			mInputHandler.keyStroke(keyCode);
	}


	@Override
	public void onFileInfoReceived(String fileName, long size) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onReadyToSend(ArrayList<File> filesToSend) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onSendFileInfo(File file) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onFileSent(File file) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onInterrupt() {
		// TODO Auto-generated method stub
		
	}
	
	private ArrayList<File> getFileList(List<String> pathlist) {
		ArrayList<File> result = new ArrayList<File>();
		for(int i = 0 ; i<pathlist.size() ; i++){
			result.add(new File(pathlist.get(i)));
		}
		
		return result;
	}
	
		

}
