package org.secmem.remoteroid.activity;

import org.secmem.remoteroid.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class NotificationReceiverSettings extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    this.addPreferencesFromResource(R.xml.notification_receiver_preferences);
	}

}
