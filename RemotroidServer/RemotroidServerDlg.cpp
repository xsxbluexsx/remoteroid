
// RemotroidServerDlg.cpp : implementation file
//

#include "stdafx.h"
#include "RemotroidServer.h"
#include "RemotroidServerDlg.h"
#include "afxdialogex.h"



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
	: CDialogEx(CRemotroidServerDlg::IDD, pParent)	
	, m_pClient(NULL)
{
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
	//  m_OPCODE = _T("");
	//  m_Data = _T("");
	//  m_OPCode = _T("");
}

void CRemotroidServerDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialogEx::DoDataExchange(pDX);
	//  DDX_Text(pDX, IDC_EDIT_DATA, m_OPCODE);
	//  DDX_Text(pDX, IDC_EDIT_DATA, m_Data);
	//  DDX_Text(pDX, IDC_EDIT_OPCODE, m_OPCode);
}

BEGIN_MESSAGE_MAP(CRemotroidServerDlg, CDialogEx)
	ON_WM_SYSCOMMAND()
	ON_WM_PAINT()
	ON_WM_QUERYDRAGICON()
	ON_WM_DESTROY()
	ON_BN_CLICKED(IDOK, &CRemotroidServerDlg::OnBnClickedOk)
	ON_BN_CLICKED(IDCANCEL, &CRemotroidServerDlg::OnBnClickedCancel)
//	ON_BN_CLICKED(IDC_BTN_SEND, &CRemotroidServerDlg::OnClickedBtnSend)
END_MESSAGE_MAP()


// CRemotroidServerDlg message handlers

BOOL CRemotroidServerDlg::OnInitDialog()
{
	CDialogEx::OnInitDialog();

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

	m_ServerSocket = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
	if(m_ServerSocket == INVALID_SOCKET)
	{
		MessageBox(_T("Server Socket Error"));
		EndDialog(IDCANCEL);
		return TRUE;
	}

	SOCKADDR_IN addr;
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

	AfxBeginThread(AcceptFunc, this);

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
		CDialogEx::OnPaint();
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
	AfxMessageBox(_T("접속 완료"));

	AfxBeginThread(RecvFunc, pDlg);

	return 0;
}

#include "RecvFile.h"

UINT CRemotroidServerDlg::RecvFunc(LPVOID pParam)
{	
	CRemotroidServerDlg *pDlg = (CRemotroidServerDlg *)pParam;
	CMyClient *pClient = pDlg->GetClientSocket();

	char bPacket[4096];
	CRecvFile recvFileClass;
	
	while (TRUE)
	{
		memset(bPacket, 0, sizeof(bPacket));
		int iRecvLen = pClient->RecvPacket();
		if(iRecvLen < 0)
		{
			AfxMessageBox(_T("접속 종료"));
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
			}
		}
	}
	delete pClient;
	return 0;
}


void CRemotroidServerDlg::SetClientSocket(CMyClient * pClient)
{
	m_pClient = pClient;
}
CMyClient * CRemotroidServerDlg::GetClientSocket(void)
{
	return m_pClient;
}


HANDLE CRemotroidServerDlg::RecvFileInfo(char *data, unsigned int *fileSize)
{
	char bFileName[FILENAMESIZE+1];
	memset(bFileName, 0, sizeof(bFileName));
	memcpy(bFileName, data, FILENAMESIZE);
	//상위 100바이트에서 파일 이름 추출

	char bFileSize[FILESIZESIZE+1];
	memset(bFileSize, 0, sizeof(bFileSize));
	memcpy(bFileSize, data+FILENAMESIZE, FILESIZESIZE);
	//하위 100바이트에서 파일 크기 추출

	*fileSize = atoi(bFileSize);

	TCHAR uniFileName[100];
	memset(uniFileName, 0, sizeof(uniFileName));
	CUtil::UtfToUni(uniFileName, bFileName);
	return CreateFile(uniFileName, GENERIC_WRITE, NULL, NULL, CREATE_ALWAYS,
		FILE_ATTRIBUTE_NORMAL, NULL);
}



void CRemotroidServerDlg::OnDestroy()
{
	CDialogEx::OnDestroy();	
	// TODO: Add your message handler code here		
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

