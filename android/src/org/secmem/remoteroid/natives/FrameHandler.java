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
