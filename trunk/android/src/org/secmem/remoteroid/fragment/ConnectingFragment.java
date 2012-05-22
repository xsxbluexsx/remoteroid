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

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.service.FrameBufferService;
import org.secmem.remoteroid.util.HongUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

public class ConnectingFragment extends Fragment {
	
	public static boolean isFinished=false;
	
	
	private ImageView mIvCircuit;
	private Button mBtnCancel;
	
	private String mIpAddr;
	private String mPassword;
	
	private Intent frameIntent;
	private IntentFilter filter;
	
	public ConnectingFragment(String ipAddr, String password){
		mIpAddr = ipAddr;
		mPassword = password;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_connecting, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mIvCircuit = (ImageView)view.findViewById(R.id.circuit_board);
		mBtnCancel = (Button)view.findViewById(R.id.cancel);
		mIvCircuit.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.blink));
		
		isFinished = false;
		
		filter = new IntentFilter();
		filter.addAction("connecting_fragment_connect");
		
		frameIntent = new Intent(getActivity(), FrameBufferService.class);
		frameIntent.putExtra("IP", mIpAddr);
		getActivity().startService(frameIntent);
		
		mBtnCancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO cancel connection
				getFragmentManager().beginTransaction().replace(R.id.container, new AuthenticateFragment()).commit();
			}
			
		});
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getActivity().registerReceiver(service_BR, filter);
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		getActivity().unregisterReceiver(service_BR);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		isFinished = true;
	}
	
	private BroadcastReceiver service_BR = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			if(intent.getAction().equals("connecting_fragment_connect")){
				if(intent.getBooleanExtra("isConnected", true)){
					HongUtil.makeToast(getActivity(), "Success");
				}
				else{
					HongUtil.makeToast(getActivity(), "Fail");
				}
				getFragmentManager().beginTransaction().replace(R.id.container, new AuthenticateFragment()).commit();
			}
		}
	};
}
