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

import org.secmem.remoteroid.IRemoteroid;
import org.secmem.remoteroid.R;
import org.secmem.remoteroid.intent.RemoteroidIntent;
import org.secmem.remoteroid.service.RemoteroidService;
import org.secmem.remoteroid.util.HongUtil;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class ConnectingFragment extends Fragment {
	
	public static boolean isFinished=false;
	
	private ImageView mIvCircuit;
	private Button mBtnCancel;
	
	private String mIpAddr;
	private String mPassword;
	
	private Intent frameIntent;
	private IntentFilter filter;
	
	private IRemoteroid mRemoteroidSvc;
	
	private ServiceConnection mConnection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mRemoteroidSvc = IRemoteroid.Stub.asInterface(service);
			try {
				mRemoteroidSvc.connect(mIpAddr, mPassword);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mRemoteroidSvc = null;
		}
		
	};
	
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
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(RemoteroidIntent.ACTION_CONNECTED);
		filter.addAction(RemoteroidIntent.ACTION_INTERRUPTED);
		
		getActivity().registerReceiver(serviceConnReceiver, filter);
		
		// First, start RemoteroidService
		this.getActivity().startService(new Intent(getActivity(), RemoteroidService.class));
		
		// Then bind to service, to prevent service stopping when ConnectingFragment has unbound from service
		this.getActivity().
			bindService(new Intent(getActivity(), RemoteroidService.class), 
					mConnection, Context.BIND_AUTO_CREATE);
		
		/*filter = new IntentFilter();
		filter.addAction("connecting_fragment_connect");
		
		frameIntent = new Intent(getActivity(), FrameBufferService.class);
		frameIntent.putExtra("IP", mIpAddr);
		getActivity().startService(frameIntent);*/
		
		mBtnCancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), R.string.connection_cancelled, Toast.LENGTH_SHORT).show();
				getFragmentManager().beginTransaction().replace(R.id.container, new AuthenticateFragment()).commit();
			}
			
		});
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//getActivity().registerReceiver(service_BR, filter);
	}
	
	@Override
	public void onStop(){
		super.onStop();
		getActivity().unregisterReceiver(serviceConnReceiver);
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//getActivity().unregisterReceiver(service_BR);
	}
	
	private BroadcastReceiver serviceConnReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			
			if(RemoteroidIntent.ACTION_CONNECTED.equals(action)){
				// Connected show ConnectedFragment.
				getFragmentManager().beginTransaction().replace(R.id.container, new ConnectedFragment()).commit();
			}else{
				// Failed to connect. return to AuthenticateFragment.
				getFragmentManager().beginTransaction().replace(R.id.container, new AuthenticateFragment()).commit();
			}
		}
		
	};
	
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
