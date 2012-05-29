
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
, m_bTrack(FALSE)
, m_strMyIp(_T(""))
, m_isConnect(FALSE)
{
	m_bkgImg.LoadFromResource(AfxGetInstanceHandle(), IDB_BITMAP_WAITING);		
	ZeroMemory(&lf, sizeof(lf));
	lf.lfHeight = 45;
	lf.lfWeight = FW_BOLD;
	wsprintf(lf.lfFaceName, _T("HY견고딕"));
	
	newFont.CreateFontIndirect(&lf);
}

CScreen::~CScreen()
{	
	newFont.DeleteObject();
}


BEGIN_MESSAGE_MAP(CScreen, CStatic)
	ON_WM_DESTROY()
	ON_MESSAGE(WM_RECVJPGINFO, OnSetJpgInfo)
	ON_MESSAGE(WM_RECVJPGDATA, OnRecvJpgData)	
	ON_MESSAGE(WM_RECVDEVICEINFO, OnSetResolution)
	ON_WM_LBUTTONDOWN()
	ON_WM_LBUTTONUP()
	ON_WM_MOUSEMOVE()		
	ON_WM_MOUSELEAVE()
	ON_WM_PAINT()
	ON_WM_CREATE()
		
	ON_WM_MOUSEWHEEL()
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

void CScreen::SetJpgInfo(char *data)
{
	drawJpg.SetJpgInfo(data);
}


LRESULT CScreen::OnRecvJpgData(WPARAM wParam, LPARAM lParam)
{	
	int packetSize = wParam;
	char *data = (char *)lParam;
	drawJpg.RecvJpgData(data, packetSize);
	return 0;
}



void CScreen::RecvJpgData(char * data, int iPacketSize)
{
	drawJpg.RecvJpgData(data, iPacketSize);
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
	
	if(m_bTrack)
	{
		TRACE("cancel\n");
		TRACKMOUSEEVENT MouseEvent;
		memset(&MouseEvent, 0, sizeof(0));
		MouseEvent.cbSize = sizeof(MouseEvent);
		MouseEvent.dwFlags = TME_CANCEL|TME_LEAVE;
		MouseEvent.hwndTrack = m_hWnd;

		::_TrackMouseEvent(&MouseEvent);
		m_bTrack = FALSE;		
	}
		
		
	CVitualEventPacket event(TOUCHUP);
	pClient->SendPacket(OP_VIRTUALEVENT, event.asByteArray(), event.payloadSize);	

	CStatic::OnLButtonUp(nFlags, point);
}


void CScreen::SetClient(CMyClient * pClient)
{
	m_isConnect = TRUE;
	aniWait.SetAnimation(FALSE);

	this->pClient = pClient;	
}


void CScreen::OnMouseMove(UINT nFlags, CPoint point)
{
	// TODO: Add your message handler code here and/or call default		

	if((nFlags & MK_LBUTTON)==MK_LBUTTON)
	{
		if(pClient == NULL)
			return;

		if(m_bTrack == FALSE)
		{
			//////마우스 추적 시작
			TRACE("start tracl\n");
			TRACKMOUSEEVENT MouseEvent;
			memset(&MouseEvent, 0, sizeof(0));
			MouseEvent.cbSize = sizeof(MouseEvent);
			MouseEvent.dwFlags = TME_LEAVE;
			MouseEvent.hwndTrack = m_hWnd;
			MouseEvent.dwHoverTime = 0;
			m_bTrack = ::_TrackMouseEvent(&MouseEvent);
		}

		CoordinateTransform(point);
		CVitualEventPacket event(SETCOORDINATES, point.x, point.y);		
		
		pClient->SendPacket(OP_VIRTUALEVENT, event.asByteArray(), event.payloadSize);		
	}	
	CStatic::OnMouseMove(nFlags, point);
}



//마우스가 벗어나면 Up 이벤트 전송해야 한다
void CScreen::OnMouseLeave()
{
	// TODO: Add your message handler code here and/or call default		
	if(pClient == NULL)
		return;

	if(m_bTrack)
	{
		TRACE("leave\n");
		TRACKMOUSEEVENT MouseEvent;
		memset(&MouseEvent, 0, sizeof(0));
		MouseEvent.cbSize = sizeof(MouseEvent);
		MouseEvent.dwFlags = TME_CANCEL|TME_LEAVE;
		MouseEvent.hwndTrack = m_hWnd;

		::_TrackMouseEvent(&MouseEvent);
		m_bTrack = FALSE;

		CVitualEventPacket event(TOUCHUP);
		pClient->SendPacket(OP_VIRTUALEVENT, event.asByteArray(), event.payloadSize);	
	}	
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


void CScreen::OnPaint()
{
	CPaintDC dc(this); // device context for painting
	// TODO: Add your message handler code here
	// Do not call CStatic::OnPaint() for painting messages
	if(m_isConnect)
		return;
	
	CDC *pDC = CDC::FromHandle(m_bkgImg.GetDC());
		
	CFont *pOldFont = pDC->SelectObject(&newFont);

	pDC->SetTextColor(RGB(255,255,255));
	pDC->SetBkMode(TRANSPARENT);
	pDC->TextOut(40,250, m_strMyIp);

	dc.SelectObject(pOldFont);
	
	m_bkgImg.ReleaseDC();
	m_bkgImg.BitBlt(dc.m_hDC, 0, 0);	
}


int CScreen::OnCreate(LPCREATESTRUCT lpCreateStruct)
{
	if (CStatic::OnCreate(lpCreateStruct) == -1)
		return -1;

	// TODO:  Add your specialized creation code here
	aniWait.Create(_T(""), WS_CHILD|WS_VISIBLE,CRect(60,330,290,500), this, 0);
	aniWait.myRect = CRect(80,350,280,480);
	aniWait.SetAnimation(TRUE);
	
	return 0;
}


void CScreen::SetDisconnect()
{
	m_isConnect = FALSE;
	RedrawWindow();
	aniWait.SetAnimation(TRUE);
}





BOOL CScreen::OnMouseWheel(UINT nFlags, short zDelta, CPoint pt)
{
	// TODO: Add your message handler code here and/or call default
	TRACE("nFlgs : %d, zDelta : %d, x : %d, y : %d", nFlags, zDelta, pt.x, pt.y);
	return CStatic::OnMouseWheel(nFlags, zDelta, pt);
}


BOOL CScreen::PreTranslateMessage(MSG* pMsg)
{
	// TODO: Add your specialized code here and/or call the base class
	if(pMsg->message == WM_MOUSEHWHEEL)
	{
		SendMessage(pMsg->message, pMsg->wParam, pMsg->lParam);
	}
	return CStatic::PreTranslateMessage(pMsg);
}
