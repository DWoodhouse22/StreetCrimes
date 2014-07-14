package com.dwoodhouse.sleuth;

import java.util.HashMap;

public class Notification extends HashMap<String, Object>
{
	private static final long serialVersionUID = 1L;
	public static final String ADD_MAP_MARKERS = "addMapMarkers";
	public static final String SLEUTH_BUTTON_PRESSED = "sleuthButtonPressed";
	
	public static boolean isNotificationType(Object data, String notificationID)
	{
		return ((Notification)data).isNotificationType(notificationID);
	}
	
	public boolean isNotificationType(String notificationID)
	{
		return ((String)get("notificationID")).equals(notificationID);
	}
};
