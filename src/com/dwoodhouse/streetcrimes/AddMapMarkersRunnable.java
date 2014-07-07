package com.dwoodhouse.streetcrimes;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

public class AddMapMarkersRunnable implements Runnable {

	private String mData;
	
	public AddMapMarkersRunnable(String data)
	{
		mData = data;
	}
	@Override
	public void run() 
	{
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
		
	}

}
