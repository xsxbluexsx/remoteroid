#pragma once

#include "MyClient.h"

class CFileSender
{
public:
	CFileSender(CMyClient *pClient);
	~CFileSender(void);

	void SendFileInfo(HANDLE hSendFile);
private:
	CMyClient *m_pClient;
	HANDLE m_hSendFile;

	int SendPacket(int iOPCode, const char * data, int iDataLen);
};

