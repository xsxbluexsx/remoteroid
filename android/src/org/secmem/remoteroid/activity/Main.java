package org.secmem.remoteroid.activity;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.util.Util;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;

public class Main extends SherlockActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
    }

	@Override
	protected void onResume() {
		super.onResume();
		
		// Check accessibility service has enabled or not before connect to pc.
		if(!Util.isAccessibilityServiceEnabled(this)){
        	new AlertDialog.Builder(this).setTitle(android.R.string.dialog_alert_title)
        		.setMessage("RemoteroidService has not enabled. Please enable before use.")
        		.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Util.launchAccessibilitySettings(Main.this);
					}
				}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}).setCancelable(false).show();
        }
	}
}