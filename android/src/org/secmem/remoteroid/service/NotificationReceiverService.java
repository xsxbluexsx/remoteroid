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
import android.view.accessibility.AccessibilityEvent;

public class NotificationReceiverService extends AccessibilityService {
	private static final String TAG = "NotificationReceiverService";
	private static final boolean D = true;
	
	private IRemoteroid mRemoteroidSvc = null;
	private ServiceConnection mRemoteroidSvcConn = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mRemoteroidSvc = IRemoteroid.Stub.asInterface(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
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

	public String listToString(List<CharSequence> list){
		StringBuilder builder = new StringBuilder();
		for(CharSequence str : list)
			builder.append(str);
		return builder.toString();
	}
	

}
