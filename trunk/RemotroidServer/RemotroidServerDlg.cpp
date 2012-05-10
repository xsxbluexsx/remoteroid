
// RemotroidServerDlg.cpp : implementation file
//

#include "stdafx.h"
#include "RemotroidServer.h"
#include "RemotroidServerDlg.h"
#include "afxdialogex.h"

#include "RecvFile.h"




#ifdef _DEBUG
#define new DEBUG_NEW
#endif


// CAboutDlg dialog used for App About

class CAboutDlg : public CDialogEx
{
public:
	CAboutDlg();

// Dialog Data
	enum { IDD = IDD_ABOUTBOX };

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support

// Implementation
protected:
	DECLARE_MESSAGE_MAP()
};

CAboutDlg::CAboutDlg() : CDialogEx(CAboutDlg::IDD)
{
}

void CAboutDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialogEx::DoDataExchange(pDX);
}

BEGIN_MESSAGE_MAP(CAboutDlg, CDialogEx)
END_MESSAGE_MAP()


// CRemotroidServerDlg dialog




CRemotroidServerDlg::CRemotroidServerDlg(CWnd* pParent /*=NULL*/)
	: CImageDlg(CRemotroidServerDlg::IDD, pParent)	
	, m_pClient(NULL)
	, pRecvThread(NULL)
	, m_isClickedEndBtn(FALSE)
	, pAcceptThread(NULL)
	, pUdpRecvThread(NULL)
{
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
	
}

void CRemotroidServerDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialogEx::DoDataExchange(pDX);
	//DDX_Control(pDX, IDC_SCREEN, screen);
}

BEGIN_MESSAGE_MAP(CRemotroidServerDlg, CDialogEx)
	ON_WM_SYSCOMMAND()
	ON_WM_PAINT()
	ON_WM_QUERYDRAGICON()
	ON_WM_DESTROY()
	ON_BN_CLICKED(IDOK, &CRemotroidServerDlg::OnBnClickedOk)
	ON_BN_CLICKED(IDCANCEL, &CRemotroidServerDlg::OnBnClickedCancel)	
	ON_WM_DROPFILES()
	ON_MESSAGE(WM_RECVJPGINFO, OnRecvJpgInfo)
	ON_MESSAGE(WM_RECVJPGDATA, OnRecvJpgData)
	ON_MESSAGE(WM_MYENDRECV, OnEndRecv)
	ON_MESSAGE(WM_MYENDACCEPT, OnEndAccept)
	ON_BN_CLICKED(IDC_FILESENDER, &CRemotroidServerDlg::OnBnClickedFilesender)
	ON_WM_MOUSEMOVE()
	ON_WM_CTLCOLOR()
	
END_MESSAGE_MAP()


// CRemotroidServerDlg message handlers

BOOL CRemotroidServerDlg::OnInitDialog()
{
	CImageDlg::OnInitDialog();

	// Add "About..." menu item to system menu.

	// IDM_ABOUTBOX must be in the system command range.
	ASSERT((IDM_ABOUTBOX & 0xFFF0) == IDM_ABOUTBOX);
	ASSERT(IDM_ABOUTBOX < 0xF000);

	CMenu* pSysMenu = GetSystemMenu(FALSE);
	if (pSysMenu != NULL)
	{
		BOOL bNameValid;
		CString strAboutMenu;
		bNameValid = strAboutMenu.LoadString(IDS_ABOUTBOX);
		ASSERT(bNameValid);
		if (!strAboutMenu.IsEmpty())
		{
			pSysMenu->AppendMenu(MF_SEPARATOR);
			pSysMenu->AppendMenu(MF_STRING, IDM_ABOUTBOX, strAboutMenu);
		}
	}

	// Set the icon for this dialog.  The framework does this automatically
	//  when the application's main window is not a dialog
	SetIcon(m_hIcon, TRUE);			// Set big icon
	SetIcon(m_hIcon, FALSE);		// Set small icon

	// TODO: Add extra initialization here
	screen.Create(NULL, WS_CHILD|WS_VISIBLE|WS_BORDER|SS_NOTIFY, CRect(42,106,402,704), this, 1234);




	m_UDPServerSocket = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
	if(m_UDPServerSocket == INVALID_SOCKET)
	{
		MessageBox(_T("UDP소켓 생성 실패"));
		EndDialog(IDCANCEL);
		return TRUE;
	}

	SOCKADDR_IN addr;
	memset(&addr, 0, sizeof(addr));
	addr.sin_addr.s_addr = htonl(INADDR_ANY);
	addr.sin_family = AF_INET;
	addr.sin_port = htons(UDPPORT);

	if(bind(m_UDPServerSocket, (sockaddr *)&addr, sizeof(addr)) == SOCKET_ERROR )
	{
		MessageBox(_T("udp bind error"));
		EndDialog(IDCANCEL);
		return TRUE;
	}

	pUdpRecvThread = AfxBeginThread(UDPRecvFunc, this);
	//pUdpRecvThread->m_bAutoDelete = FALSE;

	m_ServerSocket = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
	if(m_ServerSocket == INVALID_SOCKET)
	{
		MessageBox(_T("Server Socket Error"));
		EndDialog(IDCANCEL);
		return TRUE;
	}
	
	memset(&addr, 0, sizeof(addr));
	addr.sin_addr.s_addr = htonl(INADDR_ANY);
	addr.sin_family = AF_INET;
	addr.sin_port = htons(PORT);

	if(bind(m_ServerSocket, (sockaddr*)&addr, sizeof(addr)) == SOCKET_ERROR)
	{
		MessageBox(_T("bind error"));
		EndDialog(IDCANCEL);
		return TRUE;
	}

	if(listen(m_ServerSocket , SOMAXCONN) == SOCKET_ERROR)
	{
		MessageBox(_T("listen error"));
		EndDialog(IDCANCEL);
		return TRUE;
	}

	pAcceptThread = AfxBeginThread(AcceptFunc, this);	
	pAcceptThread->m_bAutoDelete = FALSE;

	return TRUE;  // return TRUE  unless you set the focus to a control
}

void CRemotroidServerDlg::OnSysCommand(UINT nID, LPARAM lParam)
{
	if ((nID & 0xFFF0) == IDM_ABOUTBOX)
	{
		CAboutDlg dlgAbout;
		dlgAbout.DoModal();
	}
	else
	{
		CDialogEx::OnSysCommand(nID, lParam);
	}
}

// If you add a minimize button to your dialog, you will need the code below
//  to draw the icon.  For MFC applications using the document/view model,
//  this is automatically done for you by the framework.

void CRemotroidServerDlg::OnPaint()
{
	if (IsIconic())
	{
		CPaintDC dc(this); // device context for painting

		SendMessage(WM_ICONERASEBKGND, reinterpret_cast<WPARAM>(dc.GetSafeHdc()), 0);

		// Center icon in client rectangle
		int cxIcon = GetSystemMetrics(SM_CXICON);
		int cyIcon = GetSystemMetrics(SM_CYICON);
		CRect rect;
		GetClientRect(&rect);
		int x = (rect.Width() - cxIcon + 1) / 2;
		int y = (rect.Height() - cyIcon + 1) / 2;

		// Draw the icon
		dc.DrawIcon(x, y, m_hIcon);
	}
	else
	{		
		CImageDlg::OnPaint();
	}
}

// The system calls this function to obtain the cursor to display while the user drags
//  the minimized window.
HCURSOR CRemotroidServerDlg::OnQueryDragIcon()
{
	return static_cast<HCURSOR>(m_hIcon);
}


UINT CRemotroidServerDlg::AcceptFunc(LPVOID pParam)
{
	CRemotroidServerDlg *pDlg = (CRemotroidServerDlg *)pParam;

	SOCKADDR_IN addr;
	memset(&addr, 0, sizeof(addr));
	int iAddrLen = sizeof(addr);
	SOCKET ClientSocket = accept(pDlg->m_ServerSocket, (sockaddr*)&addr, &iAddrLen);
	
	if(ClientSocket == INVALID_SOCKET)
	{		
		return 0;
	}
	CMyClient *pClient = new CMyClient(ClientSocket);
	pDlg->SetClientSocket(pClient);
	pDlg->pRecvThread = AfxBeginThread(RecvFunc, pDlg);
	pDlg->pRecvThread->m_bAutoDelete = FALSE;
	
	if(pDlg->m_isClickedEndBtn == FALSE)
	{
		pDlg->PostMessage(WM_MYENDACCEPT, 0, 0);
	}
	return 0;
}


//화면 수신은 UDP를 통해서 한다.
UINT CRemotroidServerDlg::UDPRecvFunc(LPVOID pParam)
{
	CRemotroidServerDlg *pDlg = (CRemotroidServerDlg *)pParam;
	char packet[MAXSIZE];
	SOCKADDR_IN addr;
	memset(&addr, 0, sizeof(addr));
	int iAddrLen = sizeof(addr);
	while (TRUE)
	{
		memset(packet, 0, sizeof(packet));
		int iRecvLen = recvfrom(pDlg->m_UDPServerSocket, packet, MAXSIZE, NULL, (sockaddr*)&addr, &iAddrLen);
				
		if(iRecvLen < 0)
		{
			break;
		}
		int iOPCode = CUtil::GetOpCode(packet);
		int iPacketSize = CUtil::GetPacketSize(packet);

		char *data = packet+HEADERSIZE;

		switch(iOPCode)
		{
		case OP_SENDJPGINFO:
			//pDlg->SendMessage(WM_RECVJPGINFO, 0, (LPARAM)data);				
			//pDlg->screen.SetJpgInfo(data);
			pDlg->screen.SendMessage(WM_RECVJPGINFO, 0, (LPARAM)data);
			break;
		case OP_SENDJPGDATA:
			//pDlg->SendMessage(WM_RECVJPGDATA, (WPARAM)iPacketSize, (LPARAM)data);
			//pDlg->screen.RecvJpgData(data, iPacketSize);
			pDlg->screen.SendMessage(WM_RECVJPGDATA, iPacketSize, (LPARAM)data);
			break;
		}
	}	
	return 0;
}



UINT CRemotroidServerDlg::RecvFunc(LPVOID pParam)
{	
	CRemotroidServerDlg *pDlg = (CRemotroidServerDlg *)pParam;
	CMyClient *pClient = pDlg->GetClientSocket();

	char bPacket[MAXSIZE];
	CRecvFile recvFileClass;	
	
	while (TRUE)
	{
		memset(bPacket, 0, sizeof(bPacket));
		int iRecvLen = pClient->RecvPacket();		
		if(iRecvLen <= 0)
		{
			TRACE("recvlen <= 0 \n");
			break;
		}
		while(pClient->GetPacket(bPacket))
		{
			int iOPCode = CUtil::GetOpCode(bPacket);
			int iPacketSize = CUtil::GetPacketSize(bPacket);
			char *data = bPacket+HEADERSIZE;

			switch(iOPCode)
			{
			case OP_SENDFILEINFO:
				recvFileClass.RecvFileInfo(data);
				break;
			case OP_SENDFILEDATA:
				recvFileClass.RecvFileData(data, iPacketSize);				
				break;		
			case OP_SENDJPGINFO:
				//pDlg->SendMessage(WM_RECVJPGINFO, 0, (LPARAM)data);				
				//pDlg->screen.SetJpgInfo(data);
				pDlg->screen.SendMessage(WM_RECVJPGINFO, 0, (LPARAM)data);
				break;
			case OP_SENDJPGDATA:
				//pDlg->SendMessage(WM_RECVJPGDATA, (WPARAM)iPacketSize, (LPARAM)data);
				//pDlg->screen.RecvJpgData(data, iPacketSize);
				pDlg->screen.SendMessage(WM_RECVJPGDATA, iPacketSize, (LPARAM)data);
				break;
			}
		}
	}
	delete pClient;	

	//종료 버튼을 통한 종료가 아닌 클라이언트 접속종료
	if(pDlg->m_isClickedEndBtn == FALSE)
	{
		pDlg->PostMessage(WM_MYENDRECV, 0, 0);
	}
	return 0;
}


void CRemotroidServerDlg::SetClientSocket(CMyClient * pClient)
{
	m_pClient = pClient;
	fileSender.SetClient(pClient);
	screen.InitDrawJpg();
}

CMyClient * CRemotroidServerDlg::GetClientSocket(void)
{
	return m_pClient;
}





void CRemotroidServerDlg::OnDestroy()
{
	CDialogEx::OnDestroy();	
	// TODO: Add your message handler code here	
	m_isClickedEndBtn = TRUE;
	EndAccept();
	EndConnect();
}


void CRemotroidServerDlg::OnBnClickedOk()
{
	// TODO: Add your control notification handler code here	
	CDialogEx::OnOK();
}


void CRemotroidServerDlg::OnBnClickedCancel()
{
	// TODO: Add your control notification handler code here
	CDialogEx::OnCancel();
}


void CRemotroidServerDlg::OnDropFiles(HDROP hDropInfo)
{
	// TODO: Add your message handler code here and/or call default
	TCHAR path[MAX_PATH];
	int count = 0;

	count = DragQueryFile(hDropInfo, 0xffffffff, NULL, 0);
	for(int i=0; i<count; i++)
	{
		CFile *pFile; 
		DragQueryFile(hDropInfo, i, path, MAX_PATH);
		TRY 
		{
			pFile = new CFile(path, CFile::modeRead | CFile::shareDenyRead);
		}
		CATCH (CFileException, e)
		{
			MessageBox(_T("다른 프로그램에서 사용중입니다"));
			break;
		}
		END_CATCH
		
		if(FALSE == fileSender.AddSendFile(pFile))
		{
			return;
		}			
	}
	fileSender.StartSendFile();
	CDialogEx::OnDropFiles(hDropInfo);
}


LRESULT CRemotroidServerDlg::OnRecvJpgInfo(WPARAM wParam, LPARAM lParam)
{
// 	char *data = (char*)lParam;
// 	screen.SetJpgInfo(data);
	return LRESULT();
}


LRESULT CRemotroidServerDlg::OnRecvJpgData(WPARAM wParam, LPARAM lParam)
{
// 	int packetSize = wParam;
// 	char *data = (char *)lParam;
// 	screen.RecvJpgData(data, packetSize);
	return LRESULT();
}

//////////////////////////////////////////////////////////////////
////쓰레드 정상 종료를 위한 함수들
void CRemotroidServerDlg::EndAccept(void)
{
	if(pAcceptThread == NULL)
	{
		return;
	}

	closesocket(m_ServerSocket);
	WaitForSingleObject(pAcceptThread->m_hThread, 100);
	delete pAcceptThread;
	pAcceptThread = NULL;
}

void CRemotroidServerDlg::EndConnect(void)
{
	if(pRecvThread == NULL)
		return;

	m_pClient->CloseSocket();
	WaitForSingleObject(pRecvThread->m_hThread, 100);
	delete pRecvThread;
	pRecvThread = NULL;
}


//클라이언트 접속 종료로 인한 recv 쓰레드 종료시 호출
LRESULT CRemotroidServerDlg::OnEndRecv(WPARAM wParam, LPARAM lParam)
{
	WaitForSingleObject(pRecvThread->m_hThread, 100);	
	delete pRecvThread;
	pRecvThread = NULL;

	pAcceptThread = AfxBeginThread(AcceptFunc, this);
	pAcceptThread->m_bAutoDelete = FALSE;
	return LRESULT();
}

LRESULT CRemotroidServerDlg::OnEndAccept(WPARAM wParam, LPARAM lParam)
{
	WaitForSingleObject(pAcceptThread->m_hThread, 100);
	delete pAcceptThread;
	pAcceptThread = NULL;
	return LRESULT();
}

////쓰레드 정상 종료를 위한 함수들
//////////////////////////////////////////////////////////////////



void CRemotroidServerDlg::OnBnClickedFilesender()
{
	// TODO: Add your control notification handler code here
	//closesocket(m_ServerSocket);
	m_pClient->SendPacket(3,"",0);
}



//다이얼로그 이동을 위한..
void CRemotroidServerDlg::OnMouseMove(UINT nFlags, CPoint point)
{
	// TODO: Add your message handler code here and/or call default
	
	CRect screenRect;
	screen.GetWindowRect(&screenRect);
	ScreenToClient(&screenRect);

	if(!PtInRect(&screenRect, point))
	{
		PostMessage( WM_NCLBUTTONDOWN, HTCAPTION, MAKELPARAM( point.x, point.y));
	}
	CImageDlg::OnMouseMove(nFlags, point);
}


HBRUSH CRemotroidServerDlg::OnCtlColor(CDC* pDC, CWnd* pWnd, UINT nCtlColor)
{
	HBRUSH hbr = CImageDlg::OnCtlColor(pDC, pWnd, nCtlColor);

	// TODO:  Change any attributes of the DC here
	if(pWnd->GetDlgCtrlID() == 1234)
	{
		pDC->SetBkMode(TRANSPARENT);
		return (HBRUSH)GetStockObject(BLACK_BRUSH);
	}
	// TODO:  Return a different brush if the default is not desired
	return hbr;
}
