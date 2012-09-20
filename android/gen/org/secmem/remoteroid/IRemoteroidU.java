/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\Androidhuman\\Documents\\Eclipse_EE\\Remoteroid\\src\\org\\secmem\\remoteroid\\IRemoteroidU.aidl
 */
package org.secmem.remoteroid;
public interface IRemoteroidU extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements org.secmem.remoteroid.IRemoteroidU
{
private static final java.lang.String DESCRIPTOR = "org.secmem.remoteroid.IRemoteroidU";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an org.secmem.remoteroid.IRemoteroidU interface,
 * generating a proxy if needed.
 */
public static org.secmem.remoteroid.IRemoteroidU asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof org.secmem.remoteroid.IRemoteroidU))) {
return ((org.secmem.remoteroid.IRemoteroidU)iin);
}
return new org.secmem.remoteroid.IRemoteroidU.Stub.Proxy(obj);
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
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements org.secmem.remoteroid.IRemoteroidU
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
}
}
}
