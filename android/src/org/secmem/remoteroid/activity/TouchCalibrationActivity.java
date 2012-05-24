package org.secmem.remoteroid.activity;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.intent.RemoteroidIntent;
import org.secmem.remoteroid.service.CalibrationService;
import org.secmem.remoteroid.util.Util;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class TouchCalibrationActivity extends Activity{
	
	private TextView mTvMessage;
	private ProgressBar mProgress;
	
	private boolean mIsCalibrating = false;
	
	// When activity restarts, non-static variable's value is discarded.
	// Put static modifier to variables below to prevent loss of value.
	private static int displayWidth;
	private static int displayHeight;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_touch_calibrate);
	    
	    mTvMessage = (TextView)findViewById(R.id.touch_calibrate_msg);
	    mProgress = (ProgressBar)findViewById(R.id.touch_calibrate_progress);
	    
	    if(savedInstanceState==null){
			Display display = getWindowManager().getDefaultDisplay();
			displayWidth = display.getWidth();
			displayHeight = display.getHeight();
			mTouchHandler.sendEmptyMessage(MSG_COUNT);
	    }else{
	    	// When virtual event has injected, current activity restarted by unknown reason.
	    	// When calibration finished and about to close activity, onCreate() called
	    	// and initial message displayed on the screen. to prevent this,
	    	// I just put a simple hack for that.
	    	mTvMessage.setText("");
	    }
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		IntentFilter filter = new IntentFilter();
	    filter.addAction(RemoteroidIntent.ACTION_DEVICE_OPEN_FAILED);
	    registerReceiver(DeviceReceiver, filter);
	}

	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(DeviceReceiver);
	}

	private static final int MSG_START = 0;
	private static final int MSG_COUNT = 1;
	
	private int count = 5;
	
	private Handler mTouchHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case MSG_START:
				mIsCalibrating = true;
				Util.Screen.resetScalingFactor(getApplicationContext()); // Reset current scaling factor
				mProgress.setVisibility(View.VISIBLE); // Show progress bar
				startService(
						new Intent(TouchCalibrationActivity.this, CalibrationService.class)
							.putExtra("x", displayWidth/2)
							.putExtra("y", displayHeight/2));
				break;
			case MSG_COUNT:
				if(count>=0){
					mTvMessage.setText(String.format(getString(R.string.calibration_starts_in_s_seconds), count--));
					mTouchHandler.sendEmptyMessageDelayed(MSG_COUNT, 1000);
				}else
					mTouchHandler.sendEmptyMessage(MSG_START);
				break;
			}
		}
	};
	
	@Override
	public void onBackPressed() {
		if(!mIsCalibrating){
			super.onBackPressed();
			mTouchHandler.removeMessages(MSG_COUNT);
			mTouchHandler.removeMessages(MSG_START);
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction()==MotionEvent.ACTION_DOWN){
			
			int centerInWidth = displayWidth/2;
			int centerInHeight = displayHeight/2;
			
			float receivedCenterInWidth = event.getX();
			float receivedCenterInHeight = event.getY();
			
			// Save scaling factor on preferences
			Util.Screen.setScalingFactor(getApplicationContext(), 
					((float)centerInWidth)/receivedCenterInWidth, 
					((float)centerInHeight)/receivedCenterInHeight);
			
			mIsCalibrating = false;

			mProgress.setVisibility(View.GONE); // Hide progress bar
			Toast.makeText(getApplicationContext(), R.string.calibration_completed, Toast.LENGTH_SHORT).show();
			finish();
		}
		return true;
	}
	
	private BroadcastReceiver DeviceReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			Toast.makeText(getApplicationContext(), R.string.calibration_failed, Toast.LENGTH_SHORT).show();
			finish();
		}
		
	};

}
