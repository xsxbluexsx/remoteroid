/*
 * Remoteroid - A remote control solution for Android platform, including handy file transfer and notify-to-PC.
 * Copyright (C) 2012 Taeho Kim(jyte82@gmail.com), Hyomin Oh(ohmnia1112@gmail.com), Hongkyun Kim(godgjdgjd@nate.com), Yongwan Hwang(singerhwang@gmail.com)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package org.secmem.remoteroid.fragment;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.regex.Pattern;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.lib.api.Codes;
import org.secmem.remoteroid.lib.request.Response;
import org.secmem.remoteroid.util.HongUtil;
import org.secmem.remoteroid.util.Util;
import org.secmem.remoteroid.web.RemoteroidWeb;

import android.app.AlertDialog;
import android.app.PendingIntent.CanceledException;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
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
import android.widget.LinearLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class AuthenticateFragment extends Fragment {
	
	private EditText mEdtIpAddr;
	private EditText mEdtPassword;
	private CheckBox mCbAutoConn;
	private Button mBtnConnect;
	private Button mBtnSignUp;

	private boolean isIpValid=false;
	private boolean isPwValid=false;
	
	private ConnectionStateListener mListener;
	
	private ProgressDialog mProgress;
	
	public AuthenticateFragment(){
		
	}
	public AuthenticateFragment(ConnectionStateListener listener){
		this.mListener = listener;
	}

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
		mBtnSignUp = (Button)view.findViewById(R.id.signup);
		
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
				Util.Connection.setAutoConnect(getActivity(), isChecked);
			}
			
		});
		
		// Restore saved connection data from SharedPreferences
		String ipAddrStored = Util.Connection.getIpAddress(getActivity());
		String passwordStored = Util.Connection.getPassword(getActivity());
		
		if(ipAddrStored!=null)
			mEdtIpAddr.setText(ipAddrStored);
		
		if(passwordStored!=null)
			mEdtPassword.setText(passwordStored);
		
		mBtnConnect.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				Util.Connection.saveConnectionData(getActivity(), mEdtIpAddr.getText().toString(), mEdtPassword.getText().toString());
				mListener.onConnectRequested(mEdtIpAddr.getText().toString(), mEdtPassword.getText().toString());
				
			}
		});
		mBtnSignUp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ShowDialog();
			}
		});
		
//		if(Util.Connection.isAutoConnectEnabled(getActivity())){
//			getFragmentManager().beginTransaction().replace(R.id.container, 
//					new ConnectingFragment(Util.Connection.getIpAddress(getActivity()), Util.Connection.getPassword(getActivity())))
//					.commit();
//		}
	}
	
	public void ShowDialog(){
		final LinearLayout linear = (LinearLayout)View.inflate(getActivity(), R.layout.dialog_sign_up, null);
		new AlertDialog.Builder(getActivity())
		.setTitle(getString(R.string.dialog_sign_up_title))
		.setIcon(R.drawable.ic_launcher)
		.setView(linear)
		.setPositiveButton("Account", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText edtEmail = (EditText)linear.findViewById(R.id.dialog_sign_up_edt_email);
				EditText edtPw = (EditText)linear.findViewById(R.id.dialog_sign_up_edt_pw);
				
				if(edtEmail.getText().length()==0 || edtPw.getText().length()==0){
					if(edtEmail.getText().length()==0)
						HongUtil.makeToast(getActivity(), getActivity().getString(R.string.dialog_sign_up_input_email));
					else
						HongUtil.makeToast(getActivity(), getActivity().getString(R.string.dialog_sign_up_input_pwd));
					ShowDialog();
				}
				else{
					new SignUpAsync().execute(edtEmail.getText().toString(), edtPw.getText().toString());
				}
				
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		})
		.show();
	}
	
	public class SignUpAsync extends AsyncTask<String, Void, Integer>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgress = new ProgressDialog(getActivity());
			mProgress.setTitle("Loading...");
			mProgress.setMessage("Sign Up.......");
			mProgress.show();
		}
		
		@Override
		protected Integer doInBackground(String... params) {
			String email = params[0];
			String pw = params[1];
			Response response = null;
			try {
				response = RemoteroidWeb.addAccount(email, pw);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return (response !=null && response.isSucceed())? Codes.Result.OK : Codes.Result.FAILED;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			
			mProgress.dismiss();
			if(result == Codes.Result.OK){
				HongUtil.makeToast(getActivity(), "Success.");
			}
			else if(result == Codes.Result.FAILED){
				HongUtil.makeToast(getActivity(), "Please check your email again");
				ShowDialog();
			}
			
		}
	}

}
