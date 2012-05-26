
// Screen.cpp : implementation file
//

#include "stdafx.h"
#include "RemotroidServer.h"
#include "Screen.h"


// CScreen

IMPLEMENT_DYNAMIC(CScreen, CStatic)

CScreen::CScreen()
: pClient(NULL)
, widthResolution(0)
, heightResolution(0)
, width(0)
, height(0)
{	
}

CScreen::~CScreen()
{	
	
}


BEGIN_MESSAGE_MAP(CScreen, CStatic)
	ON_WM_DESTROY()
	ON_MESSAGE(WM_RECVJPGINFO, OnSetJpgInfo)
	ON_MESSAGE(WM_RECVJPGDATA, OnRecvJpgData)	
	ON_MESSAGE(WM_RECVDEVICEINFO, OnSetResolution)
	ON_WM_LBUTTONDOWN()
	ON_WM_LBUTTONUP()
	ON_WM_MOUSEMOVE()		
END_MESSAGE_MAP()
 


// CScreen message handlers




void CScreen::InitDrawJpg(void)
{
	CRect rect;
	GetClientRect(&rect);	
	width = rect.Width();
	height = rect.Height();
	drawJpg.InitDrawJpg(GetSafeHwnd(), width, height);	
}


void CScreen::OnDestroy()
{
	CStatic::OnDestroy();

	// TODO: Add your message handler code here	
}



LRESULT CScreen::OnSetResolution(WPARAM wParam, LPARAM lParam)
{
	char *data = (char *)lParam;

	char bWidth[WIDTH_LENGTH+1];
	memset(bWidth, 0, sizeof(bWidth));
	memcpy(bWidth, data, WIDTH_LENGTH);
	widthResolution = atoi(bWidth);

	char bHeight[HEIGHT_LENGTH+1];
	memset(bHeight, 0, sizeof(bHeight));
	memcpy(bHeight, data+WIDTH_LENGTH, HEIGHT_LENGTH);
	heightResolution = atoi(bHeight);
	return 0;
}


LRESULT CScreen::OnSetJpgInfo(WPARAM wParam, LPARAM lParam)
{	
	char *data = (char*)lParam;
	drawJpg.SetJpgInfo(data);
	return 0;
}


LRESULT CScreen::OnRecvJpgData(WPARAM wParam, LPARAM lParam)
{	
	int packetSize = wParam;
	char *data = (char *)lParam;
	drawJpg.RecvJpgData(data, packetSize);
	return 0;
}

inline void CScreen::CoordinateTransform(CPoint& point)
{
	point.x = point.x*((float)widthResolution/width);
	point.y = point.y*((float)heightResolution/height);
}


void CScreen::OnLButtonDown(UINT nFlags, CPoint point)
{
	// TODO: Add your message handler code here and/or call default
	if(pClient == NULL)
		return;

	CoordinateTransform(point);
	CVitualEventPacket event(TOUCHDOWN, point.x, point.y);
	pClient->SendPacket(OP_VIRTUALEVENT, event.asByteArray(), event.payloadSize);

	CStatic::OnLButtonDown(nFlags, point);
}



void CScreen::OnLButtonUp(UINT nFlags, CPoint point)
{
	// TODO: Add your message handler code here and/or call default
	if(pClient == NULL)
		return;

	CoordinateTransform(point);
	CVitualEventPacket event(TOUCHUP);
	pClient->SendPacket(OP_VIRTUALEVENT, event.asByteArray(), event.payloadSize);	

	CStatic::OnLButtonUp(nFlags, point);
}


void CScreen::SetClient(CMyClient * pClient)
{
	this->pClient = pClient;
}


void CScreen::OnMouseMove(UINT nFlags, CPoint point)
{
	// TODO: Add your message handler code here and/or call default		

	if((nFlags & MK_LBUTTON)==MK_LBUTTON)
	{
		if(pClient == NULL)
			return;

		CoordinateTransform(point);
		CVitualEventPacket event(SETCOORDINATES, point.x, point.y);		
		
		pClient->SendPacket(OP_VIRTUALEVENT, event.asByteArray(), event.payloadSize);		
	}	
	CStatic::OnMouseMove(nFlags, point);
}


BOOL CScreen::PreCreateWindow(CREATESTRUCT& cs)
{
	// TODO: Add your specialized code here and/or call the base class

	//더블클릭을 막기 위해서
	WNDCLASS wc;
	memset(&wc, 0, sizeof(wc));
	GetClassInfo(cs.hInstance, cs.lpszClass, &wc);	
	wc.style = wc.style & (~CS_DBLCLKS);	
	cs.lpszClass = AfxRegisterWndClass(wc.style, wc.hCursor, wc.hbrBackground, wc.hIcon);	
	return CStatic::PreCreateWindow(cs);
}


