// ResizingDlg.cpp : implementation file
//

#include "stdafx.h"
#include "RemotroidServer.h"
#include "ResizingDlg.h"
#include "afxdialogex.h"


// CResizingDlg dialog

IMPLEMENT_DYNAMIC(CResizingDlg, CDialogEx)

CResizingDlg::CResizingDlg(CWnd* pParent /*=NULL*/)
	: CDialogEx(CResizingDlg::IDD, pParent)
{

}

CResizingDlg::~CResizingDlg()
{
}

void CResizingDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialogEx::DoDataExchange(pDX);
}


BEGIN_MESSAGE_MAP(CResizingDlg, CDialogEx)
	ON_WM_WINDOWPOSCHANGING()
	ON_WM_NCLBUTTONUP()

	ON_WM_NCLBUTTONDOWN()
	ON_WM_LBUTTONUP()
	ON_WM_MOUSEMOVE()
END_MESSAGE_MAP()


// CResizingDlg message handlers


BOOL CResizingDlg::OnInitDialog()
{
	CDialogEx::OnInitDialog();

	// TODO:  Add extra initialization here


	COLORREF cr = GetSysColor(COLOR_BTNFACE);
	SetLayeredWindowAttributes(cr, 0, LWA_COLORKEY);

	return TRUE;  // return TRUE unless you set the focus to a control
	// EXCEPTION: OCX Property Pages should return FALSE
}



void CResizingDlg::OnWindowPosChanging(WINDOWPOS* lpwndpos)
{
	CDialogEx::OnWindowPosChanging(lpwndpos);
	lpwndpos->cx = (int)((float)(lpwndpos->cy)*0.55);
	// TODO: Add your message handler code here
}


int CResizingDlg::SearchSide(CRect rc, CPoint point)
{
	long x, y;

    x = point.x;// + rc.left;
    y = point.y;// + rc.top;

	if((rc.left <= x) && (x <= rc.left+SIDE))
    {
        if((rc.top <= y) && (y <= rc.top+SIDE))
        {
            return HTTOPLEFT;
        }
        if((rc.bottom >= y) && (y >= rc.bottom-SIDE))
        {			
            return HTBOTTOMLEFT;
        }

        return -1;
    }

    if((rc.right >= x) && (x >= rc.right-SIDE))
    {
        if((rc.top <= y) && (y <= rc.top+SIDE))
        {
            return HTTOPRIGHT;
        }
        if((rc.bottom >= y) && (y >= rc.bottom-SIDE))
        {			
            return HTBOTTOMRIGHT;
        }

        return -1;
    }    
	
	return -1;
}


void CResizingDlg::OnNcLButtonUp(UINT nHitTest, CPoint point)
{
	// TODO: Add your message handler code here and/or call default
	TRACE(_T("OnNcLButtonUp\n"));
	
	CDialogEx::OnNcLButtonUp(nHitTest, point);
}




void CResizingDlg::OnNcLButtonDown(UINT nHitTest, CPoint point)
{
	// TODO: Add your message handler code here and/or call default		
	TRACE(_T("OnNcLButtonDown\n"));
	CDialogEx::OnNcLButtonDown(nHitTest, point);
}


void CResizingDlg::OnLButtonUp(UINT nFlags, CPoint point)
{
	// TODO: Add your message handler code here and/or call default
	//ReleaseCapture();
	//ShowWindow(SW_HIDE);
	TRACE(_T("OnLButtonUp"));
	CDialogEx::OnLButtonUp(nFlags, point);
}


void CResizingDlg::OnMouseMove(UINT nFlags, CPoint point)
{
	// TODO: Add your message handler code here and/or call default
	
	//PostMessage(WM_NCMOUSEMOVE, HTBOTTOMRIGHT, MAKELPARAM(point.x, point.y));
	TRACE(_T("OnMouseMove"));
	CDialogEx::OnMouseMove(nFlags, point);
}
