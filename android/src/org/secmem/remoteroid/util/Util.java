package org.secmem.remoteroid.util;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;

public class Util {
	/**
	 * Class name for Remoteroid NotificationReceiverService.
	 * @see org.secmem.remoteroid.service.NotificationReceiverService NotificationReceiverService
	 */
	private static final String ACC_SERVICE_NAME = "org.secmem.remoteroid/org.secmem.remoteroid.service.NotificationReceiverService";
	
	/**
	 * Determine whether Notification receiver service enabled or not.
	 * @param context Application or Activity's context
	 * @return true if Accessibility service has enabled, false otherwise
	 */
	public static boolean isAccessibilityServiceEnabled(Context context){
		try {
			boolean globalAccServiceEnabled = Secure.getInt(context.getContentResolver(), Secure.ACCESSIBILITY_ENABLED)==1?true:false;
			if(!globalAccServiceEnabled)
				return false;
			
			String enabledAccServices = Secure.getString(context.getContentResolver(), Secure.ENABLED_ACCESSIBILITY_SERVICES);
			return enabledAccServices.contains(ACC_SERVICE_NAME);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Launch Accessibility settings activity.
	 * @param context Application or Activity's context
	 */
	public static void launchAccessibilitySettings(Context context){
		context.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
	}
	
}
