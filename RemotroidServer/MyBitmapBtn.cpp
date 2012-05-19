// MyBitmapBtn.cpp : implementation file
//

#include "stdafx.h"
#include "RemotroidServer.h"
#include "MyBitmapBtn.h"



// CMyBitmapBtn

IMPLEMENT_DYNAMIC(CMyBitmapBtn, CBitmapButton)

CMyBitmapBtn::CMyBitmapBtn()
{	
	m_bMouseHover				= FALSE;
	m_nIDBitmapResourceHover	= 0;
	ZeroMemory(&m_stTrackMouse, sizeof(m_stTrackMouse));
}

CMyBitmapBtn::~CMyBitmapBtn()
{
}


BEGIN_MESSAGE_MAP(CMyBitmapBtn, CBitmapButton)
	ON_WM_ERASEBKGND()
	ON_WM_MOUSEMOVE()	
	ON_WM_CREATE()
	ON_WM_MOUSELEAVE()
END_MESSAGE_MAP()



// CMyBitmapBtn message handlers
void CMyBitmapBtn::DrawItem(LPDRAWITEMSTRUCT lpDIS)
{
	ASSERT(lpDIS != NULL);
	// must have at least the first bitmap loaded before calling DrawItem
	ASSERT(m_bitmap.m_hObject != NULL);     // required

	// use the main bitmap for up, the selected bitmap for down
	CBitmap* pBitmap = &m_bitmap;
	UINT state = lpDIS->itemState;
	if ((state & ODS_SELECTED) && m_bitmapSel.m_hObject != NULL)
		pBitmap = &m_bitmapSel;	
	else if ((state & ODS_FOCUS) && m_bitmapFocus.m_hObject != NULL)
		pBitmap = &m_bitmapFocus;   // third image for focused
	else if ((state & ODS_DISABLED) && m_bitmapDisabled.m_hObject != NULL)
		pBitmap = &m_bitmapDisabled;   // last image for disabled
	else if ((TRUE == m_bMouseHover) && NULL != m_cBitmapHover.GetSafeHandle())
		pBitmap = &m_cBitmapHover;
	
	
	// draw the whole button
	CDC* pDC = CDC::FromHandle(lpDIS->hDC);
	CDC memDC;
	memDC.CreateCompatibleDC(pDC);
	CBitmap* pOld = memDC.SelectObject(pBitmap);
	if (pOld == NULL)
		return;     // destructors will clean up

	CRect rect;
	rect.CopyRect(&lpDIS->rcItem);
	pDC->BitBlt(rect.left, rect.top, rect.Width(), rect.Height(),
		&memDC, 0, 0, SRCCOPY);
	memDC.SelectObject(pOld);	
}


BOOL CMyBitmapBtn::OnEraseBkgnd(CDC* pDC)
{
	return TRUE;
}


void CMyBitmapBtn::OnMouseMove(UINT nFlags, CPoint point)
{
	if (FALSE == m_bMouseHover)
	{
		m_stTrackMouse.cbSize		= sizeof(TRACKMOUSEEVENT);
		m_stTrackMouse.dwFlags		= TME_LEAVE;
		m_stTrackMouse.dwHoverTime	= HOVER_DEFAULT;
		m_stTrackMouse.hwndTrack	= m_hWnd;
		::_TrackMouseEvent(&m_stTrackMouse);
		m_bMouseHover = TRUE;
		Invalidate(FALSE);		
	}
	
	CBitmapButton::OnMouseMove(nFlags, point);
}


VOID CMyBitmapBtn::SetHoverBitmapID(IN UINT nIDBitmapResourceHover)
{
	m_nIDBitmapResourceHover = nIDBitmapResourceHover;
	if (0 != m_nIDBitmapResourceHover)
	{
		m_cBitmapHover.DeleteObject();
		m_cBitmapHover.LoadBitmap(m_nIDBitmapResourceHover);
	}
}
int CMyBitmapBtn::OnCreate(LPCREATESTRUCT lpCreateStruct)
{
	if (CBitmapButton::OnCreate(lpCreateStruct) == -1)
		return -1;	
	return 0;
}

void CMyBitmapBtn::OnMouseLeave()
{
	// TODO: Add your message handler code here and/or call default
	m_bMouseHover = FALSE;
	Invalidate(FALSE);
	CBitmapButton::OnMouseLeave();	
}
