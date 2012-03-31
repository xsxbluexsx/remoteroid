package org.secmem.remoteroid.activity;

import org.secmem.remoteroid.util.Util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Redirector extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    if(!Util.isAccessibilityServiceEnabled(this) || !Util.isDeviceAdminEnabled(this)){
	    	startActivity(new Intent(this, ConfigurationChecker.class));
	    }else{
	    	startActivity(new Intent(this, Main.class));
	    }
	}

}
