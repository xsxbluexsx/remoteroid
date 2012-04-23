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

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.data.ExplorerType;
import org.secmem.remoteroid.util.HongUtil;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ExplorerAdapter extends BaseAdapter{
	
	Context context;
	int layout;
	DataList dataList;
	
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
		
		if (viewItem == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewItem = vi.inflate(layout, null);
        }
		ImageView img= (ImageView)viewItem.findViewById(R.id.grid_explorer_img);
		TextView tv = (TextView)viewItem.findViewById(R.id.grid_explorer_tv);
		
		final String path = dataList.getPath();
		final String fileName = dataList.getExpList().get(pos).getName();
		
		if(dataList.getExpList().get(pos).getType()==ExplorerType.TYPE_FOLDER){
			img.setBackgroundResource(R.drawable.blue_folder);
		}
		else{
//			HongUtil.getFileIcon(path, fileName);
			img.setBackgroundResource(R.drawable.ic_launcher);
		}
		
		tv.setText(dataList.getExpList().get(pos).getName());
		
		viewItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				String fileName = getItem(pos).getName();
				
				if (dataList.getExpList().get(pos).getType()== ExplorerType.TYPE_FOLDER) {
					dataList.setPath(dataList.getRealPathName(fileName));
					notifyDataSetChanged();
				} 
				else {
					if (dataList.getOnFileSelected() != null){ 
						File f = new File(dataList.getPath());				// 피시로 전송될 파일
						Log.i("qq","dataList.getPath() = "+dataList.getPath() + "       fileName = "+fileName);
						dataList.getOnFileSelected().onSelected(dataList.getPath(), fileName);
						String nn = HongUtil.getMimeType(path, fileName);
						Log.i("qq","Type = "+nn);
//						if(nn!=null){
//							Intent intent = new Intent();
//							intent.setAction(Intent.ACTION_VIEW);
//							intent.setDataAndType(Uri.fromFile(new File(path+fileName)), nn);
//							try {
//								context.startActivity(intent);
//							} catch (Exception e) {
//							}
//						}
						
					}
				}
			}
		});
		
		
		return viewItem;
	}

}
