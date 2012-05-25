package org.secmem.remoteroid.service;

import org.secmem.remoteroid.intent.RemoteroidIntent;
import org.secmem.remoteroid.natives.InputHandler;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class CalibrationService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private InputHandler mHandler;

	@Override
	public void onCreate() {
		super.onCreate();

		mHandler = new InputHandler(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(!mHandler.open())
			sendBroadcast(new Intent(RemoteroidIntent.ACTION_DEVICE_OPEN_FAILED));
		
		Bundle extras = intent.getExtras();
		int xCoord = extras.getInt("x");
		int yCoord = extras.getInt("y");
		
		mTouchHandler.sendMessageDelayed(Message.obtain(mTouchHandler, 0, xCoord, yCoord), 1000);
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	private Handler mTouchHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			mHandler.touchOnce(msg.arg1, msg.arg2);
			mHandler.close();
			stopSelf();
		}
		
	};

}
