package org.secmem.remoteroid.receiver;

import java.util.ArrayList;

import org.secmem.remoteroid.data.RDSmsMessage;
import org.secmem.remoteroid.service.RemoteroidService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {
	
	public static final String EXTRA_MSGS = "msg";
		
	@Override
	public void onReceive(Context context, Intent intent) {

		Bundle extras = intent.getExtras();
		
		if(extras != null){
		    Object[] pdus = (Object[])extras.get("pdus");
		    SmsMessage[] messages = new SmsMessage[pdus.length];
		     
		    for(int i=0; i<pdus.length; i++){
		    	messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
		    }
		    
		    ArrayList<RDSmsMessage> rdMsgs = new ArrayList<RDSmsMessage>();
		    
		    for(SmsMessage message: messages){
		    	RDSmsMessage rdMsg = new RDSmsMessage();
		    	
		    	rdMsg.setPhoneNumber(message.getOriginatingAddress());
		    	rdMsg.setMessageBody(message.getMessageBody());
		    	rdMsg.setDeliveredAt(message.getTimestampMillis());
		    	rdMsg.setDisplayedName(context);
		    	
		    	rdMsgs.add(rdMsg);
		    }
		    context.startService(new Intent(context, RemoteroidService.class)
		    	.putParcelableArrayListExtra(EXTRA_MSGS, rdMsgs));
		 }
	}

}
