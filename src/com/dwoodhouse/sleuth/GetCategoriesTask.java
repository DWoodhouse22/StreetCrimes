package com.dwoodhouse.sleuth;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class GetCategoriesTask extends AsyncTask<Void, Void, String> 
{
	private final String TAG = "GetCategoriesTask";
	private String mDate;
	private Context mContext;
	public GetCategoriesTask(String date, Context context)
	{
		mDate = date;
		mContext = context;
	}
	@Override
	protected String doInBackground(Void... v) {
		try
		{
			String requestURL = "http://data.police.uk/api/crime-categories?date=";
			requestURL += mDate;
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
		
		if (response == null)
		{
			Toast.makeText(mContext, "Something went wrong, try again", Toast.LENGTH_LONG).show();
			ObservingService.getInstance().postNotification(Notification.SLEUTH_ERROR);
		}
		else
		{
			ObservingService.getInstance().postNotification(Notification.RETRIEVED_CRIME_CATEGORIES, n);
		}
	}

}
