package com.dwoodhouse.streetcrimes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

public class MainActivity extends Activity implements OnMarkerClickListener,  OnMyLocationButtonClickListener, ConnectionCallbacks,
OnConnectionFailedListener, LocationListener {
	private final String TAG = "MainActivity";
	private final double LAT = 51.5046742;
	private final double LNG = -0.0860056;
	private List<Marker> mMapMarkers;
	private StreetCrimeData[] crimesData;
	private GoogleMap mMap;
	
	private LocationClient mLocationClient;
	
	private Activity getActivity() { return this; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// Initialisation
		setContentView(R.layout.main_layout);
		mMapMarkers = new ArrayList<Marker>();
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		mLocationClient = new LocationClient(this, this, this);
		
		// Disable user inputs
		mMap.getUiSettings().setAllGesturesEnabled(true);
		mMap.getUiSettings().setZoomControlsEnabled(false);
		mMap.setMyLocationEnabled(true);
		
		mMap.setOnMyLocationButtonClickListener((OnMyLocationButtonClickListener)this);
		
        // position the camera over london bridge
		LatLng londonBridge = new LatLng(LAT, LNG);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(londonBridge, 13));  
        
        // Send off a request to retrieve the crime data using the default location initially
     	getCrimeData(new LatLng(LAT, LNG));

        // Set a click listener for the markers to display the position overlay
        mMap.setOnMarkerClickListener((OnMarkerClickListener)this);
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		mLocationClient.connect();
	}
	
	@Override
	protected void onStop()
	{
		mLocationClient.disconnect();
		super.onStop();
	}
           
	private void getCrimeData(LatLng latLng)
	{
		new GetCrimesTask(latLng).execute();
	}
	
	// This is called once the HTTP request has come back
	private void addMapMarkers(String data)
	{		
		// If for some reason Gson cannot understand the JSON array format, we need to catch the exception
		try 
		{
			crimesData = new Gson().fromJson(data, StreetCrimeData[].class);
			
			for (int i = 0; i < crimesData.length; i++)
			{
				if (crimesData[i].getCategory().equals("anti-social-behaviour"))
				{
					LatLng loc = new LatLng(crimesData[i].getLocation().latitude, crimesData[i].getLocation().longitude);
					
					String snippet = "Location: ";
					snippet += Double.toString(loc.latitude);
					snippet += ", ";
					snippet += Double.toString(loc.longitude);
					Marker marker = mMap.addMarker(new MarkerOptions().position(loc).snippet(snippet).title(crimesData[i].getLocation().street.name));
					mMapMarkers.add(marker);
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
	
	// Cannot run http requests on main thread so use this ASyncTask to run the connection request
	private class GetCrimesTask extends AsyncTask<String, Void, String> {

		private LatLng mLatLng;
		public GetCrimesTask(LatLng latLng)
		{
			mLatLng = latLng;
		}
	    protected String doInBackground(String... urls) 
	    {
	    	try
			{
				BufferedReader in;
				
				// Hard-coded URL
				String requestURL = "http://data.police.uk/api/crimes-street/all-crime?lat=" + Double.toString(LAT) + "&lng=" + Double.toString(LNG) + "&date=2014-04";
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
		    }
			catch(Exception e)
			{
				String message = (e.getMessage() == null) ? "Message is empty" : e.getMessage();
				
				Log.e(TAG, "Error in http connection " + message);
				
				return "Error!";
				
		    }
	    }
	    
	    //This is called once the task has completed
	    protected void onPostExecute(String serverData) 
	    {
	    	// Fire the data off to be parsed by Gson
	        addMapMarkers(serverData);
	    }
	    
	    protected void onPreExecute()
	    {
	    	// Some basic user feedback so they know something's happening
	    	Toast.makeText(getActivity(), "Fetching data...", Toast.LENGTH_LONG).show();
	    }
	}
	
	@Override
	public void onBackPressed()
	{
		// Override onBackPressed so if an info window is showing, close it, not the app.
		for (Marker m : mMapMarkers)
		{
			if (m.isInfoWindowShown())
			{
				m.hideInfoWindow();
				return;
			}
		}
		
		// if no info window open, close the app.
		super.onBackPressed();
	}

	/*  BUTTON AND EVENT LISTENERS */
	
	@Override
    public boolean onMarkerClick(Marker marker) 
	{       
		marker.showInfoWindow();
		return true;
    }
	
	@Override
	public boolean onMyLocationButtonClick() {
		Log.i(TAG, "onMyLocationButtonCLick()");
		
		Location loc = mLocationClient.getLastLocation();
		
		getCrimeData(new LatLng(loc.getLatitude(), loc.getLongitude()));
		return false;
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Toast.makeText(this, "Connection failed...", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onConnected(Bundle bundle) {
		Toast.makeText(this, "Connected to location service", Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void onDisconnected() {
		Toast.makeText(this, "Connection to location service lost", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onLocationChanged(Location loc) {
		Log.d(TAG, "LocationUpdated!");
		
	}
}
