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
import android.util.Log;
import android.widget.Toast;

public class HongUtil {
	
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
	
	
	
	
	
	
	
	
	
	
	
	
	//�명���� Calendar 留���댁�
	public static Calendar setCal(long date){
		Calendar result=new GregorianCalendar();
		result.setTimeInMillis(date);
		
	    Date d = result.getTime();
	    /*String day = (d.getYear()+1900)+"��   "+(d.getMonth()+1)+"��+d.getDate()+"�� "+d.getHours() +"��+d.getMinutes()+" 遺�+d.getSeconds()+"珥�;
		Log.i("music","day           "+day);*/
	    
	    return result;
	}
	
	public static String getTime(Calendar day) {
		String result="";
		
		Date d = day.getTime();
//		result = (d.getYear()+1900)+"��"+(d.getMonth()+1)+"��"+d.getDate()+"��"+d.getHours() +":"+d.getMinutes()+":"+d.getSeconds();
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
