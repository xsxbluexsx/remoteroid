package org.secmem.remoteroid.activity;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.util.Util;

import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class Main extends SherlockActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_red));
       
    }

	@Override
	protected void onResume() {
		super.onResume();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = new MenuInflater(this);
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.menu_main_preferences:
			// TODO move following 'lock screen' code to proper place
			/*DevicePolicyManager mDpm = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
			if(!mDpm.isAdminActive(ComponentName.unflattenFromString("org.secmem.remoteroid/org.secmem.remoteroid.receiver.RemoteroidDeviceAdminReceiver"))){
				startActivity(new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, ComponentName.unflattenFromString("org.secmem.remoteroid/org.secmem.remoteroid.receiver.RemoteroidDeviceAdminReceiver")));
			}
			else
				mDpm.lockNow();*/
			startActivity(new Intent(this, NotificationReceiverSettings.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
}