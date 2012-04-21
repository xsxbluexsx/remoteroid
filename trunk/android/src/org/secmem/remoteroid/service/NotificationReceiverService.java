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

import java.util.List;

import org.secmem.remoteroid.IRemoteroid;

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class NotificationReceiverService extends AccessibilityService {
	private static final String TAG = "NotificationReceiverService";
	private static final boolean D = true;
	
	private IRemoteroid mRemoteroidSvc = null;
	private ServiceConnection mRemoteroidSvcConn = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			if(D) Log.d(TAG, "Connected to Remoteroid service.");
			mRemoteroidSvc = IRemoteroid.Stub.asInterface(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			if(D) Log.d(TAG, "Disconnected from Remoteroid service.");
			mRemoteroidSvc = null;
		}
		
	};
	
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		try{
			// TODO Apply user's preferences(Package filtering)
			if(mRemoteroidSvc!=null)
				mRemoteroidSvc.onNotificationCatched(listToString(event.getText()), event.getEventTime());
		}catch(RemoteException e){
			e.printStackTrace();
		}
	}

	@Override
	public void onInterrupt() {
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// Bind to RemoteroidService on start
		if(mRemoteroidSvc!=null)
			bindService(new Intent(this, RemoteroidService.class), mRemoteroidSvcConn, Context.BIND_AUTO_CREATE);
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Unbind from RemoteroidService on destroy
		if(mRemoteroidSvc!=null)
			unbindService(mRemoteroidSvcConn);
	}

	/**
	 * Converts notification or toast string lists into single String.
	 * @param list events's text, returned by <code>AccessibilityEvent.getText()</code>.
	 * @return
	 */
	private String listToString(List<CharSequence> list){
		StringBuilder builder = new StringBuilder();
		for(CharSequence str : list)
			builder.append(str);
		return builder.toString();
	}

}
