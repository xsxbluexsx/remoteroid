package org.secmem.remoteroid.service;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.activity.RemoteConnectRedirector;
import org.secmem.remoteroid.intent.RemoteroidIntent;
import org.secmem.remoteroid.lib.api.API;
import org.secmem.remoteroid.lib.data.Account;
import org.secmem.remoteroid.lib.data.Device;
import org.secmem.remoteroid.lib.data.WakeupMessage;
import org.secmem.remoteroid.lib.request.Request;
import org.secmem.remoteroid.lib.request.Response;
import org.secmem.remoteroid.util.DeviceUUIDGeneratorImpl;
import org.secmem.remoteroid.util.Util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	@Override
	protected void onError(Context context, String errorId) {
		
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		// A push message has arrived.
		Intent connIntent = new Intent(context, RemoteConnectRedirector.class);
		connIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		connIntent.putExtra(RemoteroidIntent.EXTRA_IP_ADDESS, intent.getStringExtra(WakeupMessage.IP_ADDRESS));
		startActivity(connIntent);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		// Registration completed.
		// Now, we should send registration ID to server.
		Account account = Util.Connection.getUserAccount(context);
		final Device device = new Device();
		device.setOwnerAccount(account);
		device.setNickname(Util.Connection.getDeviceNickname(context));
		device.setDeviceUUID(new DeviceUUIDGeneratorImpl(context));
		device.setRegistrationKey(registrationId);
		
		new AsyncTask<Void, Void, Response>(){
			
			@SuppressWarnings("deprecation")
			@Override
			protected void onPreExecute(){
				super.onPreExecute();
				Notification notification = new Notification();
				PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent()/* Null intent*/, 0);
				notification.icon = R.drawable.ic_launcher;
				notification.tickerText = "Registering device...";
				notification.when = System.currentTimeMillis();
				notification.setLatestEventInfo(getApplicationContext(), getString(R.string.app_name), "Registering device...", intent);
				
				NotificationManager notifManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
				notifManager.notify(0, notification);
			}

			@Override
			protected Response doInBackground(Void... params) {
				Request request = Request.Builder.setRequest(API.Device.ADD_DEVICE).setPayload(device).build();
				return request.sendRequest();
			}

			@Override
			protected void onPostExecute(Response result) {
				super.onPostExecute(result);
				NotificationManager notifManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
				notifManager.cancelAll();
				
				if(result.isSucceed()){
					Toast.makeText(getApplicationContext(), "Device registered.", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(getApplicationContext(), "Cannot register device.", Toast.LENGTH_SHORT).show();
				}
			}
			
		}.execute();
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		// Device now unregistered.
		// We should delete this device from server.
	}

}
