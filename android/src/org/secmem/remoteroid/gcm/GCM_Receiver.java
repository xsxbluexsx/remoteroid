package org.secmem.remoteroid.gcm;

import org.secmem.remoteroid.activity.Main;
import org.secmem.remoteroid.lib.data.WakeupMessage;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

public class GCM_Receiver extends BroadcastReceiver {
	
	private Context context;
	private final static String TAG = "GCM_Receiver";
	

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		
		
		if(intent.getAction().equals(GcmActionType.ReceiveFromServer.GCM_REGISTRATION)){
			handleRegistration(context, intent);
		}
		else if(intent.getAction().equals(GcmActionType.ReceiveFromServer.GCM_RECEIVE)){
			String msg = intent.getExtras().getString("msg");
			Log.i("qq","GCM_RECEIVE");
			/** auto Login */
			Intent i = new Intent(this.context, Main.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.putExtra(GcmActionType.ActionMessage.ACTION_MESSAGE_IP, intent.getStringExtra(WakeupMessage.IP_ADDRESS));
			
			this.context.startActivity(i);

			
//			PendingIntent pi = PendingIntent.getActivity(this.context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
//			try {
//				pi.send();
//				
//			} catch (CanceledException e) {
//				e.printStackTrace();
//			}
			
			
		}

	}
	
	
	
	private void handleRegistration(Context context, Intent intent) {     
		if(intent.hasExtra("registration_id")){
			Log.i(TAG,"res = "+intent.getExtras().getString("registration_id"));
			Intent send_intent = new Intent("org.secmem.remoteroid.REGI");
			send_intent.putExtra("regi_id", intent.getExtras().getString("registration_id"));
			context.sendBroadcast(send_intent);
		}    	
	}

}
