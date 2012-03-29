package org.secmem.remoteroid.intent;

/**
 * A class represents action, category, extras related to Intent.
 * @author Taeho Kim
 *
 */
public final class RemoteroidIntent {
	/**
	 * An action used for PendingIntent to notify user about SMS has been sent successful or not.<div/>
	 * <strong>Extras:</strong><br/>
	 * {@link RemoteroidIntent#EXTRA_PHONE_NUMBER}
	 */
	public static final String ACTION_SMS_SENT = "org.secmem.remoteroid.intent.action.SMS_SENT";
	
	/**
	 * Key for extra data contains phone number.
	 * @see RemoteroidIntent#ACTION_SMS_SENT
	 */
	public static final String EXTRA_PHONE_NUMBER = "org.secmem.remoteroid.intent.extra.PHONE_NUMBER";
}
