
// RemotroidServerDlg.h : header file
//

#pragma once

#include "MyClient.h"
#include "FileSender.h"
#include "screen.h"
#include "ImageDlg.h"

// CRemotroidServerDlg dialog
class CRemotroidServerDlg : public CImageDlg
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
	CFileSender fileSender;
	CScreen screen;

public:
	static UINT AcceptFunc(LPVOID pParam);	
	static UINT RecvFunc(LPVOID pParam);	
	void SetClientSocket(CMyClient * pClient);
	CMyClient * GetClientSocket(void);
	
	
	afx_msg void OnDestroy();
	afx_msg void OnBnClickedOk();
	afx_msg void OnBnClickedCancel();	
	afx_msg void OnDropFiles(HDROP hDropInfo);

	
	LRESULT OnRecvJpgInfo(WPARAM wParam, LPARAM lParam);
	LRESULT OnRecvJpgData(WPARAM wParam, LPARAM lParam);
private:
	CWinThread *pRecvThread;
	void EndConnect(void);
	BOOL m_isClickedEndBtn;
public:
	LRESULT OnEndRecv(WPARAM wParam, LPARAM lParam);
	afx_msg void OnBnClickedFilesender();
private:
	CWinThread *pAcceptThread;
public:
	LRESULT OnEndAccept(WPARAM wParam, LPARAM lParam);
private:
	void EndAccept(void);
	SOCKET m_UDPServerSocket;
	CWinThread *pUdpRecvThread;
public:
	static UINT UDPRecvFunc(LPVOID pParam);
	afx_msg void OnMouseMove(UINT nFlags, CPoint point);
	afx_msg HBRUSH OnCtlColor(CDC* pDC, CWnd* pWnd, UINT nCtlColor);
};
