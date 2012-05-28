
#ifndef __ANDROID_FBUFFER_H__
#define __ANDROID_FBUFFER_H__

#include <stdint.h>
#include <sys/types.h>

enum FormatIndex{
	INDEX_ALPHA	= 0,
	INDEX_RED	= 1,
	INDEX_GREEN	= 2,
	INDEX_BLUE	= 3,

	INDEX_Y		= 0,
	INDEX_CB	= 1,
	INDEX_CR	= 2,
};

typedef struct{
	uint32_t		width;
	uint32_t		height;
	uint8_t			pixelformat;
	uint8_t			byteperpixel;
	uint32_t		fullbyte;
} Display;

#define	PIXEL_UNKNOWN		 0
#define	PIXEL_ARGB_8888		 1			//4 BYTE ARGB
#define	PIXEL_RGBX_8888		 2			//4 BYTE RGBX
#define	PIXEL_RGB_888		 3			//3 BYTE RGB
#define	PIXEL_RGB_565		 4			//2 BYTE RGB
#define	PIXEL_XBGR_8888		 5			//4 BYTE XBGR
#define	PIXEL_RGBA_5551		 6			//2 BYTE RGBA
#define	PIXEL_ARGB_4444		 7			//2 BYTE ARGB

#define FB_FAIL				0
#define	VINFO_FAIL			1
#define FINFO_FAIL			2
#define MMAP_FAIL			3
#define FORMAT_UNKNOWN		4
#define FORMAT_VALUE_FAIL	5
#define	ORIENTATION_ERROR	10
#define	SCREEN_SLEEP		100

#endif //ANDROID_FBUFFER_H
