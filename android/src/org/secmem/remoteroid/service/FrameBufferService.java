package org.secmem.remoteroid.service;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.secmem.remoteroid.natives.FrameHandler;
import org.secmem.remoteroid.socket.SocketModule;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class FrameBufferService extends Service {
	
	private Bitmap bitmap;
	private int size;
	
	private SocketModule fSocket;
	private boolean flag=false;
	private Process p;
	private int operation;
	private FrameHandler fHandler;
	
	int count=0;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		fHandler = new FrameHandler(getApplicationContext());
		suPermission();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		
//		fSocket = (SocketModule)intent.getSerializableExtra("socket");
//		if(fSocket.socket.isConnected()){
//			flag = true;
//		}
		flag=true;
		Thread thread = new Thread(){
			public void run(){
				while(flag){
					
					ByteArrayOutputStream frameStream = fHandler.getFrameStream();
					
					
					SystemClock.sleep(5000);
				}
			}
		};
		thread.start();
		
	}
	
	
//	Bitmap bitmap = fHandler.getTestFrameStream();
//	
//	File copyFile = new File("/mnt/sdcard/captured"+count+".png");
//	OutputStream out = null;
//	try {
//		copyFile.createNewFile();
//		out = new FileOutputStream(copyFile);
//		bitmap.compress(CompressFormat.JPEG,100,out);
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}finally{
//		try {
//			out.close();
//		} catch (Exception e2) {
//			// TODO: handle exception
//		}
//	}
//	
//	
//	count++;
//	Log.i("qq","Service");
	
	
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
