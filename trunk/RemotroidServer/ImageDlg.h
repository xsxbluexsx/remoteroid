#pragma once


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
	HRSRC		hResource;
	HGLOBAL		hGlobal;
	HGLOBAL		hBuffer;
	LPVOID		pData;
	IStream		*pStream;
	Image		*pImagePng;
	
public:
	afx_msg void OnPaint();
	virtual BOOL OnInitDialog();
	
};
