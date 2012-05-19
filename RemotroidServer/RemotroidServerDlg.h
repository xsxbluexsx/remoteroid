
// RemotroidServerDlg.h : header file
//

#pragma once

#include "MyClient.h"
#include "FileSender.h"
#include "screen.h"
#include "ImageDlg.h"
#include "afxwin.h"
#include "recvfile.h"
#include "MyBitmapBtn.h"
#include "TextProgressCtrl.h"


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
	afx_msg void OnMouseMove(UINT nFlags, CPoint point);
	afx_msg HBRUSH OnCtlColor(CDC* pDC, CWnd* pWnd, UINT nCtlColor);	

	afx_msg void OnKeyDown(UINT nChar, UINT nRepCnt, UINT nFlags);	
	afx_msg void OnChar(UINT nChar, UINT nRepCnt, UINT nFlags);
	afx_msg void OnLButtonUp(UINT nFlags, CPoint point);
	
	LRESULT OnRecvJpgInfo(WPARAM wParam, LPARAM lParam);
	LRESULT OnRecvJpgData(WPARAM wParam, LPARAM lParam);
	LRESULT OnEndAccept(WPARAM wParam, LPARAM lParam);
	LRESULT OnReadyRecvFile(WPARAM wParam, LPARAM lParam);
	LRESULT OnEndRecv(WPARAM wParam, LPARAM lParam);

	static UINT UDPRecvFunc(LPVOID pParam);

	virtual BOOL PreTranslateMessage(MSG* pMsg);		

private:
	CWinThread *pRecvThread;
	CWinThread *pAcceptThread;
	CWinThread *pUdpRecvThread;
	BOOL m_isClickedEndBtn;
	SOCKET m_UDPServerSocket;
	BOOL m_isReadyRecv;	
	CRecvFile recvFileClass;
	CTextProgressCtrl m_progressCtrl;

private:
	void EndAccept(void);
	void EndConnect(void);
	void ReadyRecvFile(void);		
	void GetStoreFilePath(void);
public:
	CMyBitmapBtn m_BackButton;
	CMyBitmapBtn m_HomeButton;
	CMyBitmapBtn m_MenuButton;
	
	afx_msg void OnClickedBtnBack();
	afx_msg void OnClickedBtnHome();
	afx_msg void OnClickedBtnMenu();	
	afx_msg void OnBnClickedButton1();
};
