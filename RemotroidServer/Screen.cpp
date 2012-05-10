// Screen.cpp : implementation file
//

#include "stdafx.h"
#include "RemotroidServer.h"
#include "Screen.h"


// CScreen

IMPLEMENT_DYNAMIC(CScreen, CStatic)

CScreen::CScreen()
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
	TCHAR temp[100];
	wsprintf(temp, _T("x:%d, y:%d"), point.x, point.y);
	MessageBox(temp);
	CStatic::OnLButtonDown(nFlags, point);
}
