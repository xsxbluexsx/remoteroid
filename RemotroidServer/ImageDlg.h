#pragma once
#include "afxwin.h"


// CImageDlg dialog
#include "ResizingDlg.h"
#include "atltypes.h"

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
private:
	CResizingDlg *pResizeDlg;
protected:
	RECT baseRect;
public:
	void SetResizingDlg(void);
	
protected:
	BITMAP m_bmp;
	void SetDlgPosition(void);

public:
	afx_msg LRESULT OnNcHitTest(CPoint point);
	afx_msg BOOL OnSetCursor(CWnd* pWnd, UINT nHitTest, UINT message);
//	afx_msg void OnNcLButtonDown(UINT nHitTest, CPoint point);
	afx_msg void OnNcLButtonUp(UINT nHitTest, CPoint point);
private:
	BOOL m_bResizing;
public:
	afx_msg void OnNcMouseMove(UINT nHitTest, CPoint point);
	afx_msg void OnLButtonUp(UINT nFlags, CPoint point);
	afx_msg void OnMouseMove(UINT nFlags, CPoint point);
private:
	int m_nHitTest;



public:
	afx_msg void OnLButtonDown(UINT nFlags, CPoint point);
private:
	int SetSizeCursor(CPoint point);
public:
	CRect m_oldRect;
private:
	int m_CurCursorState;
};
