package com.dwoodhouse.sleuth;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class GetDatesTask extends AsyncTask<Void, Void, String>
{
	private final String TAG = "GetDatesTask";
	@Override
	protected String doInBackground(Void... params) {
		try
		{
			HttpParams httpParams = new BasicHttpParams();
		    HttpConnectionParams.setConnectionTimeout(httpParams, 5 * 1000); // 5 second time out
			String requestURL = "http://data.police.uk/api/crimes-street-dates";
			HttpClient httpclient = new DefaultHttpClient(httpParams);

			// Set up the Http request
			HttpGet request = new HttpGet();
			URI website = new URI(requestURL);
			request.setURI(website);

			// Try and get a response
			HttpResponse response = httpclient.execute(request);
			// Read data received from the request
			BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line = in.readLine();

			return line;
		}
		catch (Exception e)
		{
			String message = (e.getMessage() == null) ? "Message is empty" : e.getMessage();

			Log.e(TAG, "Error " + message);

			return "Error!";
		}
	}
	
	protected void onPreExecute()
	{
		
	}
	
	protected void onPostExecute(final String response)
	{
		//Toast.makeText(mContext, "Launching...", Toast.LENGTH_SHORT).show();
		Notification n = new Notification();
		n.put("response", response);
		ObservingService.getInstance().postNotification(Notification.RETRIEVED_DATES, n);
		
	}
}
