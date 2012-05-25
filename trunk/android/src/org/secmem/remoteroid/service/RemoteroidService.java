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

import org.secmem.remoteroid.IRemoteroid;
import org.secmem.remoteroid.data.RDSmsMessage;
import org.secmem.remoteroid.intent.RemoteroidIntent;
import org.secmem.remoteroid.natives.InputHandler;
import org.secmem.remoteroid.network.FileTransmissionListener;
import org.secmem.remoteroid.network.Tranceiver;
import org.secmem.remoteroid.network.VirtualEventListener;
import org.secmem.remoteroid.receiver.SmsReceiver;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.SmsManager;
import android.util.*;


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
				
				//Send devices resolution to host for coordinate transformation;
				mTransmitter.sendDeviceInfo(getApplicationContext().getResources().getDisplayMetrics());
				
				// Open input device
				mInputHandler.open();
				
				// TODO Start fetch frame buffer and send it to server
				
				sendBroadcast(new Intent(RemoteroidIntent.ACTION_CONNECTED).putExtra("ip", ipAddress));
				mState = ServiceState.CONNECTED;
			} catch (IOException e) {
				e.printStackTrace();
				sendBroadcast(new Intent(RemoteroidIntent.ACTION_INTERRUPTED));
			}
		}

		@Override
		public void disconnect() throws RemoteException {
			mInputHandler.close();
			mTransmitter.disconnect();
			mState = ServiceState.IDLE;
			sendBroadcast(new Intent(RemoteroidIntent.ACTION_DISCONNECTED));
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


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent.getExtras()!=null && intent.getParcelableArrayListExtra(SmsReceiver.EXTRA_MSGS)!=null){
			ArrayList<RDSmsMessage> list = intent.getParcelableArrayListExtra(SmsReceiver.EXTRA_MSGS);
			
			for(RDSmsMessage msg : list){
				System.out.println(msg.toString());
				// TODO Message received..
			}
		}
		
		return super.onStartCommand(intent, flags, startId);
	}


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}


	/**
	 * Place a phone call.
	 * @param phoneNumber a Phone number
	 */
	private void callPhone(String phoneNumber){
		startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:"+phoneNumber)));
	}
	
	/**
	 * Send a SMS.
	 * @param phoneNumber a Phone number
	 * @param body SMS body text
	 */
	private void sendSMS(String phoneNumber, String body){
		if(!phoneNumber.matches("^\\d+$"))
			throw new NumberFormatException("Invalid phone number format.");
		SmsManager mng = SmsManager.getDefault();
		PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, new Intent(RemoteroidIntent.ACTION_SMS_SENT), 0);
		mng.sendTextMessage(phoneNumber, null, body, sentIntent, null);
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


	



}
