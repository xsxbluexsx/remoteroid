package org.secmem.remoteroid.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Pref {
	public static final String MYPREFS = "MyPreference";
	
	public static void setMyPreferences(String key, String value, Context c) {
		int mode = Activity.MODE_PRIVATE;
		SharedPreferences mySharedPreferences = c.getSharedPreferences(MYPREFS,mode);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		
		editor.putString(key, value);
		editor.commit();
	}

	public static String getMyPreferences(String key, Context c) {
		String result="";
		int mode = Activity.MODE_PRIVATE;
		SharedPreferences mySharedPreferences = c.getSharedPreferences(MYPREFS,mode);
		result=mySharedPreferences.getString(key,null);
		return result;
	}
}
	