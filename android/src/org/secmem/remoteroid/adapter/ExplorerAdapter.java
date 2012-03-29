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
				Log.i("qq","fileName = "+fileName);
				if (dataList.getExpList().get(pos).getType()== ExplorerType.TYPE_FOLDER) {
					Log.i("qq","folder");
					dataList.setPath(dataList.getRealPathName(fileName));
					notifyDataSetChanged();
				} else {
					Log.i("qq","file");
					if (dataList.getOnFileSelected() != null) dataList.getOnFileSelected().onSelected(dataList.getPath(), fileName);
				}
			}
		});
		
		
		return viewItem;
	}

}
