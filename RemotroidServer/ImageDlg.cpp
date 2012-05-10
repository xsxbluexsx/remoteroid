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
{
}

CImageDlg::~CImageDlg()
{

}

void CImageDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialogEx::DoDataExchange(pDX);
}


BEGIN_MESSAGE_MAP(CImageDlg, CDialogEx)
	ON_WM_PAINT()	
END_MESSAGE_MAP()


// CImageDlg message handlers


void CImageDlg::OnPaint()
{
	CPaintDC dc(this); // device context for painting
	// TODO: Add your message handler code here
	// Do not call CDialogEx::OnPaint() for painting messages
}


BOOL CImageDlg::OnInitDialog()
{
	CDialogEx::OnInitDialog();

	// TODO:  Add extra initialization here
	///////////////////////////////
	//비트맵 모양에 맞춰서 다이얼로그 모양 만들기
	MoveWindow(0,0,468,846);


	hResBack = FindResource(AfxGetApp()->m_hInstance, MAKEINTRESOURCE(IDR_RGN1), _T("RGN"));
	hBackGlobal = LoadResource(AfxGetApp()->m_hInstance, hResBack);
	m_xScale = m_yScale = 1;

	if(hBackGlobal)
	{
		BYTE *rgndata = (BYTE FAR*)LockResource(hBackGlobal);       
		
		if (rgndata) 
		{
			HRGN rgn;      
			XFORM xform;      
			xform.eM11 = (FLOAT) 1;          
			xform.eM22 = (FLOAT) 1; 
			xform.eM12 = (FLOAT) 0.0;       
			xform.eM21 = (FLOAT) 0.0;             
			xform.eDx  = (FLOAT) 0;             
			xform.eDy  = (FLOAT) 0; 
			
			rgn = ExtCreateRegion(&xform, sizeof
				(RGNDATAHEADER) + (sizeof(RECT) * ((RGNDATA*)rgndata)->rdh.nCount),(RGNDATA*)rgndata);
			VERIFY(rgn!=NULL);  // if you want more comprehensive checking - feel free!
			::SetWindowRgn(m_hWnd, rgn, TRUE);
			::UnlockResource(hBackGlobal);
		}
	}
	if(hBackGlobal) ::FreeResource(hBackGlobal);
	///////////////////////////////
	
	
	
	return TRUE;  // return TRUE unless you set the focus to a control
	// EXCEPTION: OCX Property Pages should return FALSE
}

