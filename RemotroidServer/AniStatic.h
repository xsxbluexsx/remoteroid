#pragma once
#include "atlimage.h"
#include "atltypes.h"


// CAniStatic

class CAniStatic : public CStatic
{
	DECLARE_DYNAMIC(CAniStatic)

public:
	CAniStatic();
	virtual ~CAniStatic();

protected:
	DECLARE_MESSAGE_MAP()
public:
	
private:	
	CBitmap m_bmp;
public:
	
	afx_msg void OnPaint();
	afx_msg int OnCreate(LPCREATESTRUCT lpCreateStruct);
	CImage m_Img;
	int alpahValue;
	int pos;
	void SetAnimation(BOOL cond);
	afx_msg void OnTimer(UINT_PTR nIDEvent);
	CRect myRect;
};


