/*
 * Remoteroid - A remote control solution for Android platform, including handy file transfer and notify-to-PC.
 * Copyright (C) 2012 Taeho Kim(jyte82@gmail.com), Hyomin Oh(ohmnia1112@gmail.com), Hongkyun Kim(godgjdgjd@nate.com), Yongwan Hwang(singerhwang@gmail.com)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package org.secmem.remoteroid.natives;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class FrameHandler {
	
	private ByteBuffer frameBuffer;
	private ByteArrayOutputStream frameStream;
	private byte[] buffer;
	
	private Context context;
	private int displaySize;
	
	public Bitmap readFrameBuffer(Context context){
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		int pixelDepth = 4;
		
		WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		
		int rotation = wm.getDefaultDisplay().getRotation();
		
		byte[] frameData = new byte[width*height*4];
//		readFrameBufferNative(width, height, pixelDepth, frameData);
		
		ByteBuffer frameBuffer = ByteBuffer.allocate(width*height*4);
		Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		// FIXME
		
		return null;
	}
	
	/*

	 1. FrameHandler handler = new FrameHandler();
	 2. bitmap = getDisplayBitmap(Context context);
	 3. suPermission();
	 
	 **Start Loop
	 
	 4. if(orientation != getDisplayOrientation()){
			bitmap = getDisplayBitmap(context);
		}
	 
	 5. frameStream = getFrameStream(Bitmap bitmap);
	 
	 **SendScreenShot
	 
	 */
	
	public FrameHandler(Context context) {
		this.context = context;
		
		this.displaySize = getDisplaySize(context);
		
		frameBuffer = ByteBuffer.allocate(displaySize);
		frameStream = new ByteArrayOutputStream();
		buffer = new byte[displaySize];
	}
	
	
	// get Device Display Bitmap
	public Bitmap getDisplayBitmap(Context context){
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		
		Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		
		return bm;
	}
	
	// get Device Display size
	public int getDisplaySize(Context context){
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		int pixel=4;
		
		return width*height*pixel;
	}
	
	// get Device Display Orientation
	public int getDisplayOrientation(Context context){
		Display display = ((WindowManager) context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();
		return display.getOrientation();
	}
	
	// Compress JPEG
	public ByteArrayOutputStream getFrameStream(Bitmap bitmap){
		ByteArrayOutputStream frameStream = new ByteArrayOutputStream();
		frameBuffer.put(buffer, 0, displaySize);
		frameBuffer.rewind();
		bitmap.copyPixelsFromBuffer(frameBuffer);
		bitmap.compress(CompressFormat.JPEG, 70, frameStream);

		ByteArrayOutputStream result = frameStream;
		
		frameStream.reset();
		
		return result;
	}
	
	
	
//	public void suPermission() {
//		try {
//			p = Runtime.getRuntime().exec("su");
//			DataOutputStream os = new DataOutputStream(p.getOutputStream());
//			os.writeBytes("chmod 664 /dev/graphics/fb0\n");
//			os.writeBytes("chmod 664 /dev/graphics/fb1\n");
//			os.writeBytes("exit\n");
//			os.flush();
//		} catch (IOException e) {	
//		}
//	}
	
//	public void suClose() {
//		try {
//			p = Runtime.getRuntime().exec("su");
//			DataOutputStream os = new DataOutputStream(p.getOutputStream());
//			os.writeBytes("chmod 660 /dev/graphics/fb0\n");
//			os.writeBytes("chmod 664 /dev/graphics/fb1\n");
//			os.writeBytes("exit\n");
//			os.flush();
//		} catch (IOException e) {	e.printStackTrace();}
//	}
	
	
	/**
	 * Read frame buffer from device.
	 * @param width Screen width
	 * @param height Screen height
	 * @param pixelDepth 
	 * @param dest Byte buffer where frame buffer's data be stored
	 * @return true if frame buffer loaded successful, false otherwise
	 */
//	public native boolean readFrameBufferNative(int width, int height, int pixelDepth, byte[] dest);

	private native int getFrameBuffer(byte[] buff, int width, int height, int pixel, int orientation);
	
	static {
		System.loadLibrary("fbuffer");
	}
}
