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
		int width = extras.getInt("width");
		int height = extras.getInt("height");
		
		// Send sequence of calibration message.
		mTouchHandler.sendMessage(Message.obtain(mTouchHandler, 0, 0, 0));
		mTouchHandler.sendMessageDelayed(Message.obtain(mTouchHandler, 0, width, 0), 1000);
		mTouchHandler.sendMessageDelayed(Message.obtain(mTouchHandler, 0, 0, height), 2000);
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	private Handler mTouchHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			System.out.println("sending event, x="+msg.arg1+", y="+msg.arg2);
			
			mHandler.touchOnce(msg.arg1, msg.arg2);
			mHandler.close();
			stopSelf();
		}
		
	};

}
