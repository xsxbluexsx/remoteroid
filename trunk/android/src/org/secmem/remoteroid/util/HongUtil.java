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

package org.secmem.remoteroid.util;

import java.io.File;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;

import org.secmem.remoteroid.data.ExplorerType;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

public class HongUtil {
	
	public static String TYPE_PICTURE = "image";
	public static String TYPE_VIDEO = "video";
	public static String TYPE_MUSIC = "audio";
	public static String TYPE_APK = "application";
	
	
	public static NomalComparator com = new NomalComparator();
	
	public static void makeToast(Context c, String str){
		Toast.makeText(c, str, Toast.LENGTH_LONG).show();
	}
	
	public static Comparator<ExplorerType> nameComparator = new Comparator<ExplorerType>() {

		public int compare(ExplorerType lhs, ExplorerType rhs) {
			// TODO Auto-generated method stub
			return com.compare(lhs.getName(), rhs.getName());
		}
	};
	
	public static String getMimeType(File file){				// 파일의 타입(비디오,오디오,사진 등등)을 체크
		String result="";
		if(file.exists()){
			MimeTypeMap mtm = MimeTypeMap.getSingleton();
			String fileExtension = file.getName().substring(file.getName().lastIndexOf(".") + 1 , file.getName().length()).toLowerCase();
			String mimeType = mtm.getMimeTypeFromExtension(fileExtension);
			if(mimeType!=null){
				result = (mimeType.split("/", 0))[0];
				Log.i("qq","result= "+result);
			}
//			result = mimeType;
		}
		
		
		return result;
	}
	
	public static int getFileIcon(String path, String fileName){				// 파일의 타입에 대한 아이콘 추출
		int result=0;
		String type = getMimeType(new File(path+fileName));
		
		if(type.equals(TYPE_PICTURE)){
			
		}
		else if(type.equals(TYPE_VIDEO)){
			
		}
		else if(type.equals(TYPE_MUSIC)){
			
		}
		else{
			
		}
		
		return result;
	}
	
	public static Bitmap getApkBitmap(File f, Context c){
		Bitmap result = null;
		String filePath = f.getPath();
		PackageInfo packageInfo = c.getPackageManager().getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
		if(packageInfo !=null){
			
			ApplicationInfo appInfo = packageInfo.applicationInfo;
			if(Build.VERSION.SDK_INT >= 8){
				appInfo.sourceDir = filePath;
				appInfo.publicSourceDir = filePath;
			}
			Drawable icon = appInfo.loadIcon(c.getPackageManager());
//			result = (((BitmapDrawable)icon).getBitmap()).createScaledBitmap(result, 72, 72, true);
			result = result.createScaledBitmap(((BitmapDrawable)icon).getBitmap(),72,72,true);
						
			
		}
		
		return result;
	}
	
//	public static Bitmap opticalBitmap(File f){				// 비트맵 이미지 최적화
//		Bitmap result=null;
//		BitmapFactory.Options option = new BitmapFactory.Options();
//		
//		if(f.length()>200000)
//			option.inSampleSize = 7;
//		else
//			option.inSampleSize = 4;
//		
//		if(BitmapFactory.decodeFile(f.getPath(), option)==null){
//			result = 
//		}
//		else{
//			
//		}
//		
//		return result;
//	}
	
	
	
	
	
	
	
	
	
	
	
	
	//세팅시킬 Calendar 만들어줌
		public static Calendar setCal(long date){
			Calendar result=new GregorianCalendar();
			result.setTimeInMillis(date);
			
		    Date d = result.getTime();
		    String day = (d.getYear()+1900)+"년    "+(d.getMonth()+1)+"월"+d.getDate()+"일  "+d.getHours() +"시"+d.getMinutes()+" 분"+d.getSeconds()+"초";
			Log.i("music","day           "+day);
		    
		    return result;
		}
		
		public static String getTime(Calendar day) {
			String result="";
			
			Date d = day.getTime();
//			result = (d.getYear()+1900)+"년 "+(d.getMonth()+1)+"월 "+d.getDate()+"일 "+d.getHours() +":"+d.getMinutes()+":"+d.getSeconds();
			result = (d.getYear()+1900)+"-"+(d.getMonth()+1)+"-"+d.getDate();
			
			
			return result;
		}
		
		public static String setHashValue(String path){
			File f = new File(path);
			String result=String.valueOf(f.hashCode());
			
			return result;
		}
		
		public static long setFileSize(String path){
			File f = new File(path);
			return f.length();
		}
	
	
	
	
	
	public static int setMusicType(String msg){
		int result=0;
		if(msg.indexOf(".mp3")!=-1){
			
		}
		
		else if(msg.indexOf(".mid")!=-1){
			
		}
		
		else if(msg.indexOf(".mid")!=-1){
			
		}
		
		else if(msg.indexOf(".mid")!=-1){
	
		}
		
		return result;
	}
	
	public static String setPhoneNumber(String num){
		return num.replace("+82", "0");
	}
	
	
	public static String getDuration(int time){
		String result = "";
		
		int hour = 0;
		int min = time/60000;
		
		if(min>=60){
			hour = min/60;
			min = min%60;
		}
		
		String sec = String.valueOf((time%60000));
		if(sec.length()==0){
			sec = "00";
		}
		else if(sec.length()<2)
			sec = "0"+sec.substring(0,1);
		else{
			sec = sec.substring(0, 2);
		}
		
		if(hour!=0){
			result = String.valueOf(hour) + String.valueOf(min)+":"+sec;
		}
		else{
			result = String.valueOf(min)+":"+sec;
		}
		return result;
	}
	
	public static String getMegabyte(long size){
		String result ="";
		double re = (double)(((double)size/(double)1024)/(double)1024);
		Log.i("photo","size = "+re);
		
		result = String.format("%.1f", re);
		
//		Log.i("parse","final result = = "+result);
		return result;
	}
	
	public static String getMegabyte(double size){
		String result ="";
		double re = size/1024/1024;
		Log.i("photo","size = "+re);
		
		result = String.format("%.1f", re);
		
//		Log.i("parse","final result = = "+result);
		return result;
	}
	
	public static String getMegaSpeed(long size){
		String result ="";
		double re = (double)size/(double)1000;
		Log.i("photo","size = "+re);
		
		result = String.format("%.1f", re);
		
		
		return result;
	}
}
