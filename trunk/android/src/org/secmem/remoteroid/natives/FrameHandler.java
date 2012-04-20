package org.secmem.remoteroid.natives;

import java.nio.ByteBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class FrameHandler {
	
	public Bitmap readFrameBuffer(Context context){
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		int pixelDepth = 4;
		
		WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		int rotation = wm.getDefaultDisplay().getRotation();
		
		byte[] frameData = new byte[width*height*4];
		readFrameBufferNative(width, height, pixelDepth, frameData);
		
		ByteBuffer frameBuffer = ByteBuffer.allocate(width*height*4);
		Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		
		// FIXME
		
		return null;
	}
	
	/**
	 * Read frame buffer from device.
	 * @param width Screen width
	 * @param height Screen height
	 * @param pixelDepth 
	 * @param dest Byte buffer where frame buffer's data be stored
	 * @return true if frame buffer loaded successful, false otherwise
	 */
	public native boolean readFrameBufferNative(int width, int height, int pixelDepth, byte[] dest);

}
