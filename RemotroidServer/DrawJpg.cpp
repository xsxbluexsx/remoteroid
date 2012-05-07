#include "StdAfx.h"
#include "DrawJpg.h"


CDrawJpg::CDrawJpg()
	: m_iTotalJpgSize(0)
	, m_iRecvJpgSize(0)	
{
	m_pBitmapData = new BYTE[MAXRESOLUTION];
	m_pJpgData = new BYTE[MAXRESOLUTION];
	memset(&image, 0, sizeof(image));
	if(ijlInit(&image) != IJL_OK)
	{
		TRACE(_T("Cannot initialize Intel JPEG library\n"));
	}
}


CDrawJpg::~CDrawJpg(void)
{
	delete [] m_pBitmapData;
	delete [] m_pJpgData;

	if(ijlFree(&image) != IJL_OK)
	{
		TRACE(_T("cannot free intel jpg\n"));
	}
}


// 한 프레임의 JPG 크기 정보를 세팅
void CDrawJpg::SetJpgInfo(char * data)
{	
	memset(m_bJpgSize, 0, sizeof(m_bJpgSize));
	memcpy(m_bJpgSize, data, JPGSIZELEGNTH);
	m_iTotalJpgSize = atoi(m_bJpgSize);
	m_iRecvJpgSize = 0;
	memset(m_pJpgData, 0, MAXRESOLUTION);
}

//수신받은 jpg data를 버퍼에 순서대로 저장
void CDrawJpg::RecvJpgData(char * data, int packetSize)
{
	int jpgDataSize = packetSize - HEADERSIZE;
	memcpy(m_pJpgData+m_iRecvJpgSize, data, jpgDataSize);
	m_iRecvJpgSize += jpgDataSize;
	if(m_iRecvJpgSize >= m_iTotalJpgSize)
	{
		DrawScreen();
		m_iRecvJpgSize = 0;
	}
}


// 한 프레임 jpg 출력
void CDrawJpg::DrawScreen(void)
{
	if(!SetIJLInfo())
	{
		return;
	}
	SetBitmapInfo();
}

//Intel Jpeg Library 초기화 작업
BOOL CDrawJpg::SetIJLInfo(void)
{
	try
	{		
		image.JPGFile = NULL;
		image.JPGBytes = m_pJpgData;
		image.JPGSizeBytes = m_iTotalJpgSize;

		if((err=ijlRead(&image, IJL_JBUFF_READPARAMS)) != IJL_OK)
		{
			TRACE(_T("cannot read jpeg file header %s\n"), ijlErrorStr(err));
			AfxThrowUserException();
		}

		switch(image.JPGChannels)
		{
		case 1:
			image.JPGColor = IJL_G;
			image.DIBChannels = 3;
			image.DIBColor = IJL_BGR;
			break;
		case 3:
			image.JPGColor = IJL_YCBCR;
			image.DIBChannels = 3;
			image.DIBColor    = IJL_BGR;
			break;
		case 4:
			image.JPGColor    = IJL_YCBCRA_FPX;
			image.DIBChannels = 4;
			image.DIBColor    = IJL_RGBA_FPX;
			break;

		default:
			// This catches everything else, but no
			// color twist will be performed by the IJL.
			image.DIBColor = (IJL_COLOR)IJL_OTHER;
			image.JPGColor = (IJL_COLOR)IJL_OTHER;
			image.DIBChannels = image.JPGChannels;
			break;
		}
		image.DIBWidth = image.JPGWidth;
		image.DIBHeight = image.JPGHeight;
		image.DIBPadBytes = IJL_DIB_PAD_BYTES(image.DIBWidth, image.DIBChannels);
		int iImageSize = (image.DIBWidth * image.DIBChannels + image.DIBPadBytes)*
			image.DIBHeight;

		memset(m_pBitmapData, 0, MAXRESOLUTION);
		image.DIBBytes = m_pBitmapData;

		if((err=ijlRead(&image, IJL_JBUFF_READWHOLEIMAGE)) != IJL_OK)
		{
			TRACE(_T("cannot read image data : %s\n"), ijlErrorStr(err));
			AfxThrowUserException();
		}
	}	
	catch (CException* e)
	{
		return FALSE;
	}
	return TRUE;
}


void CDrawJpg::SetBitmapInfo(void)
{
	memset(&bmi, 0, sizeof(BITMAPINFO));
	BITMAPINFOHEADER& bih = bmi.bmiHeader;
	memset(&bih, 0, sizeof(BITMAPINFOHEADER));
	bih.biSize = sizeof(BITMAPINFOHEADER);
	bih.biWidth = image.DIBWidth;
	bih.biHeight = -(image.DIBHeight);
	bih.biCompression = BI_RGB;
	bih.biPlanes = 1;
	//픽셀당 비트수.. 컬러수*8비트
	bih.biBitCount = image.DIBChannels*8;
	
	HDC hdc = ::GetDC(screenHandle);
	::StretchDIBits(hdc, 0, 0, image.DIBWidth, image.DIBHeight, 0, 0,
		image.DIBWidth, image.DIBHeight, m_pBitmapData, &bmi, DIB_RGB_COLORS, SRCCOPY);
	::ReleaseDC(screenHandle, hdc);
}


void CDrawJpg::InitDrawJpg(HWND screenHandle, int XSize, int YSize)
{		
	this->screenHandle = screenHandle;
	screenXSize = XSize;
	screenYSize = YSize;
}
