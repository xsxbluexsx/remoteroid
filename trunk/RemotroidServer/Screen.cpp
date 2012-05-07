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


void CScreen::SetJpgInfo(char * data)
{	
	drawJpg.SetJpgInfo(data);
}


void CScreen::RecvJpgData(char *data, int packetSize)
{	
	drawJpg.RecvJpgData(data, packetSize);
}


