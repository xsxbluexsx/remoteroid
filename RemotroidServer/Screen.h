#pragma once
#include "DrawJpg.h"
#include "MyClient.h"
#include "VitualEventPacket.h"


// CScreen
#define WIDTH	360
#define HEIGHT	600

#define LEFT	42
#define TOP		104
#define RIGHT	LEFT+WIDTH
#define BOTTOM	TOP+HEIGHT

class CScreen : public CStatic
{
	DECLARE_DYNAMIC(CScreen)

public:
	CScreen();
	virtual ~CScreen();

protected:
	DECLARE_MESSAGE_MAP()

private:
	CDrawJpg drawJpg;	
	CMyClient *pClient;
public:
	void InitDrawJpg(void);
	afx_msg void OnDestroy();
	LRESULT OnSetJpgInfo(WPARAM wParam, LPARAM lParam);
	LRESULT OnRecvJpgData(WPARAM wParam, LPARAM lParam);	
	
	afx_msg void OnLButtonDown(UINT nFlags, CPoint point);
	afx_msg void OnLButtonUp(UINT nFlags, CPoint point);

	
	void SetClient(CMyClient * pClient);
	afx_msg void OnMouseMove(UINT nFlags, CPoint point);
};


