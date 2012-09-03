#include "StdAfx.h"
#include "DeviceInfo.h"


CDeviceInfo::CDeviceInfo(void)
	: m_strUUID(_T(""))
	, m_strNickname(_T(""))
	, m_strRegKey(_T(""))
{
}


CDeviceInfo::~CDeviceInfo(void)
{
}


void CDeviceInfo::SetUUID(CString strUUID)
{
	m_strUUID = strUUID;
}

void CDeviceInfo::SetNickname(CString strNickname)
{
	m_strNickname = strNickname;
}
void CDeviceInfo::SetRegKey(CString strRegKey)
{
	m_strRegKey = strRegKey;
}

CString CDeviceInfo::GetUUID()
{
	return m_strUUID;
}

CString CDeviceInfo::GetNickname()
{
	return m_strNickname;
}

CString CDeviceInfo::GetRegKey()
{
	return m_strRegKey;
}


CDeviceInfo * CDeviceInfo::FromJson(Json::Value& jsonPayload)
{
	CDeviceInfo *deviceInfo = new CDeviceInfo();
	deviceInfo->SetUUID(CString(jsonPayload.get("deviceUUID", "").asCString()));
	deviceInfo->SetNickname(CString(jsonPayload.get("nickname", "").asCString()));
	deviceInfo->SetRegKey(CString(jsonPayload.get("registrationKey", "").asCString()));

	return deviceInfo;
}
