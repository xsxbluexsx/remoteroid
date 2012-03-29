package org.secmem.remoteroid.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class SmsSentResultReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
	    switch(getResultCode()){
	    case Activity.RESULT_OK:
	    	Toast.makeText(context, "SMS has been sent.", Toast.LENGTH_SHORT).show();
	    	break;
	    	
	    default:
	    	Toast.makeText(context, "Could not send SMS.", Toast.LENGTH_SHORT).show();
	    	break;
	    }
	}

}
