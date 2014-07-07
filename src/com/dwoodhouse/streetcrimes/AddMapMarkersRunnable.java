package com.dwoodhouse.streetcrimes;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

public class AddMapMarkersRunnable implements Runnable {

	private String mData;
	private StreetCrimeData[] crimesData;
	public AddMapMarkersRunnable(String data)
	{
		mData = data;
	}
	@Override
	public void run() 
	{
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
		try 
		{
			crimesData = new Gson().fromJson(mData, StreetCrimeData[].class);
			
			for (int i = 0; i < crimesData.length; i++)
			{
				if (crimesData[i].getCategory().equals("anti-social-behaviour"))
				{
					LatLng loc = new LatLng(crimesData[i].getLocation().latitude, crimesData[i].getLocation().longitude);
					
					String snippet = "Location: ";
					snippet += Double.toString(loc.latitude);
					snippet += ", ";
					snippet += Double.toString(loc.longitude);
					Notification n = new Notification();
					n.put("snippet", snippet);
					n.put("title", crimesData[i].getLocation().street.name);
					n.put("location", loc);
					ObservingService.getInstance().postNotification(Notification.ADD_MAP_MARKER, n);
				}
				else
				{
					// Crimes listed alphabetically by category, no point continuing after final anti-social-behaviour
					break;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
