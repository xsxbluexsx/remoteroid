#include "StdAfx.h"
#include "Util.h"


CUtil::CUtil(void)
{
}


CUtil::~CUtil(void)
{
}


int CUtil::GetOpCode(char * packet)
{
	char bOPCode[OPCODESIZE+1];
	memset(bOPCode, 0, sizeof(bOPCode));
	memcpy(bOPCode, packet, OPCODESIZE);
	return atoi(bOPCode);
}


int CUtil::GetPacketSize(char * packet)
{
	char bPacketSize[TOTALSIZE+1];
	memset(bPacketSize, 0, sizeof(bPacketSize));
	memcpy(bPacketSize, packet+OPCODESIZE, TOTALSIZE);
	return atoi(bPacketSize);
}


void CUtil::UniToUtf(TCHAR * uni, char * utf)
{
	int nLen = WideCharToMultiByte(CP_UTF8, 0, uni, _tcslen(uni)+1, NULL, NULL, NULL, NULL);
	WideCharToMultiByte(CP_UTF8, 0, uni, _tcslen(uni)+1, utf, nLen, NULL, NULL);
}


void CUtil::UtfToUni(TCHAR * uni, char * utf)
{
	int nLen = MultiByteToWideChar(CP_UTF8, 0, utf, strlen(utf)+1, NULL, NULL);
	MultiByteToWideChar(CP_UTF8, 0, utf, strlen(utf)+1, uni, nLen);
}
