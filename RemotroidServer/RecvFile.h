#pragma once
#include "TextProgressCtrl.h"

class CRecvFile
{
public:
	CRecvFile(void);
	~CRecvFile(void);
private:
	HANDLE m_hRecvFile;
	TCHAR m_uniFileName[100];
	unsigned long long m_iTotalFileSize;	
	unsigned long long m_iRecvFileSize;
	TCHAR directoryPath[MAX_PATH];
	CTextProgressCtrl *pProgressBar;

public:
	HANDLE RecvFileInfo(char * data);
	void RecvFileData(char * data, int packetSize);
	LONGLONG atoll(char * str);
	void CloseFileHandle(void);	
	void SetFilePath(TCHAR * path);
	void SetDefaultPath(void);
	void SetProgressBar(CTextProgressCtrl * pProgressBar);
};

