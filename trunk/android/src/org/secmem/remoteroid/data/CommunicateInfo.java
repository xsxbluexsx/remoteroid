package org.secmem.remoteroid.data;

import java.io.File;
import java.util.ArrayList;

import org.secmem.remoteroid.activity.ExplorerActivity;

import android.os.Environment;
import android.util.Log;

public class CommunicateInfo{
	
	
	public static String getCurrentPath(){
		String result="";
		
		if(ExplorerActivity.dataList==null || ExplorerActivity.dataList.getPath()==null || ExplorerActivity.dataList.getPath().equals("") || ExplorerActivity.adapter.getType()== ExplorerActivity.ADAPTER_TYPE_CATEGORY){
			Log.i("asd","null path = "+Environment.getExternalStorageDirectory().getAbsolutePath()+"/Remoteroid/");
			return Environment.getExternalStorageDirectory().getAbsolutePath()+"/Remoteroid/";
		}
		
		
		result = ExplorerActivity.dataList.getPath();
		Log.i("asd","result = "+result);
		return result;
	}


}
