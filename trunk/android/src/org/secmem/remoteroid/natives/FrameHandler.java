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
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class FrameHandler {
	
	private ByteBuffer frameBuffer;
	private ByteArrayOutputStream frameStream;
	private byte[] buffer;
	private Bitmap bitmap;
	
	private Context context;
	
	private int displaySize;
	private int width;
	private int height;
	private int pixel;
	private int orientation;
	
	/*

	 1. FrameHandler handler = new FrameHandler();
	 3. suPermission();
	 
	 **Start Loop{
	 
		 4. frameStream = getFrameStream();
		 
		 **SendScreenShot
	 }
	 */

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
	
//	public Bitmap readFrameBuffer(Context context){
//		DisplayMetrics dm = context.getResources().getDisplayMetrics();
//		int width = dm.widthPixels;
//		int height = dm.heightPixels;
//		int pixelDepth = 4;
//		
//		WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
//		
//		int rotation = wm.getDefaultDisplay().getRotation();
//		
//		byte[] frameData = new byte[width*height*4];
////		readFrameBufferNative(width, height, pixelDepth, frameData);
//		
//		ByteBuffer frameBuffer = ByteBuffer.allocate(width*height*4);
//		Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//		// FIXME
//		
//		return null;
//	}
	
	
	
	public FrameHandler(Context context) {
		this.context = context;
		
		setDisplayValue();
		setBitmap(getDisplayBitmap());
		
		frameBuffer = ByteBuffer.allocate(displaySize);
		buffer = new byte[displaySize];
	}
	
	
	private void setDisplayValue() {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		this.width = dm.widthPixels/2;
		this.height = dm.heightPixels/2;
		this.pixel=4;
		
		this.orientation = getDisplayOrientation();
		
		this.displaySize = width*height*pixel;
	}

	// get Device Display Bitmap
	public Bitmap getDisplayBitmap(){
		return Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
	}
	
	// get Device Display Orientation
	public int getDisplayOrientation(){
		Display display = ((WindowManager) context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();
		return display.getOrientation();
	}
	
	// Compress JPEG
	public ByteArrayOutputStream getFrameStream(){
		
		if(orientation != getDisplayOrientation()){
			setDisplayValue();
			setBitmap(getDisplayBitmap());
		}
		
		ByteArrayOutputStream frameStream = new ByteArrayOutputStream();
		
		int ret = getFrameBuffer(buffer, getWidth(), getHeight(), getPixel(), getDisplayOrientation());
		
		frameBuffer.put(buffer, 0, displaySize);
		frameBuffer.rewind();
		bitmap.copyPixelsFromBuffer(frameBuffer);
		bitmap.compress(CompressFormat.JPEG, 70, frameStream);
		
		return frameStream;
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
	
	
	public ByteBuffer getFrameBuffer() {
		return frameBuffer;
	}

	public void setFrameBuffer(ByteBuffer frameBuffer) {
		this.frameBuffer = frameBuffer;
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getPixel() {
		return pixel;
	}
	public void setPixel(int pixel) {
		this.pixel = pixel;
	}
	public int getDisplaySize() {
		return displaySize;
	}
	public void setDisplaySize(int displaySize) {
		this.displaySize = displaySize;
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public int getOrientation() {
		return orientation;
	}
	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

}
