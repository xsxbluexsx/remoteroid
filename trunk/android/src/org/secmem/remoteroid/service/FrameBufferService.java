package org.secmem.remoteroid.service;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.secmem.remoteroid.socket.SocketModule;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class FrameBufferService extends Service {
	
	private Bitmap bitmap;
	private int size;
	
	private SocketModule fSocket;
	private boolean flag=false;
	private Process p;
	private int operation;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		bitmap = getDisplayBitmap();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		fSocket = (SocketModule)intent.getSerializableExtra("socket");
		if(fSocket.socket.isConnected()){
			flag = true;
		}
		suPermission();
		
		Thread thread = new Thread(){
			public void run(){
				while(flag){
					
					try {
						int len;
						byte[] line = new byte[6];
						
						if(!flag)
							break;						
						else{
							len = fSocket.inputStream.read(line);
							if(len <=0){
								return;
							}
							else if(len==6){
								operation = (int)line[1];
								
							}
						}
						
					} catch (Exception e) {
					}
				}
			}
		};
		
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
