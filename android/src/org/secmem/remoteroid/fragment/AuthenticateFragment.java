package org.secmem.remoteroid.fragment;

import java.util.regex.Pattern;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.util.Util;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class AuthenticateFragment extends Fragment {
	
	private EditText mEdtIpAddr;
	private EditText mEdtPassword;
	private CheckBox mCbAutoConn;
	private Button mBtnConnect;
	
	private boolean isIpValid=false;
	private boolean isPwValid=false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_authenticate, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		mEdtIpAddr = (EditText)view.findViewById(R.id.ip_address);
		mEdtPassword = (EditText)view.findViewById(R.id.password);
		mCbAutoConn = (CheckBox)view.findViewById(R.id.auto_connect);
		mBtnConnect = (Button)view.findViewById(R.id.connect);
		
		mEdtIpAddr.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable arg0) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if(Pattern.matches("^(([2][5][0-5]|[2][0-4][0-9]|[0-1][0-9][0-9]|[0-9][0-9]|[0-9])\\.){3}([2][5][0-5]|[2][0-4][0-9]|[0-1][0-9][0-9]|[0-9][0-9]|[0-9])$", s))
					isIpValid = true;
				else
					isIpValid = false;
				mBtnConnect.setEnabled(isIpValid&&isPwValid);
			}
			
		});
		
		mEdtPassword.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {	
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if(count>0)
					isPwValid = true;
				else
					isPwValid = false;
				mBtnConnect.setEnabled(isIpValid&&isPwValid);
			}
			
		});
		
		mCbAutoConn.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Util.setAutoConnect(getActivity(), isChecked);
			}
			
		});
		
		mBtnConnect.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				getFragmentManager().beginTransaction().replace(R.id.container, 
						new ConnectingFragment(mEdtIpAddr.getText().toString(), mEdtPassword.getText().toString()))
						.commit();
			}
			
		});
		
		if(Util.isAutoConnectEnabled(getActivity())){
			getFragmentManager().beginTransaction().replace(R.id.container, 
					new ConnectingFragment(Util.getIpAddress(getActivity()), Util.getPassword(getActivity())))
					.commit();
		}
	}

}
