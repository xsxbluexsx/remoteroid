package org.secmem.remoteroid.fragment;

import java.io.IOException;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.util.CommandLine;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DriverInstallationFragment extends Fragment {

	private TextView tvMsg;
	private ProgressBar prgProgress;
	private Button btnConfirm;
	private boolean installCompleted = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_driver_installation, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		tvMsg = (TextView)view.findViewById(R.id.driver_installation_msg);
		prgProgress = (ProgressBar)view.findViewById(R.id.driver_installation_progress);
		btnConfirm = (Button)view.findViewById(R.id.driver_installation_confirm);
		
		btnConfirm.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(!installCompleted){
					// Install Driver
					new InstallDriverTask().execute();
					
				}else{
					// Restart immediately when user presses button after install completed
					CommandLine.restartDevice();
				}
				
			}
			
		});
	}
	
	class InstallDriverTask extends AsyncTask<Void, Void, Integer>{

		private static final int ERR_SECURITY = -1;
		private static final int ERR_IO = -2;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Disable button, change button text, then show progressbar
			btnConfirm.setEnabled(false);
			btnConfirm.setText("Installing...");
			prgProgress.setVisibility(View.VISIBLE);
		}

		@Override
		protected Integer doInBackground(Void... params) {
			try {
				CommandLine.copyInputDrivers(getActivity());
				installCompleted = true;
			} catch (SecurityException e) {
				e.printStackTrace();
				return ERR_SECURITY;
				
			} catch (IOException e) {
				e.printStackTrace();
				return ERR_IO;
			}
			return 0;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			switch(result){
			case ERR_SECURITY:
				// Root access denied
				tvMsg.setText("Failed to get root permission. Check out whether device has rooted or not.");
				btnConfirm.setText("Retry");
				break;
			case ERR_IO:
				tvMsg.setText("Failed to copy driver files.");
				btnConfirm.setText("Retry");
				break;
			default:
				tvMsg.setText("Install completed. You need to restart your device to complete install.");
				rebootHandler.sendEmptyMessage(secondRemaining);
			}	
			prgProgress.setVisibility(View.GONE);
			btnConfirm.setEnabled(true);
		}
	}

	private static final int MSG_RESTART = -1;
	private int secondRemaining = 5;
	
	private Handler rebootHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case MSG_RESTART:
				CommandLine.restartDevice();
				break;
			default:
				btnConfirm.setText(String.format("Restarts in %s", secondRemaining));
				if(secondRemaining>=1)
					rebootHandler.sendEmptyMessageDelayed(secondRemaining--, 1000);
			}
			
		}
		
	};
}

