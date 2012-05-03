#include "StdAfx.h"
#include "FileSender.h"


CFileSender::CFileSender(CMyClient *pClient)
	: m_pClient(pClient)
{
}


CFileSender::~CFileSender(void)
{
}


int CFileSender::SendPacket(int iOPCode, const char * data, int iDataLen)
{
	return m_pClient->SendPacket(iOPCode,data,iDataLen);
}


void CFileSender::SendFileInfo(HANDLE hSendFile)
{
	m_hSendFile = hSendFile;
	DWORD dwFileSizeHigh;
	//DWORD dwFileSizeLow = GetFileSize(hFile, &dwFileSizeHigh);
	
}