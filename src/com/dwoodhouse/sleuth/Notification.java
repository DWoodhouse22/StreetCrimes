package com.dwoodhouse.sleuth;

import java.util.HashMap;

public class Notification extends HashMap<String, Object>
{
	private static final long serialVersionUID = 1L;

	public static final String SLEUTH_BUTTON_PRESSED = "sleuthButtonPressed";
	public static final String RETRIEVED_DATES = "retrievedDates";
	public static final String RETRIEVED_CRIME_CATEGORIES = "retrievedCrimeCategories";
	public static final String RETRIEVED_CRIMES = "retrievedCrimes";
	public static final String MAP_MARKERS_ADDED = "mapMarkersAdded";
	public static final String SLEUTH_ERROR = "sleuthError";
	
	public static boolean isNotificationType(Object data, String notificationID)
	{
		return ((Notification)data).isNotificationType(notificationID);
	}
	
	public boolean isNotificationType(String notificationID)
	{
		return ((String)get("notificationID")).equals(notificationID);
	}
};
