#pragma once
class CUtil
{
public:
	CUtil(void);
	~CUtil(void);
	static int GetOpCode(char * packet);
	static int GetPacketSize(char * packet);
	static void UniToUtf(TCHAR * uni, char * utf);
	static void UtfToUni(TCHAR * uni, char * utf);
	static void AniMinimizeToTray(HWND hwnd);
	static void GetTrayWndRect(RECT * pRect);
	static void AniMaximiseFromTray(HWND hwnd);
	static void SetHanEngMode(HWND hWnd);
};

