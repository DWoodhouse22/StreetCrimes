package com.dwoodhouse.sleuth;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class Application 
{	
	public static boolean checkConnection(Activity activity) 
	{
 	    ConnectivityManager conMgr =  (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

 	   	NetworkInfo[] networkInfos = conMgr.getAllNetworkInfo();
 	   	
 	   	boolean connected = false;
 	   	
 	   	for (NetworkInfo networkInfo: networkInfos)
 	   	{
 	   		connected |= (networkInfo.getState() == NetworkInfo.State.CONNECTED);
 	   	}

	    return connected;
	}
}
