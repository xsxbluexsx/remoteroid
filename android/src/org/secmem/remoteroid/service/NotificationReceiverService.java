package org.secmem.remoteroid.service;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class NotificationReceiverService extends AccessibilityService {
	private static final String TAG = "NotificationReceiverService";
	private static final boolean D = true;
	
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		if(D) Log.d(TAG, event.toString());
	}

	@Override
	public void onInterrupt() {
	}

}
