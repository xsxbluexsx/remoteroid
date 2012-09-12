package org.secmem.remoteroid.activity;

import org.secmem.remoteroid.intent.RemoteroidIntent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class RemoteConnectRedirector extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    Intent intent = new Intent();
	    intent.setAction(RemoteroidIntent.ACTION_REMOTE_CONNECT);
	    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
	    intent.putExtra(RemoteroidIntent.EXTRA_IP_ADDESS, getIntent().getStringExtra(RemoteroidIntent.EXTRA_IP_ADDESS));
	    finish();
	    startActivity(intent);
	    //finish();
	}

}
