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

import java.util.ArrayList;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.adapter.DataList;
import org.secmem.remoteroid.adapter.ExplorerAdapter;
import org.secmem.remoteroid.data.CategoryList;
import org.secmem.remoteroid.dialog.CategoryDialog;
import org.secmem.remoteroid.expinterface.OnFileSelectedListener;
import org.secmem.remoteroid.expinterface.OnPathChangedListener;
import org.secmem.remoteroid.util.HongUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class ExplorerActivity extends SherlockActivity implements OnScrollListener {
	
	public static boolean SCROLL_STATE = false;
	public static ArrayList<CategoryList> searchList = new ArrayList<CategoryList>();
	public static boolean isSearched=false;
	
	private static int CODE_CATEGORY = 1;
	
	public static String TYPE_IMAGE = "0";
	public static String TYPE_VIDEO = "1";
	public static String TYPE_MUSIC = "2";
	public static String TYPE_CUTSOM = "3";
	
	public static int ADAPTER_TYPE_EXPLORER = 1;
	public static int ADAPTER_TYPE_CATEGORY = 2;
	
	private DataList dataList;
	
	private Button categoryBtn;
	private Button homeBtn;
	private Button topBtn;
	
	private TextView pathTv;
	
	private GridView gridview;
	private ExplorerAdapter adapter;
	
	private boolean isTimer=false;
	
	private ProgressDialog mProgress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.explorer_activity);	
		
		categoryBtn = (Button)findViewById(R.id.explorer_btn_category);
		topBtn = (Button)findViewById(R.id.explorer_btn_top);
		homeBtn = (Button)findViewById(R.id.explorer_btn_home);
		
		categoryBtn.setOnClickListener(topBtnListener);
		topBtn.setOnClickListener(topBtnListener);
		homeBtn.setOnClickListener(topBtnListener);
		
		pathTv = (TextView)findViewById(R.id.explorer_tv_path);
		
		gridview = (GridView)findViewById(R.id.explorer_view_grid);
		gridview.setOnScrollListener(this);
		
		dataList = new DataList(this);
		dataList.setOnPathChangedListener(onPathChanged);
		dataList.setOnFileSelected(onFileSelected);
		
		dataList.setPath("/mnt/sdcard");
		
		adapter = new ExplorerAdapter(this, R.layout.grid_explorer_row, dataList, ADAPTER_TYPE_EXPLORER);
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
			
			case R.id.explorer_btn_home : 			// 홈
				
				if(adapter.getType()==ADAPTER_TYPE_CATEGORY){
					adapter.setType(ADAPTER_TYPE_EXPLORER);
					adapter.getFileInfo().clear();
				}
				dataList.setPath("/mnt/sdcard");
				setDisplayView();
				
				break;
			
			case R.id.explorer_btn_top : 				// 상위.
				
				if(adapter.getType()==ADAPTER_TYPE_CATEGORY){
					adapter.setType(ADAPTER_TYPE_EXPLORER);
					adapter.getFileInfo().clear();
					setDisplayView();
				}
				else{
					String backPath = dataList.getBackPathName();
					if(dataList.getPathCount()!=0){
						dataList.setPath(backPath);
						setDisplayView();
					}
					else{
						HongUtil.makeToast(ExplorerActivity.this, "가장 상위 폴더 입니다.^^");
					}
				}
				
				
				break;
			
			case R.id.explorer_btn_category:			// 카테고리
				
				Intent intent = new Intent(ExplorerActivity.this, CategoryDialog.class);
				startActivityForResult(intent, CODE_CATEGORY);
				
				break;
			}
		}
	};
	
	public void onBackPressed() {
		
		String backPath = dataList.getBackPathName();
		
		if(adapter.getType()==ADAPTER_TYPE_CATEGORY){
			adapter.setType(ADAPTER_TYPE_EXPLORER);
			adapter.getFileInfo().clear();
			setDisplayView();
		}
		else if(dataList.getPath().equals("/mnt/sdcard/")){
			if(!isTimer){
				HongUtil.makeToast(ExplorerActivity.this, "\'뒤로가기\' 버튼을 한번더 누르시면 종료됩니다.");
				backTimer timer = new backTimer(2000, 1);
				timer.start();
			}
			else{
//				android.os.Process.killProcess(android.os.Process.myPid());
				finish();
			}
		}
		else if(dataList.getPath().equals("/mnt/")){
			dataList.setPath("/mnt/sdcard");
			setDisplayView();
		}
		else if(dataList.getPath().equals("/")){
			dataList.setPath("/mnt/");
			setDisplayView();
		}
		else{
			dataList.setPath(backPath);
			setDisplayView();
		}
		
//		if(dataList.getPathCount()==0){
//			finish();
//		}
	};
	
	private void setDisplayView(){
		adapter.notifyDataSetChanged();
		gridview.setSelection(20);
		gridview.invalidateViews();
		
	}
	
	public class backTimer extends CountDownTimer{
		public backTimer(long millisInFuture , long countDownInterval){
			super(millisInFuture, countDownInterval);
			isTimer = true;
		}

		@Override
		public void onFinish() {
			isTimer = false;
		}

		@Override
		public void onTick(long millisUntilFinished) {
			
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			if(requestCode == CODE_CATEGORY){
				
				Log.i("qq","result = "+data.getStringExtra("category"));
				
				if(searchList.size()!=0){
					searchList.clear();
				}
				
				String index = "."+data.getStringExtra("category");
				String type = data.getStringExtra("type");
				
				new SearchAsync().execute(index,type);
				
			}
		}
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch(scrollState){
		case OnScrollListener.SCROLL_STATE_IDLE:
			SCROLL_STATE= false;
			adapter.notifyDataSetChanged();
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
	
	private class SearchAsync extends AsyncTask<String, Void, String>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgress = new ProgressDialog(ExplorerActivity.this);
			mProgress.setTitle("검색중입니다.");
			mProgress.setMessage("파일을 검색중입니다.");
			mProgress.show();
			isSearched=true;
		}

		@Override
		protected String doInBackground(String... params) {
			String type = params[1];
			
			if(type.equals(TYPE_IMAGE)){
				String[] projection = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
				Cursor imageCursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
				HongUtil.getPhoto(imageCursor);
			}
			
			else if(type.equals(TYPE_VIDEO)){
				String[] infoVideo = { MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA};
				Cursor cursor = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, infoVideo, null, null, null);
				HongUtil.getVideo(cursor);
			}
			
			else if(type.equals(TYPE_MUSIC)){
				String[] mediaData = {MediaStore.Audio.Media.DATA, MediaStore.Audio.Media._ID, MediaStore.Audio.Media.ALBUM_ID};
				Cursor cursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mediaData, null, null, null);
				HongUtil.getMusic(cursor);
			}
			
			else if(type.equals(TYPE_CUTSOM)){
				HongUtil.searchIndex(HongUtil.getRootPath(), params[0]);
			}
			
			return type;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			adapter.setType(ADAPTER_TYPE_CATEGORY);
			adapter.setCategoryList(searchList);
			adapter.setCategoryType(result);
			adapter.getFileInfo().clear();
			adapter.notifyDataSetChanged();
			isSearched=false;
			mProgress.dismiss();
		}
		
	}
}
