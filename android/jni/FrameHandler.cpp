#include "FrameHandler.h"
#include <string.h>
#include <stdlib.h>
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

jboolean JNICALL Java_org_secmem_remoteroid_natives_FrameHandler_readFrameBufferNative(JNIEnv* env, jobject thiz,
		jint width, jint height, jint pixelDepth, jbyteArray buffer){

	int totalBytes = width * height * pixelDepth;

	unsigned char* buf;

	unsigned char* des;
	unsigned char* src;

	// Open frame buffer
	int fd = open("/dev/graphics/fb0", O_RDONLY);

	if(fd < 0){
		// Failed to open frame buffer.
		return -1;
	}

	unsigned char* pFrame = (unsigned char*)mmap(0, totalBytes, PROT_READ, MAP_PRIVATE, fd,0);

	if(pFrame == MAP_FAILED){
		close(fd);
		return -1;
	}

	unsigned char* pLandscapeFrame;

    if(width > height){ // Handle landscape mode
		pLandscapeFrame = (unsigned char*)malloc(totalBytes);

        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                //memcpy(cwidth+((799-j)*1280*4)+(i*4),pFrame+(i*800*4)+(j*4), 4);
                *(pLandscapeFrame+(((width-1)-j)*height)+i) = *(pFrame+(i*width)+j);
            }
        }
        buf = pLandscapeFrame;
    }else{
        buf = pFrame;
    }

	// FIXME env->SetByteArrayRegion(env, buffer, 0, totalBytes, (jbyte*)buf);
	munmap(pFrame, totalBytes);

    if(width > height)
    	free(pLandscapeFrame);

	return 0;
}
/*
jint Java_com_ssm_nwp_socket_SocketService_getFrameBuffer(JNIEnv* env, jobject thiz, jbyteArray jByte, jint jwidth, jint jheight, jint jpixel){
    
	int READ_BYTE = jwidth * jheight * jpixel;	
	int i,j,k;
	unsigned char *cwidth;
	unsigned char *buf;
	//hyomin
	int *des;
	int *src;
	//hyomin
    
	int fd = open("/dev/graphics/fb0", O_RDONLY);
	if(fd<0)
	{
		return -1;
	}
    
    
	char *pFrame;
    
	pFrame = mmap(0,READ_BYTE, PROT_READ, MAP_PRIVATE, fd,0);
	if(pFrame == MAP_FAILED){
		close(fd);
		return -1;
	}
	//return READ_BYTE;
	//hyomin modify
	//
    if(jwidth>jheight){			
		cwidth = (unsigned char *)malloc(READ_BYTE);
		des = (int *)cwidth;
		src = (int *)pFrame;
        for(i=0; i<1280; i++)
        {
            for(j=0; j<800; j++)
            {				
                //memcpy(cwidth+((799-j)*1280*4)+(i*4),pFrame+(i*800*4)+(j*4), 4);
                *(des+((799-j)*1280)+i) = *(src+(i*800)+j);
            }			
        }
        
        buf = (unsigned char *)des;
		
    }
    else
    {
        /*
         for(i=0; i<640; i++)
         {
         for(j=0; j<400; j++)
         {
         *(des+(i*400)+j) = *(src+(i*800*2)+j*2);
         }			
         }
         buf = (unsigned char *)des;
         *//*
        buf = pFrame;	
    }
    
	(*env)->SetByteArrayRegion(env,jByte,0,READ_BYTE,(jbyte*)buf);
	munmap(pFrame,READ_BYTE);
    
    if(jwidth>jheight) free(cwidth);
	return 0;
}*/
