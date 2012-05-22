
// Screen.cpp : implementation file
//

#include "stdafx.h"
#include "RemotroidServer.h"
#include "Screen.h"


// CScreen

IMPLEMENT_DYNAMIC(CScreen, CStatic)

CScreen::CScreen()
: pClient(NULL)
{	
}

CScreen::~CScreen()
{	
	
}


BEGIN_MESSAGE_MAP(CScreen, CStatic)
	ON_WM_DESTROY()
	ON_MESSAGE(WM_RECVJPGINFO, OnSetJpgInfo)
	ON_MESSAGE(WM_RECVJPGDATA, OnRecvJpgData)	
	ON_WM_LBUTTONDOWN()


	ON_WM_LBUTTONUP()
	ON_WM_MOUSEMOVE()
END_MESSAGE_MAP()
 


// CScreen message handlers




void CScreen::InitDrawJpg(void)
{
	CRect rect;
	GetClientRect(&rect);	
	drawJpg.InitDrawJpg(GetSafeHwnd(), rect.Width(), rect.Height());	
}


void CScreen::OnDestroy()
{
	CStatic::OnDestroy();

	// TODO: Add your message handler code here	
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



void CScreen::OnLButtonDown(UINT nFlags, CPoint point)
{
	// TODO: Add your message handler code here and/or call default
	if(pClient == NULL)
		return;


	TRACE("\t down %d %d\n", point.x, point.y);

	CVitualEventPacket event(TOUCHDOWN, point.x, point.y);
	pClient->SendPacket(OP_VIRTUALEVENT, event.asByteArray(), event.payloadSize);

	CStatic::OnLButtonDown(nFlags, point);
}



void CScreen::OnLButtonUp(UINT nFlags, CPoint point)
{
	// TODO: Add your message handler code here and/or call default
	if(pClient == NULL)
		return;

	TRACE("\t up %d %d\n", point.x, point.y);

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

		CVitualEventPacket event(SETCOORDINATES, point.x, point.y);
		
		int err = pClient->SendPacket(OP_VIRTUALEVENT, event.asByteArray(), event.payloadSize);
		TRACE("\t %d %d %d\n", point.x, point.y, err);
	}
	CStatic::OnMouseMove(nFlags, point);
}
