#pragma once

#include "MyClient.h"
#include "afxcoll.h"

#define MAXDATASIZE		MAXSIZE-HEADERSIZE

class CFileSender
{
public:
	CFileSender();
	~CFileSender(void);

public:	
	void AddSendFile(CFile * pFile);
	void SetClient(CMyClient *pClient);	
	BOOL StartSendFile(void);		
	static UINT SendFileThread(LPVOID pParam);
	CPtrList sendFileList;	
	BOOL isSending;	
	

private:
	CMyClient *m_pClient;
	HANDLE m_hSendFile;
	char buffer[MAXDATASIZE];
	unsigned long long sendedFileSize;
	unsigned long long totalFileSize;
	

private:
	int SendPacket(int iOPCode, const char * data, int iDataLen);	
	int SendFileInfo(CFile *file);
	int SendFileData(CFile * pFile);		

};

