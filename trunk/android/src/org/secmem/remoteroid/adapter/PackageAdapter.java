package org.secmem.remoteroid.adapter;

import java.util.ArrayList;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.util.FilterUtil;
import org.secmem.remoteroid.util.PackageSoundSearcher;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
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
	
	private ArrayList<PackageInfo> sPackageList = new ArrayList<PackageInfo>();
	
	private PackageManager mPkgManager;
	private FilterUtil mFilterUtil;
	
	
	private String strInitial="";
	private String afterStr="";

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
		if(strInitial.equals("") || strInitial==null){
			return mPackageList.size();
		}
		else{
			if(strInitial.equals(afterStr)){
				return sPackageList.size();
			}
			else{
				return searchSoundInitial(this.strInitial);
			}
		}
	}

	@Override
	public PackageInfo getItem(int position) {
		if(strInitial.equals("") || strInitial==null){
			return mPackageList.get(position);
		}
		else{
			return sPackageList.get(position);
		}
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
	public String getStrInitial() {
		return strInitial;
	}
	public void setStrInitial(String strInitial) {
		this.strInitial = strInitial;
	}
	public String getAfterStr() {
		return afterStr;
	}
	public void setAfterStr(String afterStr) {
		this.afterStr = afterStr;
	}
	
	public int searchSoundInitial(String msg){
		int count = 0 ;
		setAfterStr(msg);
		sPackageList.clear();
		for(int i = 0 ; i < mPackageList.size() ; i++){
			
			if(PackageSoundSearcher.matchString((mPackageList.get(i).applicationInfo.loadLabel(mPkgManager)).toString(), msg)){
				sPackageList.add(mPackageList.get(i));
				count++;
			}
			else{
			}
		}
		return count;
	}

}
