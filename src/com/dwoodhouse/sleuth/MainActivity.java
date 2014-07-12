package com.dwoodhouse.sleuth;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.dwoodhouse.sleuth.math.LatLngHelper;
import com.dwoodhouse.streetcrimes.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

public class MainActivity extends SherlockFragmentActivity implements OnMarkerClickListener,  OnMyLocationButtonClickListener, ConnectionCallbacks,
OnConnectionFailedListener, LocationListener, Observer {
	private static final int MAX_CRIMES_TO_DISPLAY = 1000;
	private static final String KEY_UPDATES_ON = "KEY_UPDATES_ON";
	private final String TAG = "MainActivity";
	private final double LAT = 51.5046742;
	private final double LNG = -0.0860056;
	private List<Marker> mMapMarkers;
	private List<String> notifications;
	
	private GoogleMap mMap;
	
	private LocationClient mLocationClient;
	private boolean mUpdatesRequested;
	
	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor mSharedPrefEditor;
	
	private static final int MILLISECONDS_PER_SECOND = 1000;
    private static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    
    LocationRequest mLocationRequest;
    
    // ABS Navigation Drawer
    private String[] title;
	private String[] subtitle;
    private CharSequence mDrawerTitle;
	private CharSequence mTitle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private MenuListAdapter mMenuAdapter;
    
    private Map<String, Float> mMarkerColourMap;
    public static Map<String, String> mMapMarkerTitleMap;
    
	private Activity getActivity() { return this; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// Initialisation
		setContentView(R.layout.main_layout);
		mTitle = mDrawerTitle = getTitle();
		notifications = new ArrayList<String>();
		mMapMarkers = new ArrayList<Marker>();
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		mLocationClient = new LocationClient(this, this, this);
        mLocationRequest = LocationRequest.create();
        mSharedPreferences = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        mSharedPrefEditor = mSharedPreferences.edit();
        mUpdatesRequested = true;
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.listview_drawer);
        mMarkerColourMap = new HashMap<String, Float>();
        mMarkerColourMap.put("anti-social-behaviour", BitmapDescriptorFactory.HUE_AZURE);
        mMarkerColourMap.put("bicycle-theft", BitmapDescriptorFactory.HUE_BLUE);
        mMarkerColourMap.put("burglary", BitmapDescriptorFactory.HUE_BLUE);
        mMarkerColourMap.put("criminal-damage-arson", BitmapDescriptorFactory.HUE_CYAN);
        mMarkerColourMap.put("drugs", BitmapDescriptorFactory.HUE_GREEN);
        mMarkerColourMap.put("other-theft", BitmapDescriptorFactory.HUE_BLUE);
        mMarkerColourMap.put("possession-of-weapons", BitmapDescriptorFactory.HUE_MAGENTA);
        mMarkerColourMap.put("public-order", BitmapDescriptorFactory.HUE_ORANGE);
        mMarkerColourMap.put("robbery", BitmapDescriptorFactory.HUE_BLUE);
        mMarkerColourMap.put("shoplifting", BitmapDescriptorFactory.HUE_BLUE);
        mMarkerColourMap.put("theft-from-the-person", BitmapDescriptorFactory.HUE_BLUE);
        mMarkerColourMap.put("vehicle-crime", BitmapDescriptorFactory.HUE_RED);
        mMarkerColourMap.put("violent-crime", BitmapDescriptorFactory.HUE_ROSE);
        mMarkerColourMap.put("other-crime", BitmapDescriptorFactory.HUE_YELLOW);
        
        mMapMarkerTitleMap = new HashMap<String, String>();
        mMapMarkerTitleMap.put("anti-social-behaviour", "Anti Social Behaviour");
        mMapMarkerTitleMap.put("bicycle-theft", "Theft - Bicycle");
        mMapMarkerTitleMap.put("burglary", "Burglary");
        mMapMarkerTitleMap.put("criminal-damage-arson", "Arson");
        mMapMarkerTitleMap.put("drugs", "Drugs");
        mMapMarkerTitleMap.put("other-theft", "Theft - Other");
        mMapMarkerTitleMap.put("possession-of-weapons", "Weapons Possession");
        mMapMarkerTitleMap.put("public-order", "Public Order");
        mMapMarkerTitleMap.put("robbery", "Robbery");
        mMapMarkerTitleMap.put("shoplifting", "Shoplifting");
        mMapMarkerTitleMap.put("theft-from-the-person", "Theft From The Person");
        mMapMarkerTitleMap.put("vehicle-crime", "Vehicle Crime");
        mMapMarkerTitleMap.put("violent-crime", "Violent Crime");
        mMapMarkerTitleMap.put("other-crime", "Other");
        
        title = new String[] 	{/*"Settings 1", "Settings 2", "Settings 3",*/ "Search Range"};
        subtitle = new String[] {/*"subtitle 1", "subtitle 2", "subtitle 3",*/ "1km"};
        mMenuAdapter = new MenuListAdapter(MainActivity.this, title, subtitle);
        mDrawerList.setAdapter(mMenuAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        // ActionBarDrawerToggle ties together the proper interactions
     	// between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
 				this,
 				mDrawerLayout,
 				R.drawable.ic_navigation_drawer, //TODO a decent icon!
 				R.string.drawer_open,
 				R.string.drawer_close) {

 			public void OnDrawerClosed(View view) {
 				super.onDrawerClosed(view);
 			}

 			public void onDrawerOpened(View drawerView) {
 				// Set the title on the action when drawer open
 				getSupportActionBar().setTitle(mDrawerTitle);
 				super.onDrawerOpened(drawerView);
 			}
 		};
     		
     	mDrawerLayout.setDrawerListener(mDrawerToggle);
     	getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setHomeButtonEnabled(true);
        
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
		
		// Disable user inputs
		mMap.getUiSettings().setAllGesturesEnabled(true);
		mMap.getUiSettings().setZoomControlsEnabled(false);
		mMap.setMyLocationEnabled(true);
		
		mMap.setOnMyLocationButtonClickListener((OnMyLocationButtonClickListener)this);
		
        // position the camera over london bridge
		LatLng londonBridge = new LatLng(LAT, LNG);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(londonBridge, 13));  
        
        
        // Send off a request to retrieve the crime data using the default location initially
     	//getCrimeData(new LatLng(LAT, LNG));

        // Set a click listener for the markers to display the position overlay
        mMap.setOnMarkerClickListener((OnMarkerClickListener)this);
        
        notifications.add(Notification.ADD_MAP_MARKER);

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == android.R.id.home) 
		{
			if (mDrawerLayout.isDrawerOpen(mDrawerList))
			{
				mDrawerLayout.closeDrawer(mDrawerList);
			} 
			else 
			{
				mDrawerLayout.openDrawer(mDrawerList);
			}
		}

		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void setTitle(CharSequence title) 
	{
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
       	super.onPostCreate(savedInstanceState);
       	// Sync the toggle state after onRestoreInstanceState has occurred.
       	mDrawerToggle.syncState();
    }
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
		{
			selectItem(position);
		}
	}
	
	private void selectItem(int position) {
		//TODO no interaction yet...
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		mSharedPrefEditor.putBoolean(KEY_UPDATES_ON, mUpdatesRequested);
		mSharedPrefEditor.commit();
		
		for (String s : notifications)
        {
        	ObservingService.getInstance().addObserver(s, this);
        }
		
		if (!mLocationClient.isConnected() && !mLocationClient.isConnecting())
			mLocationClient.connect();	
	}
	
	@Override
	protected void onStop()
	{
		if (mLocationClient.isConnected())
		{
			mLocationClient.removeLocationUpdates(this);
		}
		
		for (String s : notifications)
        {
        	ObservingService.getInstance().removeObserver(s, this);
        }
		
		mLocationClient.disconnect();
		super.onStop();
	}
	
	@Override
	protected void onPause()
	{
		mSharedPrefEditor.putBoolean(KEY_UPDATES_ON, mUpdatesRequested);
		mSharedPrefEditor.commit();
		super.onPause();
	}
	
	@Override
	protected void onResume()
	{
		if (mSharedPreferences.contains(KEY_UPDATES_ON)) 
		{
            mUpdatesRequested = mSharedPreferences.getBoolean(KEY_UPDATES_ON, false);
        } 
		else 
        {
            mSharedPrefEditor.putBoolean(KEY_UPDATES_ON, true);
            mSharedPrefEditor.commit();
        }
		
		super.onResume();
	}
           
	private void getCrimeData(LatLng latLng, List<LatLng> polyList)
	{
		new GetCrimesTask(latLng, polyList);
	}
	
	private void AddMapMarkers(String data)
	{
		Log.d(TAG, data);
		for (Marker m : mMapMarkers)
		{
			m.remove();
		}
		mMapMarkers.clear();
		try 
		{
			List<StreetCrimeData> crimesDataList = new LinkedList<StreetCrimeData>(Arrays.asList(new Gson().fromJson(data, StreetCrimeData[].class))); // convert the array into a list, more flexible.
			List<StreetCrimeData> crimesDataListCopy = new ArrayList<StreetCrimeData>(crimesDataList); 
			final List<CombinedStreetCrimeData> combinedStreetCrimeDataList = new ArrayList<CombinedStreetCrimeData>(); // This list contains all data at the same location in one object
			int duplicatesFound = 0;
			List<StreetCrimeData> removedData = new ArrayList<StreetCrimeData>();
			// Loop through list to find duplicate locations, we'll merge these together
			
			/*
			 * TODO - This nested loop arrangement is really horrible...
			 * Perhaps use an Iterator instead?
			 */
			
			// Limit results to 1000 crimes
			boolean hitCrimeLimit = false;
			for (int i = 0; i < (crimesDataList.size() <= MAX_CRIMES_TO_DISPLAY ? crimesDataList.size() : MAX_CRIMES_TO_DISPLAY); i++)
			{
				if (i == 999 && crimesDataList.size() > 999)
					hitCrimeLimit = true;
				
				boolean wantToContinue = false;
				StreetCrimeData nData = crimesDataList.get(i);
				for (StreetCrimeData qData : removedData)
				{
					if (nData.equals(qData))
						wantToContinue = true;
				}
				
				if (wantToContinue)
					continue;
				
				LatLng currentLatLng = new LatLng(nData.getLocation().latitude, nData.getLocation().longitude);
				CombinedStreetCrimeData combinedData = new CombinedStreetCrimeData(nData, this);
				for (int j = crimesDataListCopy.size() - 1; j >= i + 1; j--)
				{
					StreetCrimeData pData = crimesDataListCopy.get(j);
					LatLng nextLatLng = new LatLng(pData.getLocation().latitude, pData.getLocation().longitude);
					if (currentLatLng.equals(nextLatLng))
					{
						duplicatesFound++;
						combinedData.addCrimeData(pData);
						removedData.add(pData);
					}
				}
				
				combinedStreetCrimeDataList.add(combinedData);
			}
			
			if (hitCrimeLimit)
				Toast.makeText(this, "Too many crimes! Displaying first 1000 only", Toast.LENGTH_LONG).show();
			
			//Log.d(TAG, "Duplicates Found: " + Integer.toString(duplicatesFound));

			// Now loop through the combined data list and add these markers to the map
			for (final CombinedStreetCrimeData nData : combinedStreetCrimeDataList)
			{
				LatLng loc = nData.getmLocation();
				
				// TODO combine markers at the same location into one and add this information to the infoWindow
				final Marker newMarker = mMap.addMarker(new MarkerOptions()
					.position(loc)
					.snippet(nData.getmLocationName())
					.icon(BitmapDescriptorFactory.defaultMarker(nData.getmCrimes().size() > 1 ? BitmapDescriptorFactory.HUE_VIOLET : mMarkerColourMap.get(nData.getmCategory()))));
			
					
					mMapMarkers.add(newMarker);
				

				// Setting a custom info window adapter for the google map
		        mMap.setInfoWindowAdapter(new InfoWindowAdapter() {
		 
		            // Use default InfoWindow frame
		            @Override
		            public View getInfoWindow(Marker marker) {
		                return null;
		            }
		 
		            // Defines the contents of the InfoWindow
		            @Override
		            public View getInfoContents(Marker marker) {
		            	CombinedStreetCrimeData nMarker = null;
		            	for (CombinedStreetCrimeData n : combinedStreetCrimeDataList)
		            	{
		            		if (n.getmLocationName().equals(marker.getSnippet())) // TODO Need a better unique ID!!
		            		{
		            			nMarker = n;
		            			break;
		            		}
		            	}
		                
		            	return nMarker.getView();
		            
		            }
		        });
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.e(TAG, "ERROR PARSING THE JSON DATA, " + e.getMessage());			
		}
	}
	
	// Cannot run http requests on main thread so use this ASyncTask to run the connection request
	private class GetCrimesTask extends AsyncTask<String, Void, String> {
		private List<LatLng> mPolyList;
		String requestURL;
		public GetCrimesTask(LatLng origin, List<LatLng> polyList)
		{
			mPolyList = polyList;
			
			requestURL = "http://data.police.uk/api/crimes-street/all-crime?poly=";
			requestURL += getPoly(mPolyList);
			requestURL += "&date=2014-04";
			
			execute();
		}
		
		private String getPoly(List<LatLng> positions)
		{
			String polyArg = "";
			for (LatLng latLng : positions)
			{
				String polyPair = Double.toString(latLng.latitude);
				polyPair += ",";
				polyPair += Double.toString(latLng.longitude);
				if (latLng != positions.get(positions.size() - 1))
					polyPair += ":";
				polyArg += polyPair;
			}

			return polyArg;
		}
		
	    protected String doInBackground(String... urls) 
	    {
	    	try
			{
	    		//TODO handle time outs!
				BufferedReader in;
				//http://data.police.uk/api/crimes-street/all-crime?poly=52.268,0.543:52.794,0.238:52.130,0.478&date=2013-01
				
				Log.d(TAG, requestURL);
				
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
	    	Log.d(TAG, serverData);
	        AddMapMarkers(serverData);
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

	@Override
    public boolean onMarkerClick(Marker marker) 
	{       
		marker.showInfoWindow();
		return true;
    }
	
	@Override
	public boolean onMyLocationButtonClick() {
		
		LatLng origin = new LatLng(mLocationClient.getLastLocation().getLatitude(), mLocationClient.getLastLocation().getLongitude());
		double range = mMenuAdapter.getBarProgress(); // /2 to get radius of range rather than total range
		//Log.d(TAG, "Range: " + Double.toString(range));
		List<LatLng> polyList = new ArrayList<LatLng>();
		
		// Hardcoded way was ugly, this allows for easier modification
		int precision = 8; // number of degree steps to take for the poly line
		for (int i = 0; i < precision; i++)
		{
			int degrees = (360 / precision) * i;
			polyList.add(LatLngHelper.findDestinationWithDistance(range, degrees, origin));
		}

		getCrimeData(origin, polyList);
		
		return false;
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.e(TAG, "Connection Failed");
		Toast.makeText(this, "Connection failed...", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onConnected(Bundle bundle) {
		Toast.makeText(this, "Connected to location service", Toast.LENGTH_SHORT).show();
		Log.i(TAG, "Connected to service, " + Boolean.toString(mUpdatesRequested));
		
		if (mUpdatesRequested) 
		{
            mLocationClient.requestLocationUpdates(mLocationRequest, this);
        }
	}

	@Override
	public void onDisconnected() {
		Log.e(TAG, "Connection to location service lost");
		Toast.makeText(this, "Connection to location service lost", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onLocationChanged(Location loc) {
		//Log.d(TAG, "LocationUpdated!");
	}

	@Override
	public void update(Observable observable, Object data) 
	{
		//Notification pData = (Notification)data;
	}
}
