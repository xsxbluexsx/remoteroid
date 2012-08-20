// ImageDlg.cpp : implementation file
//

#include "stdafx.h"
#include "RemotroidServer.h"
#include "ImageDlg.h"
#include "afxdialogex.h"


// CImageDlg dialog

IMPLEMENT_DYNAMIC(CImageDlg, CDialogEx)

CImageDlg::CImageDlg(UINT nIDTemplate, CWnd *pParent)
	: CDialogEx(nIDTemplate, pParent), pStream(NULL)
	, pResizeDlg(NULL)
	, m_bResizing(FALSE)
	, m_nHitTest(0)
	, m_CurCursorState(0)
{

}

CImageDlg::~CImageDlg()
{
	if(pStream != NULL)
	{
		pStream->Release();
	}
}

void CImageDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialogEx::DoDataExchange(pDX);
}


BEGIN_MESSAGE_MAP(CImageDlg, CDialogEx)
	ON_WM_PAINT()	
	
	ON_WM_NCHITTEST()
	ON_WM_SETCURSOR()
//	ON_WM_NCLBUTTONDOWN()
	ON_WM_NCLBUTTONUP()
	ON_WM_NCMOUSEMOVE()
	ON_WM_LBUTTONUP()
	ON_WM_MOUSEMOVE()
	ON_WM_LBUTTONDOWN()
END_MESSAGE_MAP()


// CImageDlg message handlers


void CImageDlg::OnPaint()
{
	CPaintDC dc(this); // device context for painting
	// TODO: Add your message handler code here
	// Do not call CDialogEx::OnPaint() for painting messages
	CRect rc;
	GetClientRect(&rc);
	OnResizeSkin(&rc);
}


BOOL CImageDlg::OnInitDialog()
{
	CDialogEx::OnInitDialog();

	// TODO:  Add extra initialization here
	///////////////////////////////

	m_bitmap.LoadBitmap(IDB_BITMAP1);
	m_bitmap.GetBitmap(&m_bmp);


	/*
	hResource = FindResource(AfxGetApp()->m_hInstance,
		MAKEINTRESOURCEW(IDB_RESIZE), _T("PNG"));
	
	DWORD imageSize = SizeofResource(AfxGetApp()->m_hInstance, hResource);
	hGlobal = LoadResource(AfxGetApp()->m_hInstance, hResource);
	pData = LockResource(hGlobal);
	hBuffer = GlobalAlloc(GMEM_MOVEABLE, imageSize);
	LPVOID pBuffer = GlobalLock(hBuffer);
	CopyMemory(pBuffer, pData, imageSize);
	CreateStreamOnHGlobal(hBuffer, TRUE, &pStream);
	pImagePng = new Image(pStream);
	*/

	//비트맵 모양에 맞춰서 다이얼로그 모양 만들기	


// 	hResBack = FindResource(AfxGetApp()->m_hInstance, MAKEINTRESOURCE(IDR_RGN1), _T("RGN"));
// 	hBackGlobal = LoadResource(AfxGetApp()->m_hInstance, hResBack);
// 	m_xScale = m_yScale = 1;
// 
// 	if(hBackGlobal)
// 	{
// 		BYTE *rgndata = (BYTE FAR*)LockResource(hBackGlobal);       
// 		
// 		if (rgndata) 
// 		{
// 			HRGN rgn;      
// 			XFORM xform;      
// 			xform.eM11 = (FLOAT) 1;          
// 			xform.eM22 = (FLOAT) 1; 
// 			xform.eM12 = (FLOAT) 0.0;       
// 			xform.eM21 = (FLOAT) 0.0;             
// 			xform.eDx  = (FLOAT) 0;             
// 			xform.eDy  = (FLOAT) 0; 
// 			
// 			rgn = ExtCreateRegion(&xform, sizeof
// 				(RGNDATAHEADER) + (sizeof(RECT) * ((RGNDATA*)rgndata)->rdh.nCount),(RGNDATA*)rgndata);
// 			VERIFY(rgn!=NULL);  // if you want more comprehensive checking - feel free!
// 		/*	::SetWindowRgn(m_hWnd, rgn, TRUE);		*/	
// 			::UnlockResource(hBackGlobal);
// 		}
// 	}
// 	if(hBackGlobal) ::FreeResource(hBackGlobal);
	///////////////////////////////			
	
	
	return TRUE;  // return TRUE unless you set the focus to a control
	// EXCEPTION: OCX Property Pages should return FALSE
}



void CImageDlg::OnResizeSkin(CRect * rc)
{
	CDC *pDC = GetDC();
	CDC MemDC;
	MemDC.CreateCompatibleDC(pDC);	
	
	CBitmap * old = MemDC.SelectObject(&m_bitmap);
	
	pDC->StretchBlt(0,0,rc->Width(), rc->Height(), &MemDC, 0,0,
		m_bmp.bmWidth, m_bmp.bmHeight, SRCCOPY);
	MemDC.SelectObject(old);
}


void CImageDlg::SetResizingDlg(void)
{
	pResizeDlg = new CResizingDlg;
	pResizeDlg->Create(IDD_RESIZING, this);	
	
	GetWindowRect(&baseRect);
	pResizeDlg->MoveWindow(&baseRect);
	pResizeDlg->ShowWindow(SW_HIDE);
}


void CImageDlg::SetDlgPosition(void)
{
	//다이얼로그가 가운데 생성될 수 있도록..
	CWnd * pDesktopWnd = GetDesktopWindow();
	CRect desktopRect;
	pDesktopWnd->GetWindowRect(&desktopRect);
	
	int top = (desktopRect.Height()/2) - (m_bmp.bmHeight/2);
	int left = (desktopRect.Width()/2) - (m_bmp.bmWidth/2);
	MoveWindow(left, top, m_bmp.bmWidth, m_bmp.bmHeight);
}



LRESULT CImageDlg::OnNcHitTest(CPoint point)
{
	// TODO: Add your message handler code here and/or call default

	return CDialogEx::OnNcHitTest(point);
}


BOOL CImageDlg::OnSetCursor(CWnd* pWnd, UINT nHitTest, UINT message)
{
	// TODO: Add your message handler code here and/or call default
	if(nHitTest == HTTOPLEFT || nHitTest == HTTOPRIGHT || 
		nHitTest == HTBOTTOMLEFT || nHitTest == HTBOTTOMRIGHT)
	{
		switch (nHitTest)
		{
		case HTTOPLEFT:
			SetCursor(LoadCursor(NULL, IDC_SIZENWSE));
			break;
		case HTTOPRIGHT:
			SetCursor(LoadCursor(NULL, IDC_SIZENESW));
			break;
		case HTBOTTOMLEFT:
			SetCursor(LoadCursor(NULL, IDC_SIZENESW));
			break;
		case HTBOTTOMRIGHT:
			SetCursor(LoadCursor(NULL, IDC_SIZENWSE));
			break;
		}
		
		return TRUE;
	}
	return CDialogEx::OnSetCursor(pWnd, nHitTest, message);
}

void CImageDlg::OnNcLButtonUp(UINT nHitTest, CPoint point)
{
	// TODO: Add your message handler code here and/or call default
	CDialogEx::OnNcLButtonUp(nHitTest, point);
}


void CImageDlg::OnNcMouseMove(UINT nHitTest, CPoint point)
{
	// TODO: Add your message handler code here and/or call default	
	CDialogEx::OnNcMouseMove(nHitTest, point);
}


void CImageDlg::OnLButtonUp(UINT nFlags, CPoint point)
{
	// TODO: Add your message handler code here and/or call default
	if(m_bResizing)
	{
		m_bResizing = FALSE;
		pResizeDlg->ShowWindow(SW_HIDE);
		ReleaseCapture();
	}
	CDialogEx::OnLButtonUp(nFlags, point);
}


void CImageDlg::OnMouseMove(UINT nFlags, CPoint point)
{
	// TODO: Add your message handler code here and/or call default

	if(!m_bResizing)
	{
		SetSizeCursor(point);
	}
	else
	{
		ClientToScreen(&point);
		pResizeDlg->ResizingDlg(point);			
	}
	
	CDialogEx::OnMouseMove(nFlags, point);
}


void CImageDlg::OnLButtonDown(UINT nFlags, CPoint point)
{
	// TODO: Add your message handler code here and/or call default
	
	if( (m_CurCursorState = SetSizeCursor(point)) != -1)
	{
		ClientToScreen(&point);
		m_bResizing = TRUE;
		pResizeDlg->InitResizingDlg(baseRect, point, m_CurCursorState);
		pResizeDlg->ResizingDlg(point);
		pResizeDlg->ShowWindow(SW_SHOW);
		SetCapture();		
	}	

	CDialogEx::OnLButtonDown(nFlags, point);
}


//마우스 위치가 모서리인지를 파악하고 커서 모양을 바꾼다
int CImageDlg::SetSizeCursor(CPoint point)
{	
	ClientToScreen(&point);
	int result;
	if( (result = CResizingDlg::SearchSide(baseRect, point)) != -1)
	{
		switch (result)
		{
		case HTTOPLEFT:
			SetCursor(LoadCursor(NULL, IDC_SIZENWSE));
			break;
		case HTTOPRIGHT:
			SetCursor(LoadCursor(NULL, IDC_SIZENESW));
			break;
		case HTBOTTOMLEFT:
			SetCursor(LoadCursor(NULL, IDC_SIZENESW));
			break;
		case HTBOTTOMRIGHT:
			SetCursor(LoadCursor(NULL, IDC_SIZENWSE));
			break;
		}
	}
	return result;
}
