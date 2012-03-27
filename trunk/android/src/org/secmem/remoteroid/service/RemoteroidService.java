package org.secmem.remoteroid.service;

import org.secmem.remoteroid.IRemoteroid;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class RemoteroidService extends Service {
	
	private IBinder mBinder = new IRemoteroid.Stub() {
		
		@Override
		public void onNotificationCatched(String notificationText, long when)
				throws RemoteException {
			// TODO Notification hooked and notification data has been delivered. Now this data should be sent to PC.
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

}
