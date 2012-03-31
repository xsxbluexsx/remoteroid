package org.secmem.remoteroid.util;

import java.util.List;

import org.secmem.remoteroid.service.RemoteroidService;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

public class Util {
	private static final boolean D = true;
	private static final String TAG = "RemoteroidUtil";
	
	/**
	 * Class name for Remoteroid NotificationReceiverService.
	 * @see org.secmem.remoteroid.service.NotificationReceiverService NotificationReceiverService
	 */
	private static final String ACC_SERVICE_NAME = "org.secmem.remoteroid/org.secmem.remoteroid.service.NotificationReceiverService";
	private static final ComponentName DEVICE_ADMIN_NAME = ComponentName.unflattenFromString("org.secmem.remoteroid/org.secmem.remoteroid.receiver.RemoteroidDeviceAdminReceiver");
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
	
	public static boolean isDeviceAdminEnabled(Context context){
		DevicePolicyManager mDpm = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		return mDpm.isAdminActive(DEVICE_ADMIN_NAME);
		/*
			context.startActivity(new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
				.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "This permision will be used to lock your phone from your computer.")
				.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, DEVICE_ADMIN_NAME));
		}*/
	}
	
	/**
	 * Launch Accessibility settings activity.
	 * @param context Application or Activity's context
	 */
	public static void launchAccessibilitySettings(Context context){
		context.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
	}
	
	public static void launchDeviceAdminAccessRequest(Context context){
		context.startActivity(new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
		.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "This permision will be used to lock your phone from your computer.")
		.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, DEVICE_ADMIN_NAME));
	}
	
	/**
	 * Check RemoteroidService is running or not.
	 * @param context Application/Activity's context
	 * @return <code>true</code> if RemoteroidService is running, <code>false</code> otherwise.
	 */
	public static boolean isServiceAlive(Context context){
		String serviceCls = RemoteroidService.class.getName();
		ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> serviceList = manager.getRunningServices(Integer.MAX_VALUE);
		int count = serviceList.size();
		for(int i=0; i<count; i++){
			RunningServiceInfo obj = serviceList.get(i);
			if(obj.service.getClassName().equals(serviceCls)){
				return true;
			}
		}
		if(D) Log.d(TAG, "RemoteroidService not available.");
		return false;
	}
	
	/**
	 * Starts RemoteroidService.
	 * @param context Application/Activity's context
	 * @see org.secmem.remoteroid.service.RemoteroidService RemoteroidService
	 */
	public static void startRemoteroidService(Context context){
		if(!isServiceAlive(context)){
			if(D) Log.d(TAG, "Starting RemoteroidService..");
			Intent intent = new Intent(context, RemoteroidService.class);
			context.startService(intent);
		}
	}
}
