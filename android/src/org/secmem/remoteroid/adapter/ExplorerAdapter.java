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

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.data.ExplorerType;

import android.content.Context;
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
		
		if(dataList.getExpList().get(pos).getType()==ExplorerType.TYPE_FOLDER){
			img.setBackgroundResource(R.drawable.blue_folder);
		}
		else{
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
					if (dataList.getOnFileSelected() != null) dataList.getOnFileSelected().onSelected(dataList.getPath(), fileName);
				}
			}
		});
		
		
		return viewItem;
	}

}
