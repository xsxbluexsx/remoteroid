package org.secmem.remoteroid.receiver;

import org.secmem.remoteroid.IRemoteroid;
import org.secmem.remoteroid.service.RemoteroidService;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {

	private ServiceConnection mSvcConnection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mRemoteroidSvc = IRemoteroid.Stub.asInterface(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mRemoteroidSvc = null;
		}
		
	};
	
	private IRemoteroid mRemoteroidSvc;
		
	@Override
	public void onReceive(Context context, Intent intent) {
		// Bind to Remoteroid serivce
		context.bindService(new Intent(context, RemoteroidService.class), mSvcConnection, Context.BIND_AUTO_CREATE);
		
		Bundle extras = intent.getExtras();
		
		if(extras != null){
		    Object[] pdus = (Object[])extras.get("pdus");
		    SmsMessage[] messages = new SmsMessage[pdus.length];
		     
		    for(int i=0; i<pdus.length; i++){
		    	messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
		    }
		    
		    for(SmsMessage message : messages){
		    	String msg = message.getMessageBody();
		    	String from = message.getOriginatingAddress();
		    	
		    	try {
		    		// TODO find displayed name of matched phone number
					mRemoteroidSvc.onReceiveSMS(from, from, msg, message.getTimestampMillis());
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch(NullPointerException e){
					e.printStackTrace();
				}
		    }
		 }
		
		// Unbind from service
		context.unbindService(mSvcConnection);
	}

}
