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

import java.util.ArrayList;

import org.secmem.remoteroid.IRemoteroid;
import org.secmem.remoteroid.data.RDSmsMessage;
import org.secmem.remoteroid.intent.RemoteroidIntent;
import org.secmem.remoteroid.receiver.SmsReceiver;

import android.app.PendingIntent;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.SmsManager;

/**
 * A base service to communicate with PC.
 * @author Taeho Kim
 *
 */
public class RemoteroidService extends Service {
	
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
		public int getConnectionStatus() throws RemoteException {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void connect(String ipAddress, String password)
				throws RemoteException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void disconnect() throws RemoteException {
			// TODO Auto-generated method stub
			
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent.getExtras()!=null && intent.getParcelableArrayListExtra(SmsReceiver.EXTRA_MSGS)!=null){
			ArrayList<RDSmsMessage> list = intent.getParcelableArrayListExtra(SmsReceiver.EXTRA_MSGS);
			
			for(RDSmsMessage msg : list){
				System.out.println(msg.toString());
			}
		}
		return super.onStartCommand(intent, flags, startId);
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
	
	/**
	 * Lock device right now.
	 */
	private void lockNow(){
		DevicePolicyManager mDpm = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
		mDpm.lockNow();
	}

}
