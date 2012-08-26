package org.secmem.remoteroid.gcm;

import org.secmem.remoteroid.activity.Main;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

public class GCM_Receiver extends BroadcastReceiver {
	
	private Context context;
	private PowerManager.WakeLock wl;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("qq","onReceive");
		this.context = context;
		PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "my_wake");
		wl.acquire();
		
		if(intent.getAction().equals(GcmActionType.ReceiveFromServer.GCM_REGISTRATION)){
			handleRegistration(context, intent);
		}
		else if(intent.getAction().equals(GcmActionType.ReceiveFromServer.GCM_RECEIVE)){
			String msg = intent.getExtras().getString("msg");
			
			/** auto Login */
			Intent i = new Intent(this.context, Main.class);
//			i.putExtra(GcmActionType.ActionMessage.ACTION_MESSAGE_IP, ip);
//			i.putExtra(GcmActionType.ActionMessage.ACTION_MESSAGE_PASSWORD, pw);
			PendingIntent pi = PendingIntent.getActivity(this.context, 0, i, PendingIntent.FLAG_ONE_SHOT);
			try {
				pi.send();
			} catch (CanceledException e) {
				// TODO: handle exception
			}
		}

	}
	
	private void handleRegistration(Context context, Intent intent) {     
		Log.i("qq","handleRegistration");
		if(intent.hasExtra("registration_id")){
			Log.i("qq","res = "+intent.getExtras().getString("registration_id"));
			Intent send_intent = new Intent("org.secmem.remoteroid.REGI");
			send_intent.putExtra("regi_id", intent.getExtras().getString("registration_id"));
			context.sendBroadcast(send_intent);
		}    	
	}

}
