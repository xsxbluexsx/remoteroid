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
};

