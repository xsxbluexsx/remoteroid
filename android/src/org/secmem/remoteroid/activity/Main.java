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

package org.secmem.remoteroid.activity;

import org.secmem.remoteroid.IRemoteroid;
import org.secmem.remoteroid.R;
import org.secmem.remoteroid.fragment.AuthenticateFragment;
import org.secmem.remoteroid.fragment.ConnectedFragment;
import org.secmem.remoteroid.fragment.ConnectingFragment;
import org.secmem.remoteroid.fragment.ConnectionStateListener;
import org.secmem.remoteroid.fragment.DriverInstallationFragment;
import org.secmem.remoteroid.intent.RemoteroidIntent;
import org.secmem.remoteroid.service.RemoteroidService;
import org.secmem.remoteroid.service.RemoteroidService.ServiceState;
import org.secmem.remoteroid.util.CommandLine;

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
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class Main extends SherlockFragmentActivity implements
		ConnectionStateListener {

	// Fragments should be in static
	private static Fragment mAuthFragment;
	private static Fragment mConnectingFragment;
	private static Fragment mConnectedFragment;
	private static Fragment mDriverFragment;
	
	private IRemoteroid mRemoteroidSvc;
	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mRemoteroidSvc = IRemoteroid.Stub.asInterface(service);
			try {
				ServiceState status = ServiceState.valueOf(mRemoteroidSvc
						.getConnectionStatus());
				switch (status) {
				case IDLE:
					showFragment(mAuthFragment);
					break;

				case CONNECTING:
					showFragment(mConnectingFragment);
					break;

				case CONNECTED:
					showFragment(mConnectedFragment);
					break;
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mRemoteroidSvc = null;
		}

	};

	private static final int AUTH_FRAG = 0;
	private static final int CONNECTING_FRAG = 1;
	private static final int CONNECTED_FRAG = 2;
	private static final int DRIVER_FRAG = 3;

	private int lastFrag;

	private boolean isDriverInstalled = false;
	
	public void hideAllFragment(){
    	getSupportFragmentManager().beginTransaction()
		.hide(mAuthFragment)
		.hide(mConnectingFragment)
		.hide(mConnectedFragment)
		.hide(mDriverFragment).commit();
    }
	    
   public void showFragment(Fragment fragment){

    	if(mAuthFragment.equals(fragment)){
    		lastFrag = AUTH_FRAG;
    		getSupportFragmentManager().beginTransaction()
    			.show(mAuthFragment)
    			.hide(mConnectingFragment)
    			.hide(mConnectedFragment)
    			.hide(mDriverFragment).commit();
    		
    	}else if(mConnectingFragment.equals(fragment)){
    		lastFrag = CONNECTING_FRAG;
    		getSupportFragmentManager().beginTransaction()
				.hide(mAuthFragment)
				.show(mConnectingFragment)
				.hide(mConnectedFragment)
				.hide(mDriverFragment).commit();
    		
    	}else if(mConnectedFragment.equals(fragment)){
    		lastFrag = CONNECTED_FRAG;
    		getSupportFragmentManager().beginTransaction()
				.hide(mAuthFragment)
				.hide(mConnectingFragment)
				.show(mConnectedFragment)
				.hide(mDriverFragment).commit();
    		 
    	}else{
    		lastFrag = DRIVER_FRAG;
    		getSupportFragmentManager().beginTransaction()
				.hide(mAuthFragment)
				.hide(mConnectingFragment)
				.hide(mConnectedFragment)
				.show(mDriverFragment).commit();
    		
    	}
    }
   
   public void showLastFragment(){
	   switch(lastFrag){
		case AUTH_FRAG:
			showFragment(mAuthFragment);
			break;
		case CONNECTING_FRAG:
			showFragment(mConnectingFragment);
			break;
		case CONNECTED_FRAG:
			showFragment(mConnectedFragment);
			break;
		case DRIVER_FRAG:
			showFragment(mDriverFragment);
			break;
		}
   }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_red));
        
        // instantiate fragments on first run
        if(savedInstanceState==null){
        	mAuthFragment = new AuthenticateFragment(this);
        	mConnectingFragment = new ConnectingFragment(this);
        	mConnectedFragment = new ConnectedFragment(this);
        	mDriverFragment = new DriverInstallationFragment(this);
        }
        isDriverInstalled = CommandLine.isDriverExists(getApplicationContext());     
    }
    
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		lastFrag = savedInstanceState.getInt("lastFrag");
		
		hideAllFragment();
		switch(lastFrag){
		case AUTH_FRAG:
			showFragment(mAuthFragment);
			break;
		case CONNECTING_FRAG:
			showFragment(mConnectingFragment);
			break;
		case CONNECTED_FRAG:
			showFragment(mConnectedFragment);
			break;
		case DRIVER_FRAG:
			showFragment(mDriverFragment);
			break;
		}
		
	}

    
    public void onStart(){
    	super.onStart();
    	
        getSupportFragmentManager().beginTransaction()
		.add(R.id.container, mAuthFragment)
		.add(R.id.container, mConnectingFragment)
		.add(R.id.container, mConnectedFragment)
		.add(R.id.container, mDriverFragment).commit();
    
    	//Check driver existence
        if(!isDriverInstalled){
            showFragment(mDriverFragment);
        }else{
        	bindService(new Intent(this, RemoteroidService.class), conn, Context.BIND_AUTO_CREATE);
       	}
    }

	@Override
	protected void onResume() {
		super.onResume();
		
		// Register receiver to get broadcast from service
		IntentFilter filter = new IntentFilter();
	    filter.addAction(RemoteroidIntent.ACTION_CONNECTED);
	    filter.addAction(RemoteroidIntent.ACTION_CONNECTION_FAILED);
	    filter.addAction(RemoteroidIntent.ACTION_DEVICE_OPEN_FAILED);
	    filter.addAction(RemoteroidIntent.ACTION_INTERRUPTED);
	    filter.addAction(RemoteroidIntent.ACTION_DISCONNECTED);
	    registerReceiver(serviceConnReceiver, filter);
	    
	    if(!isDriverInstalled)
	    	showLastFragment();
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		unregisterReceiver(serviceConnReceiver);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		if(mRemoteroidSvc!=null)
			unbindService(conn);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("lastFrag", lastFrag);
//		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = new MenuInflater(this);
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_main_preferences:
			startActivity(new Intent(this, NotificationReceiverSettings.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	
	private BroadcastReceiver serviceConnReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			
			if(RemoteroidIntent.ACTION_CONNECTED.equals(action))
				onConnected(intent.getStringExtra("ip"));
			else if(RemoteroidIntent.ACTION_DEVICE_OPEN_FAILED.equals(action) || RemoteroidIntent.ACTION_CONNECTION_FAILED.equals(action))
				onConnectionFailed();
			else if(RemoteroidIntent.ACTION_DISCONNECTED.equals(action))
				onDisconnected();
			else if(RemoteroidIntent.ACTION_INTERRUPTED.equals(action))
				onConnectionInterrupted();
        }
        
	};

	@Override
	public void onConnectRequested(String ipAddress, String password) {
		showFragment(mConnectingFragment);
	
		try {
			mRemoteroidSvc.connect(ipAddress, password);
		} catch (RemoteException e) {
			e.printStackTrace();
			
			showFragment(mAuthFragment);
		}
	}
	
	@Override
	public void onConnectionCanceled() {
		Toast.makeText(getApplicationContext(), R.string.connection_cancelled,
				Toast.LENGTH_SHORT).show();
		showFragment(mAuthFragment);
	}

	@Override
	public void onConnectionFailed() {
		Toast.makeText(getApplicationContext(), "Failed to connect server.",
				Toast.LENGTH_SHORT).show();
		// Failed to connect. return to AuthenticateFragment.
		showFragment(mAuthFragment);
	}
	
	@Override
	public void onConnected(String ipAddress) {
		showFragment(mConnectedFragment);
	}

	@Override
	public void onConnectionInterrupted() {
		Toast.makeText(getApplicationContext(), R.string.connection_with_server_has_interrupted, Toast.LENGTH_SHORT).show();
		showFragment(mAuthFragment);
	}
	
	@Override
	public void onDisconnectRequested() {
		try {
			mRemoteroidSvc.disconnect();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDisconnected() {
		Toast.makeText(getApplicationContext(), R.string.disconnected_from_server, Toast.LENGTH_SHORT).show();
		showFragment(mAuthFragment);
	}
	
	@Override
	public void onDriverInstalled() {
		// Proceed to authenticate fragment
		showFragment(mAuthFragment);
		
		// Bind to remoteroid service
		bindService(new Intent(this, RemoteroidService.class), conn, Context.BIND_AUTO_CREATE);
	}

}