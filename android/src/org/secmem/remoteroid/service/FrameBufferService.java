package org.secmem.remoteroid.service;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.secmem.remoteroid.natives.FrameHandler;
import org.secmem.remoteroid.socket.SocketModule;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;

public class FrameBufferService extends Service {
	
	private Bitmap bitmap;
	private int size;
	
	private SocketModule fSocket;
	private boolean flag=false;
	private Process p;
	private int operation;
	private FrameHandler fHandler;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
//		bitmap = getDisplayBitmap();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		fHandler = new FrameHandler(getApplicationContext());
		Log.i("fService","Size = "+String.valueOf(fHandler.getDisplaySize()));
		Log.i("fService","Size = "+String.valueOf(fHandler.getDisplayOrientation()));
//		fSocket = (SocketModule)intent.getSerializableExtra("socket");
//		if(fSocket.socket.isConnected()){
//			flag = true;
//		}
//		suPermission();
//		
		flag=true;
		Thread thread = new Thread(){
			public void run(){
				while(flag){
					Log.i("fService","Size = "+String.valueOf(fHandler.getDisplaySize()));
					Log.i("fService","Size = "+String.valueOf(fHandler.getDisplayOrientation()));
					fHandler.getDisplayBitmap();
					SystemClock.sleep(2000);
				}
			}
		};
		thread.start();
		
	}
	
	private Bitmap getDisplayBitmap() {
		
		Bitmap bitmap;
		
		DisplayMetrics dm = getResources().getDisplayMetrics();
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		
		int size = width*height*4;
		
		byte[] frameData = new byte[size];
		
		ByteBuffer frameBuffer = ByteBuffer.allocate(size);
		bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		
		return bitmap;
	}
	
	public void suPermission() {
		try {
			p = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(p.getOutputStream());
			os.writeBytes("chmod 664 /dev/graphics/fb0\n");
			os.writeBytes("chmod 664 /dev/graphics/fb1\n");
			os.writeBytes("exit\n");
			os.flush();
		} catch (IOException e) {	
			Intent i = new Intent("su_fail");
			sendBroadcast(i);
			e.printStackTrace();	}
	}

}
