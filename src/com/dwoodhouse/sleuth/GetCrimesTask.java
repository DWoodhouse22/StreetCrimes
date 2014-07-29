package com.dwoodhouse.sleuth;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

//Cannot run http requests on main thread so use this ASyncTask to run the connection request
public class GetCrimesTask extends AsyncTask<String, Void, String> {
	private final String TAG = "GetCrimesTask";
	private List<LatLng> mPolyList;
	String requestURL;

	public GetCrimesTask(LatLng origin, List<LatLng> polyList, String date) {
		mPolyList = polyList;

		requestURL = "http://data.police.uk/api/crimes-street/all-crime?poly=";
		requestURL += getPoly(mPolyList);
		requestURL += "&date=";
		requestURL += date;

		// Start the task
		execute();
	}

	private String getPoly(List<LatLng> positions) {
		String polyArg = "";
		for (LatLng latLng : positions) {
			String polyPair = Double.toString(latLng.latitude);
			polyPair += ",";
			polyPair += Double.toString(latLng.longitude);
			if (latLng != positions.get(positions.size() - 1))
				polyPair += ":";
			polyArg += polyPair;
		}

		return polyArg;
	}

	protected String doInBackground(String... urls) {
		try {
			// TODO handle time outs!
			BufferedReader in;
			HttpClient httpclient = new DefaultHttpClient();

			// Set up the Http request
			HttpGet request = new HttpGet();
			URI website = new URI(requestURL);
			request.setURI(website);

			// Try and get a response
			HttpResponse response = httpclient.execute(request);
			// Read data received from the request
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line = in.readLine();

			return line;
		} catch (Exception e) {
			String message = (e.getMessage() == null) ? "Message is empty" : e
					.getMessage();

			Log.e(TAG, "Error in http connection " + message);

			return "Error!";
		}
	}

	// This is called once the task has completed
	protected void onPostExecute(String serverData) 
	{
		Notification n = new Notification();
		n.put("serverData", serverData);
		ObservingService.getInstance().postNotification(Notification.ADD_MAP_MARKERS, n);
	}

	protected void onPreExecute()
	{
		//TODO show user the task is starting
	}
}