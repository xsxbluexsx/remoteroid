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
#include "include/FrameHandler.h"
#include "include/android_fbuffer.h"

jint Java_org_secmem_remoteroid_natives_FrameHandler_getFrameBuffer(JNIEnv* env, jobject thiz, jbyteArray jByte, jint pixelformat){

	int		i,j,k;
	int		Dst;
	int		fd;
	int		ret;

	unsigned char	*pFrame, *buf;
	int				*intbuf, *intpFrame0, *intpFrame1;
//	uint16_t			*int16_buf;

	static Display						disinfo;
	static struct fb_var_screeninfo		vinfo;
	struct fb_fix_screeninfo			finfo;

	fd = open("/dev/graphics/fb0", O_RDONLY);
	if(fd<0)
	{
		perror("cannot open fb0");
		return FB_FAIL;
	}
	ret = ioctl(fd, FBIOGET_VSCREENINFO, &vinfo);
	if(ret < 0 )
	{
		perror("cannot open Variable screen information.");
		close(fd);
		return VINFO_FAIL;
	}
	ret = ioctl(fd, FBIOGET_FSCREENINFO, &finfo);
	if(ret < 0 )
	{
		perror("cannot open fixed screen information.");
		close(fd);
		return FINFO_FAIL;
	}

	disinfo.width			= vinfo.xres/2;
	disinfo.height			= vinfo.yres/2;
	disinfo.byteperpixel	= vinfo.bits_per_pixel/8;
	disinfo.pixelformat		= pixelformat;

	disinfo.fullbyte	= disinfo.width * disinfo.height * disinfo.byteperpixel;

	pFrame = mmap(0,finfo.smem_len, PROT_READ, MAP_PRIVATE, fd,0);
	
	if(pFrame == MAP_FAILED){
		close(fd);
		return MMAP_FAIL;
	}
	intpFrame0	= (int *) pFrame;
	intpFrame1	= (int *) (pFrame+finfo.smem_len/2);

	buf = (unsigned char *) malloc(disinfo.fullbyte);
	
	intbuf		= (int *) buf;

	if(disinfo.pixelformat == PIXEL_UNKNOWN)	return FORMAT_UNKNOWN;

	else
	if(disinfo.pixelformat == PIXEL_ARGB_8888){
		for(i=0; i<disinfo.height; i++){
			for(j=0; j<disinfo.width; j++){
				k = *(intpFrame0+(i*disinfo.width*4)+(j*2)); 
				*(intbuf+(i*disinfo.width)+j) = k;
			}
		}
	}

	else
	if(disinfo.pixelformat == PIXEL_XBGR_8888){
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
	
	(*env)->SetByteArrayRegion(env,jByte,0,disinfo.fullbyte,(jbyte*)buf);
	munmap(pFrame,finfo.smem_len);
	free(buf);
	close(fd);
	
	return j;
}	

/*
	int16_buf	= (uint16_t *) buf;
			
		for(i=0; i<disinfo.height; i++){
			for(j=0; j<disinfo.width; j++){
					k = *(intpFrame0+(i*disinfo.width*4)+(j*2)); 
					Dst	=	(k & 0x000000f8)  >> 3;
					Dst	+=	(k & 0x0000fC00)  >> 5;
					Dst	+=	(k & 0x00f80000)  >> 8;

				*(int16_buf+((i*disinfo.width)+j)) = (uint16_t) Dst;
			}
		}
		buf = (unsigned char *) int16_buf;
		*/
