#include "StdAfx.h"
#include "FileSender.h"


CFileSender::CFileSender()
	: totalFileSize(0), m_pClient(NULL),
	sendedFileSize(0),isSending(FALSE)	
	, pSendFileThread(NULL)
{
	memset(buffer, 0, sizeof(buffer));
}


CFileSender::~CFileSender(void)
{		
	DeleteFileList();
}

void CFileSender::SetClient(CMyClient *pClient)
{
	m_pClient = pClient;
}

int CFileSender::SendPacket(int iOPCode, const char * data, int iDataLen)
{
	return m_pClient->SendPacket(iOPCode,data,iDataLen);
}



BOOL CFileSender::StartSendFile(void)
{
	if(m_pClient == NULL || isSending == TRUE)
	{		
		return FALSE;
	}

	isSending = TRUE;

	SendFileInfo();

	return TRUE;
}

int CFileSender::SendFileInfo()
{	
	if(sendFileList.IsEmpty())
	{
		isSending = FALSE;
		return -1;
	}

	CFile *pFile = (CFile *)sendFileList.GetHead();
	
	totalFileSize = pFile->GetLength();
	CString fileName = pFile->GetFileName();

	//유니코드 형식의 파일 이름을 UTF-8로 변환
	TCHAR uniFileName[FILENAMESIZE];
	char utfFileName[FILENAMESIZE];	
	memset(uniFileName, 0, sizeof(uniFileName));
	memset(utfFileName, 0, sizeof(utfFileName));
	_tcscpy(uniFileName, fileName);
	CUtil::UniToUtf(uniFileName, utfFileName);

	//프로토콜에 맞춰서 상위 100바이트는 파일 이름 이후 100바이트는 파일 크기	
	memset(buffer, 0, sizeof(buffer));
	memcpy(buffer, utfFileName, FILENAMESIZE);
	sprintf(buffer+FILENAMESIZE, "%100llu", totalFileSize);

	sendedFileSize = 0;
	return SendPacket(OP_SENDFILEINFO, buffer, FILENAMESIZE+FILESIZESIZE);
}

void CFileSender::SendFileData()
{
	if(pSendFileThread != NULL)
	{
		delete pSendFileThread;
	}
	pSendFileThread = AfxBeginThread(SendFileThread, this);
	pSendFileThread->m_bAutoDelete = FALSE;
}



BOOL CFileSender::AddSendFile(CFile * pFile)
{
	if(m_pClient == NULL || isSending == TRUE)
	{		
		return FALSE;
	}

	sendFileList.AddTail((void*)pFile);		
	return TRUE;
}



UINT CFileSender::SendFileThread(LPVOID pParam)
{	
	CFileSender *pDlg = (CFileSender *)pParam;	
	CFile *pFile = (CFile *)pDlg->sendFileList.RemoveHead();
	unsigned long long totalFileSize = pFile->GetLength();
	unsigned long long sendedFileSize = 0;
		
	while(totalFileSize > sendedFileSize)
	{
		int iCurrentSendSize = (totalFileSize-sendedFileSize) > MAXDATASIZE ? MAXDATASIZE : totalFileSize-sendedFileSize;		
		pFile->Read(pDlg->buffer, iCurrentSendSize);
		if(pDlg->SendPacket(OP_SENDFILEDATA, pDlg->buffer, iCurrentSendSize) == SOCKET_ERROR)
			break;		
		sendedFileSize += iCurrentSendSize;
	}

	delete pFile;
	pDlg->SendFileInfo();
	return 0;
}



void CFileSender::DeleteFileList(void)
{
	if(pSendFileThread != NULL)
	{
		WaitForSingleObject(pSendFileThread->m_hThread, 100);
		delete pSendFileThread;
		pSendFileThread = NULL;
	}

	POSITION pos = sendFileList.GetHeadPosition();
	while(pos)
	{
		CFile *pFile = (CFile *)sendFileList.GetNext(pos);		
		delete pFile;		
	}
	sendFileList.RemoveAll();
	isSending = FALSE;
	return;
}
