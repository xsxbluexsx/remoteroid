package org.secmem.remoteroid.activity;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.util.Util;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class ConfigurationChecker extends SherlockActivity {
	
	private Button btnEnableAccService;
	private Button btnEnableDevAdmin;
	
	private boolean isAccEnabled;
	private boolean isDAEnabled;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configuration_checker);
		
		btnEnableAccService = (Button)findViewById(R.id.btn_acc_service);
		btnEnableDevAdmin = (Button)findViewById(R.id.btn_device_admin);
		
		btnEnableAccService.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Util.launchAccessibilitySettings(ConfigurationChecker.this);
			}
			
		});
		
		btnEnableDevAdmin.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Util.launchDeviceAdminAccessRequest(ConfigurationChecker.this);
			}
			
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		isAccEnabled = Util.isAccessibilityServiceEnabled(this);
		isDAEnabled = Util.isDeviceAdminEnabled(this);
		
		if(isAccEnabled){
			btnEnableAccService.setEnabled(false);
			btnEnableAccService.setText(R.string.enabled);
		}
		
		if(isDAEnabled){
			btnEnableDevAdmin.setEnabled(false);
			btnEnableDevAdmin.setText(R.string.enabled);
		}
		
		invalidateOptionsMenu(); // Update 'Done' menu availability
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = new MenuInflater(this);
		inflater.inflate(R.menu.configuration_checker, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.getItem(0).setEnabled(isAccEnabled && isDAEnabled);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.done:
			finish();
			startActivity(new Intent(this, Main.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	

}
