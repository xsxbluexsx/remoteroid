#pragma once
#include "afxwin.h"


// CImageDlg dialog

class CImageDlg : public CDialogEx
{
	DECLARE_DYNAMIC(CImageDlg)

public:
	CImageDlg(UINT nIDTemplate, CWnd *pParent = NULL);   // standard constructor
	virtual ~CImageDlg();

// Dialog Data
	enum { IDD = IDD_IMAGEDLG };

protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support

	DECLARE_MESSAGE_MAP()
private:
	HRSRC		hResource,	hResBack;
	HGLOBAL		hGlobal,	hBackGlobal;
	HGLOBAL		hBuffer;
	LPVOID		pData;
	IStream		*pStream;
	Image		*pImagePng;
	float		m_xScale,	m_yScale;
	
public:
	afx_msg void OnPaint();
	virtual BOOL OnInitDialog();
	
	void OnResizeSkin(CRect * rc);
	CBitmap m_bitmap;
};
