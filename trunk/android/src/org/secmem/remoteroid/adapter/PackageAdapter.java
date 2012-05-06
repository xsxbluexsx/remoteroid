package org.secmem.remoteroid.adapter;

import java.util.ArrayList;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.util.FilterUtil;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PackageAdapter extends BaseAdapter{
	private Context mContext;
	private ArrayList<PackageInfo> mPackageList = new ArrayList<PackageInfo>();
	private PackageManager mPkgManager;
	private FilterUtil mFilterUtil;
	
	public PackageAdapter(Context context){
		mContext = context;
		mPkgManager = mContext.getPackageManager();
		mPackageList = (ArrayList<PackageInfo>)mPkgManager.getInstalledPackages(0);
		mFilterUtil = new FilterUtil(mContext);
		mFilterUtil.open();
	}
	
	@Override
	protected void finalize() throws Throwable {
		mFilterUtil.close();
		super.finalize();
	}

	@Override
	public int getCount() {
		return mPackageList.size();
	}

	@Override
	public PackageInfo getItem(int position) {
		return mPackageList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		ImageView icon;
		TextView name;
		TextView pname;
		CheckBox checked;
		
		if(convertView==null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.row_package_selector, null);
		}
		
		icon = (ImageView)convertView.findViewById(R.id.row_package_selector_icon);
		name = (TextView)convertView.findViewById(R.id.row_package_selector_title);
		pname = (TextView)convertView.findViewById(R.id.row_package_selector_pname);
		checked = (CheckBox)convertView.findViewById(R.id.row_package_selector_checkmark);
		
		final PackageInfo item = getItem(position);
		
		icon.setImageDrawable(item.applicationInfo.loadIcon(mPkgManager));
		name.setText(item.applicationInfo.loadLabel(mPkgManager));
		pname.setText(item.packageName);
		
		checked.setChecked(mFilterUtil.exists(item.packageName));
		checked.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				ListView lv = (ListView)parent;
				lv.getOnItemClickListener().onItemClick(null, null, position, 0);
			}
			
		});
		
		return convertView;
	}

}
