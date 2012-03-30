package org.secmem.remoteroid.activity;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.util.HongUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class MainActivity extends Activity implements OnClickListener {
	
	
	private Button expBtn;
	
	private EditText ipEdt;
	private EditText pwEdt;
	private Button okBtn;
	private CheckBox autoCb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		expBtn = (Button)findViewById(R.id.main_btn_explorer);
		okBtn = (Button)findViewById(R.id.main_btn_ok);
		
		expBtn.setOnClickListener(this);
		okBtn.setOnClickListener(this);
		
		ipEdt = (EditText)findViewById(R.id.main_edt_ip);
		pwEdt = (EditText)findViewById(R.id.main_edt_pwd);
		
		autoCb =(CheckBox)findViewById(R.id.main_auto_chkbox); 
		
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
		
		case R.id.main_btn_explorer:
			Intent i = new Intent(MainActivity.this, ExplorerActivity.class);
			startActivity(i);
			break;
		
		case R.id.main_btn_ok:
			checkLogin();
			
			break;
		}
	}

	private void checkLogin() {
		if(ipEdt.getText().toString().length()==0){
			HongUtil.makeToast(MainActivity.this, "IP를 입력해주세요.");
		}
		else if(ipEdt.getText().toString().indexOf(" ")!=-1){
			HongUtil.makeToast(MainActivity.this, "공백이 있습니다.");
		}
		else if(pwEdt.getText().toString().length()==0){
			HongUtil.makeToast(MainActivity.this, "PW를 입력해주세요.");
		}
		
	}
}
