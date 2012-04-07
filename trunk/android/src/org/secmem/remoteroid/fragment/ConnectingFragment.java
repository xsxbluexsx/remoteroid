package org.secmem.remoteroid.fragment;

import org.secmem.remoteroid.R;

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
	
	private ImageView mIvCircuit;
	private Button mBtnCancel;
	
	private String mIpAddr;
	private String mPassword;

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
		
		mBtnCancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO cancel connection
				getFragmentManager().beginTransaction().replace(R.id.container, new AuthenticateFragment()).commit();
			}
			
		});
	}
}
