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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.dwoodhouse.streetcrimes.R;

public class SplashScreenActivity extends Activity {

	private final String TAG = "SplashScreenActivity";
	private Context mContext;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.splash_layout);
		mContext = this;
		
		/*new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            {
            	//overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            	Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
            	//i.putExtra("dates", response);
            	startActivity(i);
            }
        }, 1500); */
	}
	
	@Override
	protected void onStart()
	{
		new GetDatesTask();
		
		super.onStart();
	}
	
	private class GetDatesTask extends AsyncTask<String, Void, String>
	{
		public GetDatesTask()
		{
			Log.i(TAG, "GetDatesTask Constructor");
			execute();
		}
		@Override
		protected String doInBackground(String... params) {
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
			Toast.makeText(mContext, "Setting up...", Toast.LENGTH_LONG).show();
		}
		
		protected void onPostExecute(final String response)
		{
			Toast.makeText(mContext, "Launching...", Toast.LENGTH_SHORT).show();
			new Handler().postDelayed(new Runnable() 
			{
	            @Override
	            public void run() 
	            {
	            	//overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	            	Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
	            	if (!response.equals("Error!"));
	            		i.putExtra("dates", response);
	            		
	            	startActivity(i);
	            }
	        }, 3000); 
		}
	}
}
