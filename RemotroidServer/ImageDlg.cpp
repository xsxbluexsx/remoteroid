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
	hResource = FindResource(AfxGetApp()->m_hInstance, MAKEINTRESOURCE(IDB_PNG1), _T("PNG"));
	DWORD imageSize = SizeofResource(AfxGetApp()->m_hInstance, hResource);
	hGlobal = LoadResource(AfxGetApp()->m_hInstance, hResource);
	//리소스를 메모리에 로드
	pData = LockResource(hGlobal);
	//메모리에 로드된 리소스의 포인터 획득
	hBuffer = GlobalAlloc(GMEM_MOVEABLE, imageSize);
	//이미지 싸이즈 만큼 버퍼 획득
	LPVOID pBuffer = GlobalLock(hBuffer);
	//획득한 버퍼의 포인터
	CopyMemory(pBuffer, pData, imageSize);
	CreateStreamOnHGlobal(hBuffer, TRUE, &pStream);
	pImagePng = new Image(pStream);
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
END_MESSAGE_MAP()


// CImageDlg message handlers


void CImageDlg::OnPaint()
{
	CPaintDC dc(this); // device context for painting
	// TODO: Add your message handler code here
	// Do not call CDialogEx::OnPaint() for painting messages
	if(pImagePng->GetLastStatus() != Ok)
	{
		return;
	}

	Graphics gdip(dc);
	gdip.DrawImage(pImagePng, 0,0, 360, 600);
}


BOOL CImageDlg::OnInitDialog()
{
	CDialogEx::OnInitDialog();

	// TODO:  Add extra initialization here
	CRgn		m_Rgn;

	MoveWindow(0,0,360,600);

	CRect rc;
	GetClientRect(&rc);

	SetBackgroundColor(RGB(10,9,8));
	
	SetWindowLong(GetSafeHwnd(), GWL_EXSTYLE,
		GetWindowLong(GetSafeHwnd(), GWL_EXSTYLE) | WS_EX_LAYERED);
	
	SetLayeredWindowAttributes(RGB(10,9,8),0,LWA_COLORKEY);
	
	m_Rgn.CreateRoundRectRgn(rc.left+13, 
		rc.top+4, rc.right-15, rc.bottom-3, 100, 100);

	SetWindowRgn((HRGN)m_Rgn, TRUE);

	
	return TRUE;  // return TRUE unless you set the focus to a control
	// EXCEPTION: OCX Property Pages should return FALSE
}

