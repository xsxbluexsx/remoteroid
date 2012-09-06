/*
 * Remoteroid - A remote control solution for Android platform, including handy file transfer and notify-to-PC.
 * Copyright (C) 2012 Taeho Kim(jyte82@gmail.com), Hyomin Oh(ohm    ]\
 nia1112@gmail.com), Hongkyun Kim(godgjdgjd@nate.com), Yongwan Hwang(singerhwang@gmail.com)
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
 * along with this program; if not, writeD to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/



#include <string.h>
#include <stdlib.h>
#include <stdint.h>
#include <jni.h>
#include <stdio.h>
#include <errno.h>
#include <fcntl.h>
#include <unistd.h>
#include <time.h>

#include <linux/kd.h>
#include <linux/fb.h>

#include <sys/mman.h>
#include <sys/ioctl.h>
#include <sys/types.h>

#include <asm/page.h>

#include "include/DisplayInfo.h"
#include "include/FrameHandler.h"
#include "include/Frame.h"

	static	DisplayInfo					disinfo;
	static struct fb_var_screeninfo		vinfo;
	static struct fb_fix_screeninfo		finfo;
	int		fd;

int getFrame(JNIEnv* env, jbyteArray jByte, jint pixelFormat){

	int		i,j,k;
	int		Dst;
	int		ret;

	unsigned char	*pFrame, *buf;
	int				*intbuf, *intpFrame0, *intpFrame1;
//	uint16_t			*int16_buf;

	if(disinfo.initialized == false){
		int i;
		i = initValue(pixelFormat);
		if(i>=0){
			return i;
		}
	}
	else {
		fd = open("/dev/graphics/fb0", O_RDONLY);
	
		if(fd<0)
		{
			LOGD(LOGTAG, "Cannot open device - 'remoteroid'");
			return FB_FAIL;
		}

	}
	pFrame = (unsigned char *)mmap(0,finfo.smem_len, PROT_READ, MAP_PRIVATE, fd,0);
	
	if(pFrame == MAP_FAILED){
		close(fd);
		return MMAP_FAIL;
	}
	intpFrame0	= (int *) pFrame;
	intpFrame1	= (int *) (pFrame+finfo.smem_len/2);

	buf = (unsigned char *) malloc(disinfo.fullbyte);
	
	intbuf		= (int *) buf;

	if(disinfo.pixelFormat == PIXEL_UNKNOWN)	return FORMAT_UNKNOWN;

	else
	if(disinfo.pixelFormat == PIXEL_ARGB_8888){
		for(i=0; i<disinfo.height; i++){
			for(j=0; j<disinfo.width; j++){
				k = *(intpFrame0+(i*disinfo.width*4)+(j*2)); 
				*(intbuf+(i*disinfo.width)+j) = k;
			}
		}
	}

	else
	if(disinfo.pixelFormat == PIXEL_XBGR_8888){
		for(i=0; i<disinfo.height; i++){
			for(j=0; j<disinfo.width; j++){
				k = *(intpFrame0+(i*disinfo.width*4)+(j*2));
				Dst = k & 0xff00ff00;
				Dst += (k & 0x00ff0000) >> 16;
				Dst += (k & 0x000000ff) << 16;
				*(intbuf+(i*disinfo.width)+j) = Dst;
			}
		}
	}

	else{
		return FORMAT_VALUE_FAIL;
	}


	buf = (unsigned char *) intbuf;
	
	(*env).SetByteArrayRegion(jByte,0,disinfo.fullbyte,(jbyte*)buf);
	munmap(pFrame,finfo.smem_len);
	free(buf);
	close(fd);
	LOGD(LOGTAG, "'remoteroid'");
	return j;
}	

int initValue(int pF){
	int ret;

	fd = open("/dev/graphics/fb0", O_RDONLY);
	if(fd<0)
	{
		LOGD(LOGTAG, "Cannot open device - 'remoteroid'");
		return FB_FAIL;
	}
	ret = ioctl(fd, FBIOGET_VSCREENINFO, &vinfo);
	if(ret < 0 )
	{
		LOGD(LOGTAG, "Cannot open Variable screen information. - 'remoteroid'");
		close(fd);
		return VINFO_FAIL;
	}
	ret = ioctl(fd, FBIOGET_FSCREENINFO, &finfo);
	if(ret < 0 )
	{
		LOGD(LOGTAG, "Cannot open fixed screen information. - 'remoteroid'");
		close(fd);
		return FINFO_FAIL;
	}

	disinfo.width			= vinfo.xres/2;
	disinfo.height			= vinfo.yres/2;
	disinfo.byteperpixel	= vinfo.bits_per_pixel/8;
	disinfo.pixelFormat		= pF;
	disinfo.fullbyte	= disinfo.width * disinfo.height * disinfo.byteperpixel;

	disinfo.initialized = true;

	return -1;
}