
#pragma once


//어떤 종류의 이벤트인지 설정
#define SETCOORDINATES	0
#define TOUCHDOWN		1
#define TOUCHUP			2
#define KEYDOWN			3
#define KEYUP			4


#define EVENTCODE_SIZE	sizeof(char)
#define XPOSITION_SIZE	sizeof(int)
#define YPOSITION_SIZE	sizeof(int)
#define KEYCODE_SIZE	sizeof(int)
#define MAXEVENT_SIZE	EVENTCODE_SIZE+XPOSITION_SIZE+YPOSITION_SIZE


class CVitualEventPacket
{
public:
	CVitualEventPacket(char eventCode);
	CVitualEventPacket(char eventCode, int xPos, int yPos);
	CVitualEventPacket(char eventCode, int keyCode);
	~CVitualEventPacket(void);

private:
	char m_EventCode;
	int m_xPos;
	int m_yPos;
	int m_keyCode;
// 	char bEventCode[EVENTCODE_SIZE+1];
// 	char bXPos[XPOSITION_SIZE+1];
// 	char bYPos[YPOSITION_SIZE+1];
// 	char bKeyCode[KEYCODE_SIZE+1];
	char buffer[MAXEVENT_SIZE];

public:
	char* asByteArray();	
	int payloadSize;

private:
	char* SetCoordinates();
	char* KeyDownUp();	
	char* TouchUp();
	char* (CVitualEventPacket::*Event)();	
};

