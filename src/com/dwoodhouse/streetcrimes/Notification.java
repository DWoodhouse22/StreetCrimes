package com.dwoodhouse.streetcrimes;

import java.util.HashMap;

public class Notification extends HashMap<String, Object>
{
	public static final String ADD_MAP_MARKER = "addMapMarker";
	
	
	
	private static final long serialVersionUID = 1L;

	public static boolean isNotificationType(Object data, String notificationID)
	{
		return ((Notification)data).isNotificationType(notificationID);
	}
	
	public boolean isNotificationType(String notificationID)
	{
		return ((String)get("notificationID")).equals(notificationID);
	}
};
