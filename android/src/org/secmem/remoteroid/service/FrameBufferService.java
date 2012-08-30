package org.secmem.remoteroid.service;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.secmem.remoteroid.natives.FrameHandler;
import org.secmem.remoteroid.network.Tranceiver;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class FrameBufferService extends Service {
	
	private boolean flag=false;
	private Process p=null;
	
	private FrameHandler fHandler;
	private Tranceiver transmitter;
	
	int count=0;
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		flag=false;
		if(p!=null)
			suClose();
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
//		transmitter = new Tranceiver();
		Intent bIntent = new Intent("connecting_fragment_connect");
		
//		try {
//			transmitter.connect(intent.getStringExtra("IP"));
//			flag=true;
//		} catch (Exception e) {
//		}finally{
//			bIntent.putExtra("isConnected", flag);
//			if(ConnectingFragment.isFinished && flag){		// ���以��怨������痍⑥�����ㅻ㈃
//				transmitter.disconnect();				
//				stopSelf();
//			}
//			else{														// �����痍⑥���� ����ㅻ㈃
//				sendBroadcast(bIntent);
//				if(!flag)												// ���������쇰㈃ ����ㅼ�猷�
//					stopSelf();
//			}
//		}
		fHandler = new FrameHandler(getApplicationContext());
		suPermission();
		flag=true;
		Thread thread = new Thread(){
			@Override
			public void run() {
				
				while(flag){
//					ByteArrayOutputStream frameStream = fHandler.getFrameStream();
					// sendScreen(frameStream);
					Bitmap bitmap = fHandler.getTestFrameStream();
//					ByteArrayOutputStream frameStream = fHandler.getFrameStream();
				//	
					File copyFile = new File("/mnt/sdcard/captured"+count+".jpg");
					OutputStream out = null;
					try {
						copyFile.createNewFile();
						out = new FileOutputStream(copyFile);
						bitmap.compress(CompressFormat.JPEG,100,out);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally{
						try {
							out.close();
						} catch (Exception e2) {
							// TODO: handle exception
						}
					}
					
					
					count++;
					SystemClock.sleep(3000);
				}
				
			}
		};
		thread.setDaemon(true);
		thread.start();
		
		return super.onStartCommand(intent, flags, startId);
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
	
	public void suPermission() {
		try {
			p = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(p.getOutputStream());
			os.writeBytes("chmod 664 /dev/graphics/fb0\n");
			os.writeBytes("chmod 664 /dev/graphics/fb1\n");
			os.writeBytes("exit\n");
			os.flush();
		} catch (IOException e) {	
			
		}
	}
	
	public void suClose() {
		try {
			p = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(p.getOutputStream());
			os.writeBytes("chmod 660 /dev/graphics/fb0\n");
			os.writeBytes("chmod 664 /dev/graphics/fb1\n");
			os.writeBytes("exit\n");
			os.flush();
		} catch (IOException e) {
			
		}
	}

}
