/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\AndroidProject\\Remoteroid\\src\\org\\secmem\\remoteroid\\universal\\service\\IRemoteroidU.aidl
 */
package org.secmem.remoteroid.universal.service;
public interface IRemoteroidU extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements org.secmem.remoteroid.universal.service.IRemoteroidU
{
private static final java.lang.String DESCRIPTOR = "org.secmem.remoteroid.universal.service.IRemoteroidU";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an org.secmem.remoteroid.universal.service.IRemoteroidU interface,
 * generating a proxy if needed.
 */
public static org.secmem.remoteroid.universal.service.IRemoteroidU asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof org.secmem.remoteroid.universal.service.IRemoteroidU))) {
return ((org.secmem.remoteroid.universal.service.IRemoteroidU)iin);
}
return new org.secmem.remoteroid.universal.service.IRemoteroidU.Stub.Proxy(obj);
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
case TRANSACTION_requestBroadcastConnectionState:
{
data.enforceInterface(DESCRIPTOR);
this.requestBroadcastConnectionState();
reply.writeNoException();
return true;
}
case TRANSACTION_isCommandConnected:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isCommandConnected();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_isScreenConnected:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isScreenConnected();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_connectCommand:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.connectCommand(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_connectScreen:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.connectScreen(_arg0);
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
case TRANSACTION_disconnectScreen:
{
data.enforceInterface(DESCRIPTOR);
this.disconnectScreen();
reply.writeNoException();
return true;
}
case TRANSACTION_onNotification:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String[] _arg1;
_arg1 = data.createStringArray();
this.onNotification(_arg0, _arg1);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements org.secmem.remoteroid.universal.service.IRemoteroidU
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
public void requestBroadcastConnectionState() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_requestBroadcastConnectionState, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean isCommandConnected() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isCommandConnected, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean isScreenConnected() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isScreenConnected, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void connectCommand(java.lang.String ipAddress) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(ipAddress);
mRemote.transact(Stub.TRANSACTION_connectCommand, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void connectScreen(java.lang.String ipAddress) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(ipAddress);
mRemote.transact(Stub.TRANSACTION_connectScreen, _data, _reply, 0);
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
public void disconnectScreen() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_disconnectScreen, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void onNotification(int notificationType, java.lang.String[] args) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(notificationType);
_data.writeStringArray(args);
mRemote.transact(Stub.TRANSACTION_onNotification, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_requestBroadcastConnectionState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_isCommandConnected = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_isScreenConnected = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_connectCommand = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_connectScreen = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_disconnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_disconnectScreen = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_onNotification = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
}
public void requestBroadcastConnectionState() throws android.os.RemoteException;
public boolean isCommandConnected() throws android.os.RemoteException;
public boolean isScreenConnected() throws android.os.RemoteException;
public void connectCommand(java.lang.String ipAddress) throws android.os.RemoteException;
public void connectScreen(java.lang.String ipAddress) throws android.os.RemoteException;
public void disconnect() throws android.os.RemoteException;
public void disconnectScreen() throws android.os.RemoteException;
public void onNotification(int notificationType, java.lang.String[] args) throws android.os.RemoteException;
}
