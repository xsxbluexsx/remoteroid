package org.secmem.remoteroid.data;

import java.io.File;
import java.util.ArrayList;

import org.secmem.remoteroid.activity.ExplorerActivity;

public class CommunicateInfo {
	
	public static ArrayList<File> getSelectFileList(){
		ArrayList <File> result = new ArrayList<File>();
		
		if(ExplorerActivity.fileInfo==null || ExplorerActivity.fileInfo.size()==0){
			return null;
		}
		result.addAll(ExplorerActivity.fileInfo);
		
		return result;
	}
	
	public static String getCurrentPath(){
		String result="";
		
		if(ExplorerActivity.dataList==null || ExplorerActivity.dataList.getPath()==null || ExplorerActivity.dataList.getPath().equals("") ){
			return null;
		}
		
		result = ExplorerActivity.dataList.getPath();
		
		return result;
	}

}
