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

package org.secmem.remoteroid.adapter;

import java.io.File;
import java.util.ArrayList;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.activity.ExplorerActivity;
import org.secmem.remoteroid.data.ExplorerType;
import org.secmem.remoteroid.data.FileList;
import org.secmem.remoteroid.util.HongUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ExplorerAdapter extends BaseAdapter{
	
	private Context context;
	private int layout;
	private DataList dataList;
	private static int threadCount=0;
	
	private ArrayList<File> fileInfo = new ArrayList<File>();
	
	
	public ExplorerAdapter(Context context, int layout, DataList dataList) {
		this.context = context;
		this.layout = layout;
		this.dataList = dataList;
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dataList.getExpList().size();
	}

	@Override
	public ExplorerType getItem(int arg0) {
		// TODO Auto-generated method stub
		return dataList.getExpList().get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View viewItem=convertView;
		final int pos = position;
		
		final ImageViewHolder holder;
		
		if (viewItem == null) {
			holder = new ImageViewHolder();
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewItem = vi.inflate(layout, null);
            holder.imgHolder = (ImageView)viewItem.findViewById(R.id.grid_explorer_img);
            holder.titleHolder = (TextView)viewItem.findViewById(R.id.grid_explorer_tv);
            viewItem.setTag(holder);
            
        }
		else{
			holder = (ImageViewHolder)viewItem.getTag();
		}
		
		final String path = dataList.getPath();
		final String fileName = dataList.getExpList().get(pos).getName();
		
		holder.titleHolder.setTextColor(Color.WHITE);
		if(dataList.getExpList().get(pos).getType()==ExplorerType.TYPE_FOLDER){					// 폴더일 때
			holder.imgHolder.setImageBitmap(null);
			holder.imgHolder.setBackgroundResource(R.drawable.blue_folder);
		}
		else{																													// 파일 일 때
//			HongUtil.getFileIcon(path, fileName);
			FileList f = (FileList)dataList.getExpList().get(pos);
			holder.imgHolder.setImageBitmap(f.getBitmap());
			File file = new File(path+fileName);
			
			String type = HongUtil.getMimeType(file);
			if(type.equals(HongUtil.TYPE_PICTURE)){						// 타입이 사진이면 사진 썸네일 추출
				if(!(ExplorerActivity.SCROLL_STATE)){
					
					if(f.isBitmapChecked()){
						holder.imgHolder.setBackgroundResource(0x00000000);
						holder.imgHolder.setImageBitmap(f.getBitmap());
					}
					else{
						holder.imgHolder.setBackgroundResource(R.drawable.rocket_photo_blank);
//						holder.imgHolder.setBackgroundResource(0x0000);
						if(threadCount<15){
							f.setBitmapChecked(true);
							threadCount++;
							new ThumbAsync().execute(path,fileName, String.valueOf(pos));
						}
					}
				}
				else{
					holder.imgHolder.setBackgroundResource(R.drawable.rocket_photo_blank);
				}
				
			}
			else if(file.getPath().endsWith(".apk")){															// 타입이 .APK 일 때 APK 썸네일 추출
				
				if(!(ExplorerActivity.SCROLL_STATE)){
					
					if(f.isBitmapChecked()){
						holder.imgHolder.setBackgroundResource(0x00000000);
						holder.imgHolder.setImageBitmap(f.getBitmap());
					}
					else{
						holder.imgHolder.setBackgroundResource(R.drawable.androidapk);
						f.setBitmapChecked(true);
						new ApkBitmapAsync().execute(path,fileName, String.valueOf(pos));
					}
				}
				else{
					holder.imgHolder.setBackgroundResource(R.drawable.androidapk);
				}
				
			}
			
			else{
				holder.imgHolder.setBackgroundResource(R.drawable.ic_launcher);
			}
			
			if(f.isFileSelected()){
				holder.titleHolder.setTextColor(Color.GREEN);
			}
		}
		holder.titleHolder.setText(dataList.getExpList().get(pos).getName());
		
		
		viewItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				String fileName = getItem(pos).getName();
				
				if (dataList.getExpList().get(pos).getType()== ExplorerType.TYPE_FOLDER) {
					dataList.setPath(dataList.getRealPathName(fileName));
					fileInfo.clear();
					notifyDataSetChanged();
				} 
				else {
					if (dataList.getOnFileSelected() != null){ 
						FileList fl = (FileList)dataList.getExpList().get(pos);
						File f = new File(dataList.getPath()+fileName);
						Log.i("qq","getPath = " + f.getPath() + "          getAb = "+f.getAbsolutePath());
						if(fl.isFileSelected()){
							fl.setFileSelected(false);
							fileInfo.remove(getFilePos(f));
							holder.titleHolder.setTextColor(Color.WHITE);
						}
						
						else{
							fl.setFileSelected(true);
							fileInfo.add(f);
							holder.titleHolder.setTextColor(Color.GREEN);
						}
					}
				}
				
				printFileInfo();
			}
			
		});
		return viewItem;
	}
	
	private void printFileInfo() {
		for (int i = 0; i < fileInfo.size(); i++) {
			Log.i("fileinfo",i + "= "+fileInfo.get(i).getAbsolutePath());
		}
	}
	
	private class ThumbAsync extends AsyncTask<String, Void, Integer>{

		@Override
		protected Integer doInBackground(String... params) {
			
			int result=setBitmap(params[0],params[1],params[2]);
			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if(result==1){
				notifyDataSetChanged();
			}
			threadCount--;
		}
		
	}
	
	private int setBitmap(String path, String file, String position) {
		// TODO Auto-generated method stub
//		String path = FileListManager.FilePhoto_List.get(position).getPath();
		
		int result=1;
		
		BitmapFactory.Options option = new BitmapFactory.Options();
		int pos = Integer.parseInt(position);
		if (new File(path+file).length() > 200000)
			option.inSampleSize = 7;
		else
			option.inSampleSize = 4;
		
		Log.i("path","bitmap Path = "+path + "        real Path = "+dataList.get_Path());
		if(path.equals(dataList.get_Path())){
			if(BitmapFactory.decodeFile(path+file, option)==null){
				Bitmap bitmap = BitmapFactory.decodeFile(path+file, option);
				if(path.equals(dataList.get_Path())){
					((FileList)dataList.getExpList().get(pos)).setBitmap(bitmap);
				}
				else{
					result=0;
				}
			}
			else{
				Bitmap tmp = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(path+file, option), 72, 72, true);
				if(path.equals(dataList.get_Path())){
					((FileList)dataList.getExpList().get(pos)).setBitmap(tmp);
				}
				else{
					result=0;
				}
			}
		}
		else{
			result=0;
		}
		
		return result;
	}
	
	private class ApkBitmapAsync extends AsyncTask<String, Void, Integer>{

		@Override
		protected Integer doInBackground(String... params) {
			
			int result=1;
			
			Bitmap b = HongUtil.getApkBitmap(new File(params[0]+params[1]),context);
			if(params[0].equals(dataList.get_Path())){
				((FileList)dataList.getExpList().get(Integer.parseInt(params[2]))).setBitmap(b);
			}
			else{
				result = 0;							// 경로가 바뀌었을 경우 체크
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result==1)
				notifyDataSetChanged();
		}
	}
	
	public ArrayList<File> getFileInfo() {
		return fileInfo;
	}

	public void setFileInfo(ArrayList<File> fileInfo) {
		this.fileInfo = fileInfo;
	}
	
	private int getFilePos(File f){
		int result=0;
		for(int i = 0 ; i < fileInfo.size() ; i++){
			if(fileInfo.get(i).getAbsolutePath().equals(f.getAbsolutePath())){
				return i;
			}
		}
		return result;
	}
	
	
	static class ImageViewHolder{
		ImageView imgHolder;
		TextView titleHolder;
	}

}
