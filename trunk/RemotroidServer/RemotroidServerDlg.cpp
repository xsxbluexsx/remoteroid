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




CRemotroidServerDlg::CRemotroidServerDlg(CWnd* pParent /*=NULL*/)
	: CImageDlg(CRemotroidServerDlg::IDD, pParent)	
	, m_pClient(NULL)
	, pRecvThread(NULL)
	, m_isClickedEndBtn(FALSE)
	, pAcceptThread(NULL)
	, pUdpRecvThread(NULL)
	, m_isReadyRecv(FALSE)	
	, isTray(FALSE)	
{
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
	
}

CRemotroidServerDlg::~CRemotroidServerDlg()
{
	m_TrayIcon.RemoveIcon();
}

void CRemotroidServerDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialogEx::DoDataExchange(pDX);
	DDX_Control(pDX, IDC_BTN_BACK, m_BackButton);
	DDX_Control(pDX, IDC_BTN_HOME, m_HomeButton);
	DDX_Control(pDX, IDC_BTN_MENU, m_MenuButton);	
	DDX_Control(pDX, IDC_PROGRESS1, m_progressCtrl);
	DDX_Control(pDX, IDC_BTN_TRAY, m_TrayButton);
	DDX_Control(pDX, IDC_BTN_CLOSE, m_CloseButton);
}

BEGIN_MESSAGE_MAP(CRemotroidServerDlg, CDialogEx)
	ON_WM_SYSCOMMAND()
	ON_WM_PAINT()
	ON_WM_QUERYDRAGICON()
	ON_WM_DESTROY()
	ON_BN_CLICKED(IDOK, &CRemotroidServerDlg::OnBnClickedOk)
	ON_BN_CLICKED(IDCANCEL, &CRemotroidServerDlg::OnBnClickedCancel)	
	ON_WM_DROPFILES()	
	ON_MESSAGE(WM_MYENDRECV, OnEndRecv)
	ON_MESSAGE(WM_MYENDACCEPT, OnEndAccept)
	ON_MESSAGE(WM_READYRECVFILE, OnReadyRecvFile)	
	ON_MESSAGE(WM_CREATEPOPUPDLG, OnCreatePopupDlg)
	ON_MESSAGE(WM_CLOSEPOPDLG, OnClosePopDlg)
	ON_MESSAGE(WM_ICON_NOTIFY, OnTrayNotification)
	ON_MESSAGE(WM_MYDBLCLKTRAY, OnMyDblClkTray)
	
	
	ON_WM_MOUSEMOVE()
	ON_WM_CTLCOLOR()
	ON_WM_KEYDOWN()	
	ON_WM_CHAR()
	ON_WM_LBUTTONUP()
	
	ON_BN_CLICKED(IDC_BTN_BACK, &CRemotroidServerDlg::OnClickedBtnBack)
	ON_BN_CLICKED(IDC_BTN_HOME, &CRemotroidServerDlg::OnClickedBtnHome)
	ON_BN_CLICKED(IDC_BTN_MENU, &CRemotroidServerDlg::OnClickedBtnMenu)		
	ON_WM_KEYUP()
	ON_BN_CLICKED(IDC_BTN_TRAY, &CRemotroidServerDlg::OnBnClickedBtnTray)
	ON_BN_CLICKED(IDC_BTN_CLOSE, &CRemotroidServerDlg::OnBnClickedBtnClose)
	ON_WM_CLOSE()
	
	ON_WM_TIMER()
	
	ON_WM_MOUSEWHEEL()
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
	//스크린 윈도우 위치 및 스타일 설정

	//ShowWindow(SW_HIDE);
	
	screen.CreateEx(WS_EX_TOPMOST
		, _T("STATIC"), NULL, WS_CHILD|WS_VISIBLE|SS_NOTIFY, CRect(LEFT, TOP, RIGHT, BOTTOM), this, 1234);
	//screen.SetFocus();	
	screen.SetLayeredWindowAttributes(0, 255, LWA_ALPHA);

	m_progressCtrl.MoveWindow(LEFT, TOP-10, WIDTH, 10);
	m_progressCtrl.ShowWindow(SW_HIDE);
	m_progressCtrl.SetBarBkColor(RGB(56,58,60));
	m_progressCtrl.SetBarColor(RGB(7,215,7));
	m_progressCtrl.SetTextColor(RGB(255,255,255));	
	m_progressCtrl.SetRange(0, 100);

	recvFileClass.SetProgressBar(&m_progressCtrl);
	fileSender.SetProgressBar(&m_progressCtrl);

	//하단 버튼 위치 설정
	m_MenuButton.MoveWindow(60, 710, BUTTONWIDTH, BUTTONHEIGHT);
	m_HomeButton.MoveWindow(60+BUTTONWIDTH, 710, BUTTONWIDTH, BUTTONHEIGHT);
	m_BackButton.MoveWindow(60+BUTTONWIDTH*2, 710, BUTTONWIDTH, BUTTONHEIGHT);

	m_TrayButton.MoveWindow(350, 30, 19, 16);
	m_CloseButton.MoveWindow(350+19, 30, 19, 16);
	
	m_CloseButton.LoadBitmaps(IDB_BITMAP_CLOSE_MAIN);
	m_CloseButton.SetHoverBitmapID(IDB_BITMAP_HOVER_MAIN);
	m_TrayButton.LoadBitmaps(IDB_BITMAP_TRAYBTN);
	m_TrayButton.SetHoverBitmapID(IDB_BITMAP_TRAY_HOVER);


	m_HomeButton.LoadBitmaps(IDB_BITMAP_HOME, IDB_BITMAP_HOME_CLICK);
	m_HomeButton.SetHoverBitmapID(IDB_BITMAP_HOME_OVER);
	m_BackButton.LoadBitmaps(IDB_BITMAP_BACK,IDB_BITMAP_BACK_CLICK);
	m_BackButton.SetHoverBitmapID(IDB_BITMAP_BACK_OVER);
	m_MenuButton.LoadBitmaps(IDB_BITMAP_MENU, IDB_BITMAP_MENU_CLICK);
	m_MenuButton.SetHoverBitmapID(IDB_BITMAP_MENU_OVER);


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
	

	int optVal;
	int optlen = sizeof(optVal);
	getsockopt(m_UDPServerSocket, SOL_SOCKET, SO_RCVBUF, (char*)&optVal, &optlen);

	optVal = optVal * 2;
	setsockopt(m_UDPServerSocket, SOL_SOCKET, SO_RCVBUF, (char*)&optVal, sizeof(optVal));

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

	//자신의 IP 얻어오기
	PHOSTENT hostinfo;
	char name[255];
	char ip[255];
	if(gethostname(name, sizeof(name)) == 0)
	{
		if((hostinfo = gethostbyname(name)) != NULL)
		{
			strcpy(ip, inet_ntoa(*(struct in_addr *)*hostinfo->h_addr_list));
			m_strMyIp = CA2W(ip);
			screen.m_strMyIp = m_strMyIp;
		}
	}	

	if(listen(m_ServerSocket , SOMAXCONN) == SOCKET_ERROR)
	{
		MessageBox(_T("listen error"));
		EndDialog(IDCANCEL);
		return TRUE;
	}	
	
	pAcceptThread = AfxBeginThread(AcceptFunc, this);	
	pAcceptThread->m_bAutoDelete = FALSE;
	

	CRect systemRc, rc;
	::SystemParametersInfo(SPI_GETWORKAREA, 0, &systemRc, 0);
	GetClientRect(&rc);	
	

	//트레이 아이콘 등록
	if(!m_TrayIcon.Create(this, WM_ICON_NOTIFY, _T("Remoteroid"), NULL, NULL))
		return -1;
	m_TrayIcon.SetIcon(IDR_MAINFRAME);
	
	SetWindowText(_T("Remoteroid"));

	//비프음 끄기
	TOGGLEKEYS tk;
	SystemParametersInfo(SPI_SETBEEP, false, &tk, 0);	

	SetTimer(0, 0, NULL);
 
	return FALSE;  // return TRUE  unless you set the focus to a control
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
	pClient->SetNoDelay(TRUE);

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
			pDlg->screen.SetJpgInfo(data);
			//pDlg->screen.SendMessage(WM_RECVJPGINFO, 0, (LPARAM)data);
			break;
		case OP_SENDJPGDATA:
			//pDlg->SendMessage(WM_RECVJPGDATA, (WPARAM)iPacketSize, (LPARAM)data);
			pDlg->screen.RecvJpgData(data, iPacketSize);
			//pDlg->screen.SendMessage(WM_RECVJPGDATA, iPacketSize, (LPARAM)data);
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

	CRecvFile& recvFileClass = pDlg->recvFileClass;	
	CTextProgressCtrl& prgressBar = pDlg->m_progressCtrl;
	
	while (TRUE)
	{		
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
			
			//packet 크기가 0이면 깨진 패킷
			if(iPacketSize == 0)
			{
				pClient->ResetBuffer();
				break;
			}

			char *data = bPacket+HEADERSIZE;

			switch(iOPCode)
			{
			case OP_SENDFILEINFO:
				if(recvFileClass.RecvFileInfo(data) != INVALID_HANDLE_VALUE)
				{
					//파일은 수신 받을 준비가 되면 req 요청을 전송한다
					pClient->SendPacket(OP_REQFILEDATA, NULL, 0);
				}
				break;
			case OP_SENDFILEDATA:
				recvFileClass.RecvFileData(data, iPacketSize);				
				break;		
			case OP_SENDJPGINFO:
				//pDlg->screen.SendMessage(WM_RECVJPGINFO, 0, (LPARAM)data);
				pDlg->screen.SetJpgInfo(data);
				break;
			case OP_SENDJPGDATA:
				//pDlg->screen.SendMessage(WM_RECVJPGDATA, iPacketSize, (LPARAM)data);
				pDlg->screen.RecvJpgData(data, iPacketSize);
				break;
			case OP_REQFILEDATA:
				pDlg->fileSender.SendFileData();
				break;
			case OP_READYSEND:
				pDlg->SendMessage(WM_READYRECVFILE, 0, 0);
				break;
			case OP_SENDDEVICEINFO:				
				pDlg->screen.SendMessage(WM_RECVDEVICEINFO, 0, (LPARAM)data);
				//디바이스 정보를 수신하면 화면 전송을 요청한다
				pClient->SendPacket(OP_REQSENDSCREEN, NULL, 0);
				break;
			case OP_SENDNOTIFICATION:
				if(iPacketSize == HEADERSIZE)
					break;
				pDlg->SendMessage(WM_CREATEPOPUPDLG, iPacketSize, (LPARAM)data);
				break;
			}
		}
	}

	recvFileClass.CloseFileHandle();		
	pDlg->fileSender.DeleteFileList();
	pDlg->screen.SetDisconnect();

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
	screen.SetClient(pClient);
}

CMyClient * CRemotroidServerDlg::GetClientSocket(void)
{
	return m_pClient;
}





void CRemotroidServerDlg::OnDestroy()
{		
	// TODO: Add your message handler code here		
	m_isClickedEndBtn = TRUE;
	EndAccept();
	EndConnect();	

	CDialogEx::OnDestroy();	
}


void CRemotroidServerDlg::OnBnClickedOk()
{
	// TODO: Add your control notification handler code here	
//	CDialogEx::OnOK();
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
	memset(path, 0, sizeof(path));
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
			continue;
		}
		END_CATCH
		
		if(FALSE == fileSender.AddSendFile(pFile))
		{
			delete pFile;
			return;
		}			
	}
	fileSender.StartSendFile();
	CDialogEx::OnDropFiles(hDropInfo);
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

	while (TRUE)
	{
		DWORD dwResult = WaitForSingleObject(pAcceptThread->m_hThread, 100);
		if(dwResult !=WAIT_TIMEOUT)
			break;
		MSG msg;
		while (::PeekMessage(&msg, NULL, NULL, NULL,PM_REMOVE))
		{
			::TranslateMessage(&msg);
			::DispatchMessage(&msg);
		}
	}
	
	delete pAcceptThread;
	pAcceptThread = NULL;
}

void CRemotroidServerDlg::EndConnect(void)
{
	if(pRecvThread == NULL)
		return;
	
	m_pClient->CloseSocket();

	while(TRUE)
	{
		DWORD dwResult = WaitForSingleObject(pRecvThread->m_hThread, 100);
		if(dwResult !=WAIT_TIMEOUT)
			break;
		MSG msg;
		while (::PeekMessage(&msg, NULL, NULL, NULL,PM_REMOVE))
		{
			::TranslateMessage(&msg);
			::DispatchMessage(&msg);
		}
	}
	
	delete pRecvThread;
	pRecvThread = NULL;
}


//클라이언트 접속 종료로 인한 recv 쓰레드 종료시 호출
LRESULT CRemotroidServerDlg::OnEndRecv(WPARAM wParam, LPARAM lParam)
{
	WaitForSingleObject(pRecvThread->m_hThread, 500);	
	delete pRecvThread;
	pRecvThread = NULL;

	pAcceptThread = AfxBeginThread(AcceptFunc, this);
	pAcceptThread->m_bAutoDelete = FALSE;
	return LRESULT();
}

LRESULT CRemotroidServerDlg::OnEndAccept(WPARAM wParam, LPARAM lParam)
{
	WaitForSingleObject(pAcceptThread->m_hThread, 500);
	delete pAcceptThread;
	pAcceptThread = NULL;
	return LRESULT();
}

////쓰레드 정상 종료를 위한 함수들
//////////////////////////////////////////////////////////////////





//다이얼로그 이동을 위한..
void CRemotroidServerDlg::OnMouseMove(UINT nFlags, CPoint point)
{
	// TODO: Add your message handler code here and/or call default
	
	CRect screenRect;
	screen.GetWindowRect(&screenRect);
	ScreenToClient(&screenRect);
	CRect moveRect;
	GetClientRect(&moveRect);
	moveRect.bottom = screenRect.top - 20;

	if(PtInRect(&moveRect, point) && m_isReadyRecv == FALSE)
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



void CRemotroidServerDlg::OnKeyUp(UINT nChar, UINT nRepCnt, UINT nFlags)
{
	// TODO: Add your message handler code here and/or call default
	
	int keyCode;
	if( (m_pClient != NULL) && ((keyCode=keyCodeGen.GetKeyCode(nChar)) != INVALID_KEYCODE) )
	{
		CVitualEventPacket event(KEYUP, keyCode);
		m_pClient->SendPacket(OP_VIRTUALEVENT, event.asByteArray(), event.payloadSize);
	}	
}


void CRemotroidServerDlg::OnKeyDown(UINT nChar, UINT nRepCnt, UINT nFlags)
{
	// TODO: Add your message handler code here and/or call default	

	//한글모드로 전환이 안되게 해야한다
	if(nChar == VK_HANGUL)
		CUtil::SetHanEngMode(GetSafeHwnd());	

	int keyCode;
	if( (m_pClient != NULL) && ((keyCode=keyCodeGen.GetKeyCode(nChar)) != INVALID_KEYCODE) )
	{
		CVitualEventPacket event(KEYDOWN, keyCode);
		m_pClient->SendPacket(OP_VIRTUALEVENT, event.asByteArray(), event.payloadSize);	
	}	
}


BOOL CRemotroidServerDlg::PreTranslateMessage(MSG* pMsg)
{
	// TODO: Add your specialized code here and/or call the base class
	//esc 종료 방지
	if(pMsg->wParam == VK_ESCAPE)
		return TRUE;	

	if((pMsg->message == WM_KEYDOWN) || (pMsg->message == WM_KEYUP))
	{
		//한영키 눌렀을 경우 처리
		if(pMsg->wParam == VK_PROCESSKEY)
			pMsg->wParam = ImmGetVirtualKey(GetSafeHwnd());

		SendMessage(pMsg->message, pMsg->wParam, pMsg->lParam);
	}
	return CImageDlg::PreTranslateMessage(pMsg);
}


void CRemotroidServerDlg::OnChar(UINT nChar, UINT nRepCnt, UINT nFlags)
{
	// TODO: Add your message handler code here and/or call default
	CString str = _T("");
	str.Format(_T("%c"), nChar);
	MessageBox(str);
 	CImageDlg::OnChar(nChar, nRepCnt, nFlags);
}


////////////////////////////////////////////////////////////
////드래그 앤 드롭으로 파일을 수신 받기 위해//////////////////
void CRemotroidServerDlg::GetStoreFilePath(void)
{
	BOOL bResult = FALSE;
	POINT pt;
	memset(&pt, 0, sizeof(pt));
	GetCursorPos(&pt);

	CWnd *wnd = WindowFromPoint(pt);
	WCHAR temp[MAX_PATH];
	CWnd *pParent = NULL;		

	for(pParent = wnd; pParent->GetParent(); pParent = pParent->GetParent());				

	CWnd *pToolbarWnd = pParent->FindWindowEx(pParent->GetSafeHwnd(), NULL, _T("WorkerW"), NULL);  
	if(!pToolbarWnd) goto ENDSEARCH;
	pToolbarWnd = pToolbarWnd->FindWindowEx(pToolbarWnd->GetSafeHwnd(), NULL, _T("ReBarWindow32"), NULL);  
	if(!pToolbarWnd) goto ENDSEARCH;
	pToolbarWnd = pToolbarWnd->FindWindowEx(pToolbarWnd->GetSafeHwnd(), NULL, _T("Address Band Root"), NULL);  
	if(!pToolbarWnd) goto ENDSEARCH;
	pToolbarWnd = pToolbarWnd->FindWindowEx(pToolbarWnd->GetSafeHwnd(), NULL, _T("msctls_progress32"), NULL);  
	if(!pToolbarWnd) goto ENDSEARCH;
	pToolbarWnd = pToolbarWnd->FindWindowEx(pToolbarWnd->GetSafeHwnd(), NULL, _T("Breadcrumb Parent"), NULL);  
	if(!pToolbarWnd) goto ENDSEARCH;
	pToolbarWnd = pToolbarWnd->FindWindowEx(pToolbarWnd->GetSafeHwnd(), NULL, _T("ToolbarWindow32"), NULL);  
	if(!pToolbarWnd) goto ENDSEARCH;

	//탐색기가 가르키는 곳의 경로 획득	
	pToolbarWnd->GetWindowText(temp, 50);
	if(temp[4] >= _T('A') && temp[4] <= _T('Z'))
		bResult = TRUE;

ENDSEARCH:
	//경로를 찾지 못하면 디폴트로..
	bResult ==  TRUE ? recvFileClass.SetFilePath(temp+4) : recvFileClass.SetDefaultPath();
	return;
}


//안드로이드에서 부터 파일 수신 받을 준비
LRESULT CRemotroidServerDlg::OnReadyRecvFile(WPARAM wParam, LPARAM lParam)
{
	m_isReadyRecv = TRUE;
	
	::SetSystemCursor(LoadCursor(0, IDC_HAND), OCR_NORMAL);
	SetCapture();	
	return LRESULT();
}


void CRemotroidServerDlg::OnLButtonUp(UINT nFlags, CPoint point)
{
	// TODO: Add your message handler code here and/or call default

	//수신 받을 파일을 드래그 한 후 드롭일 경우에 저장할
	if(m_isReadyRecv == TRUE)
	{
		GetStoreFilePath();
		SystemParametersInfo(SPI_SETCURSORS, 0, NULL, 0);
		ReleaseCapture();
		m_isReadyRecv = FALSE;		

		CVitualEventPacket event(TOUCHUP);
		m_pClient->SendPacket(OP_VIRTUALEVENT, event.asByteArray(), event.payloadSize);

		m_pClient->SendPacket(OP_REQFILEINFO, 0, 0);
	}	
	CImageDlg::OnLButtonUp(nFlags, point);
}
/////드래그 앤 드롭으로 파일을 수신 받기 위해//////////////////
////////////////////////////////////////////////////////////




void CRemotroidServerDlg::OnClickedBtnBack()
{
	// TODO: Add your control notification handler code here
	SetFocus();

	if(m_pClient == NULL)
		return;
	CVitualEventPacket event(BACKBUTTON);
	m_pClient->SendPacket(OP_VIRTUALEVENT, event.asByteArray(), event.payloadSize);
}


void CRemotroidServerDlg::OnClickedBtnHome()
{
	// TODO: Add your control notification handler code here
	SetFocus();

	if(m_pClient == NULL)
		return;

	CVitualEventPacket event(HOMEBUTTON);
	m_pClient->SendPacket(OP_VIRTUALEVENT, event.asByteArray(), event.payloadSize);
}


void CRemotroidServerDlg::OnClickedBtnMenu()
{
	// TODO: Add your control notification handler code here
	SetFocus();

	if(m_pClient == NULL)
		return;

	CVitualEventPacket event(MENUBUTTON);
	m_pClient->SendPacket(OP_VIRTUALEVENT, event.asByteArray(), event.payloadSize);
}


LRESULT CRemotroidServerDlg::OnCreatePopupDlg(WPARAM wParam, LPARAM lParam)
{
	//tray에 있을 경우만 팝업 생성
	if(!isTray)
		return 0;

	char *data = (char*)lParam;
	int payloadSize = wParam-HEADERSIZE;
	TCHAR *notiText = new TCHAR[payloadSize*2];
	memset(notiText, 0, payloadSize*2);
	CUtil::UtfToUni(notiText, data);

	CPopupDlg* pDlg = new CPopupDlg;
	pDlg->m_strNoti.Format(_T("%s"), notiText);
	
	delete [] notiText;
	CPopupDlg::numOfDlg++;	
	m_popDlgMgr.InsertPopDlg(pDlg);

	//알림 소리 재생
	PlaySound((LPCTSTR)MAKEINTRESOURCE(IDR_WAVE1), NULL, SND_ASYNC|SND_RESOURCE);

	pDlg->Create(IDD_POPUPDLG, this);
	pDlg->ShowWindow(SW_HIDE);		
	pDlg->SetLayeredWindowAttributes(0, 200, LWA_ALPHA);
	pDlg->AnimateWindow(300, AW_SLIDE | AW_VER_NEGATIVE);		
	
	return LRESULT();
}


//팝업 윈도우가 종료될 때 나머지 윈도우의 위치를 아래로 내리기 위해
LRESULT CRemotroidServerDlg::OnClosePopDlg(WPARAM wParam, LPARAM lParam)
{
	CPopupDlg *pDlg = (CPopupDlg *)wParam;
	m_popDlgMgr.RemoveAndMove(pDlg);
	return LRESULT();
}


LRESULT CRemotroidServerDlg::OnTrayNotification(WPARAM wParam, LPARAM lParam)
{
	return m_TrayIcon.OnTrayNotification(wParam, lParam);
}

LRESULT CRemotroidServerDlg::OnMyDblClkTray(WPARAM wParam, LPARAM lParam)
{	
	isTray = FALSE;

	if(m_pClient != NULL)
		m_pClient->SendPacket(OP_REQSENDSCREEN, NULL, 0);
	
	CUtil::AniMaximiseFromTray(AfxGetMainWnd()->GetSafeHwnd());
	ShowWindow(SW_RESTORE);
	return LRESULT();
}


void CRemotroidServerDlg::OnBnClickedBtnTray()
{
	// TODO: Add your control notification handler code here
	SetFocus();
	isTray = TRUE;

	//트레이로 가면 화면전송을 중지한다
	if(m_pClient != NULL)
		m_pClient->SendPacket(OP_REQSTOPSCREEN, NULL, 0);
	CUtil::AniMinimizeToTray(GetSafeHwnd());	
	ShowWindow(SW_HIDE);
}


void CRemotroidServerDlg::OnBnClickedBtnClose()
{
	// TODO: Add your control notification handler code here
	SetFocus();

	PostMessage(WM_CLOSE, 0, 0);
}


void CRemotroidServerDlg::OnClose()
{
	// TODO: Add your message handler code here and/or call default
	
	if(pRecvThread != NULL)
	{
		int ret = MessageBox(_T("현재 접속 중입니다\n종료하시겠습니까?"), _T("종료"), MB_YESNO);
		if(ret == IDNO)		
			return;		
	}
	else
	{
		int ret = MessageBox(_T("종료하시겠습니까?"), _T("종료"), MB_YESNO);
		if(ret == IDNO)
			return;
	}

	CImageDlg::OnClose();
}



void CRemotroidServerDlg::OnTimer(UINT_PTR nIDEvent)
{
	// TODO: Add your message handler code here and/or call default
 
	
	KillTimer(0);
	CImageDlg::OnTimer(nIDEvent);
}


BOOL CRemotroidServerDlg::OnMouseWheel(UINT nFlags, short zDelta, CPoint pt)
{
	// TODO: Add your message handler code here and/or call default
	TRACE("nFlgs : %d, zDelta : %d, x : %d, y : %d\n", nFlags, zDelta, pt.x, pt.y);
	return CImageDlg::OnMouseWheel(nFlags, zDelta, pt);
}
