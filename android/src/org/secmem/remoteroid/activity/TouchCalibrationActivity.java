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
import android.graphics.Point;
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
		if(count>=0){
			mTouchHandler.removeMessages(MSG_COUNT);
			mTouchHandler.removeMessages(MSG_START);
		}
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
							.putExtra("width", displayWidth)
							.putExtra("height", displayHeight));
				break;
			case MSG_COUNT:
				if(count>=0){
					mTvMessage.setText(String.format(getResources().getQuantityString(R.plurals.calibration_starts_in_s_seconds, count), count--));
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
	
	private static Point[] calPoints = new Point[3];
	private static int touchCnt = 0;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction()==MotionEvent.ACTION_DOWN){
			
			if(count>=0){
				// Calibration cancelled
				Toast.makeText(getApplicationContext(), R.string.calibration_failed, Toast.LENGTH_SHORT).show();
				finish();
				return true;
			}
			
			// Save first, second touch input (0,0), (width, 0)
			if(touchCnt<2){
				calPoints[touchCnt].x = (int)event.getX();
				calPoints[touchCnt].y = (int)event.getY();
				touchCnt++;
				return true;
			}
			
			// Save last touch pointer (0, height)
			calPoints[touchCnt].x = (int)event.getX();
			calPoints[touchCnt].y = (int)event.getY();
			
			// We got all pointer that needed to calibration.
			// First, calculate actual pointer area based on pointer that we received
			int actualWidth = calPoints[1].x - calPoints[0].x;
			int actualHeight = calPoints[2].y - calPoints[0].y;
			
			// Second, calculate scaling factor for each axis
			float xScaleFactor = displayWidth / (float)actualWidth;
			float yScaleFactor = displayHeight / (float)actualHeight;
			
			// Third, calculate offset for each axis
			int xOffset = 0 - calPoints[0].x;
			int yOffset = 0 - calPoints[0].y;
			
			// We calculated all metrics which is needed for calibration.
			// Now, store data into SharedPreferences.
			
			Util.Screen.setScalingFactor(getApplicationContext(), xScaleFactor, yScaleFactor);
			Util.Screen.setOffset(getApplicationContext(), xOffset, yOffset);
			
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
