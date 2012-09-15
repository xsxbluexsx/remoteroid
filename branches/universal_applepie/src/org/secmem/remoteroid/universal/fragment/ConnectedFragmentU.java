package org.secmem.remoteroid.universal.fragment;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.universal.listener.ConnectedFragmentListenerU;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ConnectedFragmentU extends InterfaceFragment<ConnectedFragmentListenerU> {
	
	public ConnectedFragmentU(){
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_connected, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
	}
	
	
}
