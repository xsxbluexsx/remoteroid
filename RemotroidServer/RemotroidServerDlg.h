
// RemotroidServerDlg.h : header file
//

#pragma once

#include "MyClient.h"


// CRemotroidServerDlg dialog
class CRemotroidServerDlg : public CDialogEx
{
// Construction
public:
	CRemotroidServerDlg(CWnd* pParent = NULL);	// standard constructor

// Dialog Data
	enum { IDD = IDD_REMOTROIDSERVER_DIALOG };

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV support


// Implementation
protected:
	HICON m_hIcon;

	// Generated message map functions
	virtual BOOL OnInitDialog();
	afx_msg void OnSysCommand(UINT nID, LPARAM lParam);
	afx_msg void OnPaint();
	afx_msg HCURSOR OnQueryDragIcon();
	DECLARE_MESSAGE_MAP()
private:
	SOCKET m_ServerSocket;	
	CMyClient *m_pClient;

public:
	static UINT AcceptFunc(LPVOID pParam);	
	static UINT RecvFunc(LPVOID pParam);	
	void SetClientSocket(CMyClient * pClient);
	CMyClient * GetClientSocket(void);
	HANDLE RecvFileInfo(char *data, unsigned int *fileSize);
	
	afx_msg void OnDestroy();
	afx_msg void OnBnClickedOk();
	afx_msg void OnBnClickedCancel();
};
