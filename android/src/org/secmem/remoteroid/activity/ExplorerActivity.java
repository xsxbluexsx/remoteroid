package org.secmem.remoteroid.activity;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.adapter.DataList;
import org.secmem.remoteroid.adapter.ExplorerAdapter;
import org.secmem.remoteroid.expinterface.OnFileSelectedListener;
import org.secmem.remoteroid.expinterface.OnPathChangedListener;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

public class ExplorerActivity extends Activity implements OnScrollListener {
	
	public static boolean SCROLL_STATE = false;
	
	Button categoryBtn;
	TextView pathTv;
	
	GridView gridview;
	ExplorerAdapter adapter;
	
	DataList dataList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.explorer_activity);	
		
		categoryBtn = (Button)findViewById(R.id.explorer_btn_category);
		categoryBtn.setOnClickListener(topBtnListener);
		
		pathTv = (TextView)findViewById(R.id.explorer_tv_path);
		
		gridview = (GridView)findViewById(R.id.explorer_view_grid);
		gridview.setOnScrollListener(this);
		
		dataList = new DataList(this);
		dataList.setOnPathChangedListener(onPathChanged);
		dataList.setOnFileSelected(onFileSelected);
		
		dataList.setPath("/mnt/sdcard");
		
		Log.i("qq","actSize = "+dataList.getExpList().size());
		
//		for (int i = 0 ; i < dataList.getExpList().size() ; i++){
//			Log.i("qq",i + " = "+dataList.getExpList().get(i).getType());
//		}
		adapter = new ExplorerAdapter(this, R.layout.grid_explorer_row, dataList);
		gridview.setAdapter(adapter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	OnClickListener topBtnListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			
			case R.id.explorer_btn_category:
				
				break;
			}
			
		}
	};
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch(scrollState){
		case OnScrollListener.SCROLL_STATE_IDLE:
			SCROLL_STATE= false;
			break;
			
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			SCROLL_STATE = true;
			break;
			
		case OnScrollListener.SCROLL_STATE_FLING:
			SCROLL_STATE = true;
			break;		
		}
	}
	
	private OnPathChangedListener onPathChanged = new OnPathChangedListener() {
		public void onChanged(String path) {
			pathTv.setText(path);
		}
	};
    
    private OnFileSelectedListener onFileSelected = new OnFileSelectedListener() {
		public void onSelected(String path, String fileName) {
			// TODO
		}
	};
}
