package org.secmem.remoteroid.service;

import org.secmem.remoteroid.IRemoteroidU;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RemoteroidServiceU extends Service {

	private IBinder binder = new IRemoteroidU.Stub() {
		
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

}
