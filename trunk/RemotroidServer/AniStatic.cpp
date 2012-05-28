// AniStatic.cpp : implementation file
//

#include "stdafx.h"
#include "RemotroidServer.h"
#include "AniStatic.h"
#include <atlimage.h>

// CAniStatic

IMPLEMENT_DYNAMIC(CAniStatic, CStatic)

CAniStatic::CAniStatic()
: alpahValue(255), pos(5)
{
}

CAniStatic::~CAniStatic()
{
}


BEGIN_MESSAGE_MAP(CAniStatic, CStatic)		
	ON_WM_PAINT()
	ON_WM_CREATE()
	ON_WM_TIMER()
END_MESSAGE_MAP()



// CAniStatic message handlers


void CAniStatic::OnPaint()
{
	CPaintDC dc(this); // device context for painting
	// TODO: Add your message handler code here
	// Do not call CStatic::OnPaint() for painting messages
	
	m_Img.AlphaBlend(dc.m_hDC,0,0,alpahValue);	
}


int CAniStatic::OnCreate(LPCREATESTRUCT lpCreateStruct)
{
	if (CStatic::OnCreate(lpCreateStruct) == -1)
		return -1;

	// TODO:  Add your specialized creation code here	
	m_Img.LoadFromResource(AfxGetInstanceHandle(), IDB_BITMAPWAITING3);
	return 0;
}


void CAniStatic::SetAnimation(BOOL cond)
{
	if(cond)
		SetTimer(0, 30, NULL);
	else
		KillTimer(0);
}


void CAniStatic::OnTimer(UINT_PTR nIDEvent)
{
	// TODO: Add your message handler code here and/or call default
	
	if(alpahValue == 255 || alpahValue == 0)
	{
		pos = pos * -1;		
	}	
	alpahValue += pos;	
	
	GetParent()->InvalidateRect(&myRect, TRUE);
	CStatic::OnTimer(nIDEvent);
}
