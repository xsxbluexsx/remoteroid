#pragma once


class CMyClient
{
public:
	CMyClient(SOCKET clientSocket);
	~CMyClient(void);
private:
	int m_iCurrent;
	char m_RecvBuffer[MAXSIZE*2];	
	SOCKET m_ClientSocket;
public:
	int RecvPacket(void);
	bool GetPacket(char * packet);
	int SendPacket(int iOPCode, const char * data, int iDataLen);
};

