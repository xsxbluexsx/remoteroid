#pragma once


// CResizingDlg dialog

#define SIDE					30
#define GAROSERORATIO			0.55
#define MAXHEIGHT				781
#define MINHEIGHT				500


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
	
	
private:
	int m_CurCursorState;
	RECT baseRect;
	int xDiff;
	int yDiff;
public:
	void ResizingDlg(CPoint point);
	void InitResizingDlg(RECT rect, CPoint point, int CursorState);


};
