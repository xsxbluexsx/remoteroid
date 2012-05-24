package org.secmem.remoteroid.activity;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.view.CalibrationView;
import org.secmem.remoteroid.view.CalibrationView.OnCalibrateListener;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TouchCalibrationActivity extends Activity implements OnCalibrateListener{
	
	private ImageView imgTouchPtr;
	private CalibrationView mCalView;
	private Button btnCalibrate;
	private TextView txtMessage;
	
	private boolean isCalibrated = false;
	private boolean isCalibrating = false;
	
	private int ptrStartX;
	private int ptrStartY;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_touch_calibrate);
	    
	    imgTouchPtr = (ImageView)findViewById(R.id.touch_calibrate_coordinate_img);
	    mCalView = (CalibrationView)findViewById(R.id.touch_calibrate_calibration_view);
	    btnCalibrate = (Button)findViewById(R.id.touch_calibrate_calibrate);
	    txtMessage = (TextView)findViewById(R.id.touch_calibrate_msg);
	    
	    mCalView.setCalibrateListener(this);
	    btnCalibrate.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(!isCalibrated){
					mCalView.setClickable(true);
					mCalView.requestCalibrate();
				}else{
					finish();
				}
			}
	    });
	}

	@Override
	public void onBackPressed() {
		if(!isCalibrating)
			super.onBackPressed();
	}

	@Override
	public void onStartCalibrate() {
		isCalibrating = true;
		txtMessage.setText(R.string.calibrate_in_progress);
		btnCalibrate.setEnabled(false);
	}

	@Override
	public void onError() {
		System.out.println("onError()");
		isCalibrating = false;
		txtMessage.setText(R.string.calibration_failed);
		btnCalibrate.setText(R.string.retry);
		btnCalibrate.setEnabled(false);
	}

	@Override
	public void onReceiveRawCoordinate(int x, int y) {
		System.out.println("onReceiveRawCoord()");
		ptrStartX = x;
		ptrStartY = y;
		imgTouchPtr.setVisibility(View.VISIBLE);
	}

	@Override
	public void onFinishCalibrate(float xScale, float yScale) {
		System.out.println("ScaleX="+xScale+", ScaleY="+yScale);
		isCalibrating = false;
		isCalibrated = true;
		txtMessage.setText(R.string.calibration_completed);
		btnCalibrate.setText(android.R.string.ok);
		btnCalibrate.setEnabled(true);
		Toast.makeText(getApplicationContext(), R.string.calibration_completed, Toast.LENGTH_SHORT).show();
		finish();
	}

}
