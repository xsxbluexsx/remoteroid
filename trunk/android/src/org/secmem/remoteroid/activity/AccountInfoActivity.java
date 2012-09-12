package org.secmem.remoteroid.activity;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.intent.RemoteroidIntent;
import org.secmem.remoteroid.lib.api.API;
import org.secmem.remoteroid.lib.data.Account;
import org.secmem.remoteroid.lib.data.Device;
import org.secmem.remoteroid.lib.request.Request;
import org.secmem.remoteroid.lib.request.Response;
import org.secmem.remoteroid.lib.util.DeviceUUIDGenerator;
import org.secmem.remoteroid.util.DeviceUUIDGeneratorImpl;
import org.secmem.remoteroid.util.DialogAsyncTask;
import org.secmem.remoteroid.util.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class AccountInfoActivity extends SherlockActivity {

	private Button btnLogout;
	private TextView tvUserAccount;
	private TextView tvDeviceRegistrationStatus;
	private Button btnRegisterDevice;
	private Button btnChangeDeviceNickname;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_account_info);
	    
	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	    getSupportActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_red));
	    
	    if(!Util.Connection.isUserAccountSet(getApplicationContext())){
	    	// If user did not logged-in into remoteroid account,
	    	// proceed to login screen.
	    	startActivity(new Intent(RemoteroidIntent.ACTION_LOGIN));
	    	finish();
	    	return;
	    }
	    tvDeviceRegistrationStatus = (TextView)findViewById(R.id.activity_account_info_device_registration_status);
	    
	    btnRegisterDevice = (Button)findViewById(R.id.activity_account_info_register_device);
	    btnChangeDeviceNickname = (Button)findViewById(R.id.activity_account_info_set_device_nickname);
	    
	    btnRegisterDevice.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
	    	
	    });
	    
	    btnChangeDeviceNickname.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
	    	
	    });
	    
	    tvUserAccount = (TextView)findViewById(R.id.activity_account_info_user_account);
	    tvUserAccount.setText(Util.Connection.getUserEmail(getApplicationContext()));
	    
	    btnLogout = (Button)findViewById(R.id.activity_account_info_logout);
	    btnLogout.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(AccountInfoActivity.this)
					.setTitle(android.R.string.dialog_alert_title)
					.setMessage(R.string.proceed_logout)
					.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Util.Connection.setUserAccountEnabled(getApplicationContext(), false);
							finish();
							
						}
					}).setNegativeButton(android.R.string.no, null)
					.show();
			}
	    	
	    });
	    
	    new GetDeviceInfoTask(this).setFinishOnCancel(true).execute();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	class GetDeviceInfoTask extends DialogAsyncTask<Void, Void, Response>{

		public GetDeviceInfoTask(Activity context) {
			super(context);
		}

		@Override
		protected Response doInBackground(Void... args) {
			Account account = Util.Connection.getUserAccount(getApplicationContext());
			Device device = new Device();
			DeviceUUIDGenerator generator = new DeviceUUIDGeneratorImpl(getApplicationContext());
			device.setDeviceUUID(generator);
			
			device.setOwnerAccount(account);
			
			Request request = Request.Builder.setRequest(API.Device.RETRIEVE_DEVICE_INFO).setPayload(device).build();
			
			return request.sendRequest();
		}

		@Override
		protected void onPostExecute(Response result) {
			super.onPostExecute(result);
			if(result.isSucceed()){
				btnChangeDeviceNickname.setVisibility(View.VISIBLE);
				btnRegisterDevice.setVisibility(View.GONE);
				tvDeviceRegistrationStatus.setText("Device name");
				tvDeviceRegistrationStatus.setVisibility(View.VISIBLE);
			}else{
				btnChangeDeviceNickname.setVisibility(View.GONE);
				btnRegisterDevice.setVisibility(View.VISIBLE);
				tvDeviceRegistrationStatus.setText("Device not registered.");
				tvDeviceRegistrationStatus.setVisibility(View.VISIBLE);
			}
		}
		
	}

}
