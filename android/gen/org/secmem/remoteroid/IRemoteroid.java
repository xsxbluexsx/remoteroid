/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\HwangYongWan\\workspace\\Remoteroid\\src\\org\\secmem\\remoteroid\\IRemoteroid.aidl
 */
package org.secmem.remoteroid;
public interface IRemoteroid extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements org.secmem.remoteroid.IRemoteroid
{
private static final java.lang.String DESCRIPTOR = "org.secmem.remoteroid.IRemoteroid";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an org.secmem.remoteroid.IRemoteroid interface,
 * generating a proxy if needed.
 */
public static org.secmem.remoteroid.IRemoteroid asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof org.secmem.remoteroid.IRemoteroid))) {
return ((org.secmem.remoteroid.IRemoteroid)iin);
}
return new org.secmem.remoteroid.IRemoteroid.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getConnectionStatus:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getConnectionStatus();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_isConnected:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isConnected();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_connect:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.connect(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_disconnect:
{
data.enforceInterface(DESCRIPTOR);
this.disconnect();
reply.writeNoException();
return true;
}
case TRANSACTION_onNotificationCatched:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
long _arg1;
_arg1 = data.readLong();
int _arg2;
_arg2 = data.readInt();
this.onNotificationCatched(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_onSendFile:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<java.lang.String> _arg0;
_arg0 = data.createStringArrayList();
this.onSendFile(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_requestFragmentBeShown:
{
data.enforceInterface(DESCRIPTOR);
this.requestFragmentBeShown();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements org.secmem.remoteroid.IRemoteroid
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public java.lang.String getConnectionStatus() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getConnectionStatus, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean isConnected() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isConnected, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void connect(java.lang.String ipAddress) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(ipAddress);
mRemote.transact(Stub.TRANSACTION_connect, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void disconnect() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_disconnect, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void onNotificationCatched(java.lang.String notificationText, long when, int type) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(notificationText);
_data.writeLong(when);
_data.writeInt(type);
mRemote.transact(Stub.TRANSACTION_onNotificationCatched, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void onSendFile(java.util.List<java.lang.String> pathlist) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStringList(pathlist);
mRemote.transact(Stub.TRANSACTION_onSendFile, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void requestFragmentBeShown() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_requestFragmentBeShown, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_getConnectionStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_isConnected = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_connect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_disconnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_onNotificationCatched = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_onSendFile = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_requestFragmentBeShown = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
}
public java.lang.String getConnectionStatus() throws android.os.RemoteException;
public boolean isConnected() throws android.os.RemoteException;
public void connect(java.lang.String ipAddress) throws android.os.RemoteException;
public void disconnect() throws android.os.RemoteException;
public void onNotificationCatched(java.lang.String notificationText, long when, int type) throws android.os.RemoteException;
public void onSendFile(java.util.List<java.lang.String> pathlist) throws android.os.RemoteException;
public void requestFragmentBeShown() throws android.os.RemoteException;
}
