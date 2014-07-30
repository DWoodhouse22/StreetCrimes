package com.dwoodhouse.sleuth;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class GetCategoriesTask extends AsyncTask<String, Void, String> 
{
	private final String TAG = "GetCategoriesTask";
	@Override
	protected String doInBackground(String... date) {
		try
		{
			String requestURL = "http://data.police.uk/api/crime-categories?date=";
			requestURL += date;
			HttpClient httpClient = new DefaultHttpClient();
			
			HttpGet request = new HttpGet();
			URI website = new URI(requestURL);
			request.setURI(website);
			
			HttpResponse response = httpClient.execute(request);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line = in.readLine();
			
			return line;
		}
		catch (Exception e)
		{
			String message = (e.getMessage() == null) ? "Message is empty" : e.getMessage();

			Log.e(TAG, "Error " + message);
			
			return null;
		}	
	}
	
	protected void onPreExecute()
	{
		
	}
	
	protected void onPostExecute(final String response)
	{
		Notification n = new Notification();
		n.put("response", response);
		ObservingService.getInstance().postNotification(Notification.RETRIEVED_CRIME_CATEGORIES, n);
	}

}
