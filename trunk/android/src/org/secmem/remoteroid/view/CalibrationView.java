package org.secmem.remoteroid.view;

import org.secmem.remoteroid.natives.InputHandler;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

public class CalibrationView extends View implements OnTouchListener{
	
	private OnCalibrateListener mListener;
	private InputHandler mInputHandler;
	private boolean isCalibrateRequested = false;
	
	private int displayWidth;
	private int displayHeight;

	public CalibrationView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CalibrationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CalibrationView(Context context) {
		super(context);
		init();
	}
	
	public void requestCalibrate(){
		
		System.out.println("requestCalibrate, calReqested="+isCalibrateRequested);
		boolean isOpened = mInputHandler.open();
		if(!isOpened){
			mListener.onError();
		}else{
			mListener.onStartCalibrate();
			mTouchHandler.sendEmptyMessageDelayed(0, 2000);
		}
		
	}
	
	public int getDisplayWidth() {
		return displayWidth;
	}

	public int getDisplayHeight() {
		return displayHeight;
	}

	public void setCalibrateListener(OnCalibrateListener listener){
		this.mListener = listener;
	}
	
	@SuppressWarnings("deprecation")
	private void init(){
		setOnTouchListener(this);
		if(!isInEditMode()){
			mInputHandler = new InputHandler();
			WindowManager windowManager = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
			Display display = windowManager.getDefaultDisplay();
			displayWidth = display.getWidth();
			displayHeight = display.getHeight();
		}
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		System.out.println("onTouch, act="+event.getAction()+" x="+event.getX()+" y="+event.getY());
		System.out.println("onTouch, calReqested="+isCalibrateRequested);
		
			System.out.println("inner");
			int centerInWidth = displayWidth/2;
			int centerInHeight = displayHeight/2;
			
			float receivedCenterInWidth = event.getX();
			float receivedCenterInHeight = event.getY();
			
			mListener.onReceiveRawCoordinate((int)receivedCenterInWidth, (int)receivedCenterInHeight);	
			mListener.onFinishCalibrate((float)centerInWidth/receivedCenterInWidth, (float)centerInHeight/receivedCenterInHeight);
			//isCalibrateRequested = false;
			mInputHandler.close();
		
		return true;
	}
	
	private Handler mTouchHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			System.out.println("Dispatching touch..");
			isCalibrateRequested = true;
			mInputHandler.touchOnce(displayWidth/2, displayHeight/2);
		}
		
	};
	public interface OnCalibrateListener{
		public void onStartCalibrate();
		public void onError();
		public void onReceiveRawCoordinate(int x, int y);
		public void onFinishCalibrate(float xScale, float yScale);
	}

}
