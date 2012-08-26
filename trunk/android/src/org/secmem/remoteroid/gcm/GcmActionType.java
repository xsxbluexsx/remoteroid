package org.secmem.remoteroid.gcm;

public class GcmActionType {
	
	public class RegistrationToServer{
		public final static String RESISTER = "com.google.android.c2dm.intent.REGISTER";
		public final static String UNRESISTER = "com.google.android.c2dm.intent.UNREGISTER";
	}
	
	public class ReceiveFromServer{
		public final static String GCM_REGISTRATION= "com.google.android.c2dm.intent.REGISTRATION";
		public final static String GCM_RECEIVE= "com.google.android.c2dm.intent.RECEIVE";
	}
	
	public class ActionMessage{
		public final static String ACTION_MESSAGE_IP= "ip";
		public final static String ACTION_MESSAGE_PASSWORD= "password";
	}

}
