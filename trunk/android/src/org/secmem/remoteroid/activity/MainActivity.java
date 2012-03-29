package org.secmem.remoteroid.activity;

import org.secmem.remoteroid.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {
	
	public static boolean isSetting = false;
	
	private Button ipBtn;
	private Button expBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		ipBtn = (Button)findViewById(R.id.main_btn_ip);
		expBtn = (Button)findViewById(R.id.main_btn_explorer);
		
		ipBtn.setOnClickListener(this);
		expBtn.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isSetting=false;
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
		
		Intent i=null;
		switch(v.getId()){
		
		case R.id.main_btn_ip:
			i = new Intent(MainActivity.this, SetIPActivity.class);
			break;
			
		case R.id.main_btn_explorer:
			i = new Intent(MainActivity.this, ExplorerActivity.class);
			break;
		}
		
		startActivity(i);
		
	}

	private void checkSetting() {
		if(isSetting){
			
		}
		else{
			
		}
	}

}
