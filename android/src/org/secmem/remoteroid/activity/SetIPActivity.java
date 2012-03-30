package org.secmem.remoteroid.activity;

import org.secmem.remoteroid.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class SetIPActivity extends Activity implements OnClickListener, OnCheckedChangeListener {

	Button okBtn;
	Button cancelBtn;
	
	EditText ipEdt;
	EditText pwdEdt;
	
	CheckBox autoLogin;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setip_activity);
		
		okBtn = (Button)findViewById(R.id.setip_btn_ok);
		cancelBtn = (Button)findViewById(R.id.setip_btn_cancel);
		
		okBtn.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);
		
		ipEdt = (EditText)findViewById(R.id.setip_edt_ip);
		pwdEdt = (EditText)findViewById(R.id.setip_edt_pwd);
		
		autoLogin = (CheckBox)findViewById(R.id.setip_auto_chkbox);
		autoLogin.setOnCheckedChangeListener(this);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		
		case R.id.setip_btn_ok:
			
			break;
			
		case R.id.setip_btn_cancel:
			finish();
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		
	}
	
}
