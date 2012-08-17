#pragma once


// CResizingDlg dialog

#define SIDE		30

class CResizingDlg : public CDialogEx
{
	DECLARE_DYNAMIC(CResizingDlg)

public:
	CResizingDlg(CWnd* pParent = NULL);   // standard constructor
	virtual ~CResizingDlg();

// Dialog Data
	enum { IDD = IDD_RESIZING };

protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support

	DECLARE_MESSAGE_MAP()
public:
	virtual BOOL OnInitDialog();

	
	afx_msg void OnWindowPosChanging(WINDOWPOS* lpwndpos);
	static int SearchSide(CRect rc, CPoint point);
	afx_msg void OnNcLButtonUp(UINT nHitTest, CPoint point);

	afx_msg void OnNcLButtonDown(UINT nHitTest, CPoint point);
	afx_msg void OnLButtonUp(UINT nFlags, CPoint point);
	afx_msg void OnMouseMove(UINT nFlags, CPoint point);
};
