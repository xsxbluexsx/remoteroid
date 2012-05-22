package org.secmem.remoteroid.data;

import java.io.File;
import java.util.ArrayList;

import org.secmem.remoteroid.activity.ExplorerActivity;

import android.os.*;
import android.util.Log;

public class CommunicateInfo {
	
	public static ArrayList<File> getSelectFileList(){
		ArrayList <File> result = new ArrayList<File>();
		
		if(ExplorerActivity.fileInfo==null || ExplorerActivity.fileInfo.size()==0){
			return null;
		}
		for(int i = 0 ; i<ExplorerActivity.fileInfo.size() ; i++)
		{	
			result.add(ExplorerActivity.fileInfo.get(i).getAbsoluteFile());
		}
		
		return result;
	}
	
	public static String getCurrentPath(){
		String result="";
		
		if(ExplorerActivity.dataList==null || ExplorerActivity.dataList.getPath()==null || ExplorerActivity.dataList.getPath().equals("") ){
			Log.i("asd","null path = "+Environment.getExternalStorageDirectory().getAbsolutePath()+"/Remoteroid/");
			return Environment.getExternalStorageDirectory().getAbsolutePath()+"/Remoteroid/";
		}
		
		result = ExplorerActivity.dataList.getPath();
		Log.i("asd","result = "+result);
		return result;
	}

}
