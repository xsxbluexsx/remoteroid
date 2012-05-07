#pragma once
#include "DrawJpg.h"

// CScreen

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
public:
	void InitDrawJpg(void);
	afx_msg void OnDestroy();
	void SetJpgInfo(char * data);
	void RecvJpgData(char *data, int packetSize);	
};


