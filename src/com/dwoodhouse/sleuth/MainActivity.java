package com.dwoodhouse.sleuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
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
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

public class MainActivity extends SherlockFragmentActivity implements ConnectionCallbacks,
OnConnectionFailedListener, LocationListener, Observer {
	private static final int MAX_CRIMES_TO_DISPLAY = 1000;
	private static final String KEY_UPDATES_ON = "KEY_UPDATES_ON";
	private final String TAG = "MainActivity";
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
    
    private CharSequence mDrawerTitle;
	private CharSequence mTitle;
    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private Map<String, Float> mMarkerColourMap;
    

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
        
        new NavigationDrawerHandler(this, (LinearLayout)findViewById(R.id.drawer_list), mSharedPreferences); // create object to handle drawer input
        
        mUpdatesRequested = true;
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (LinearLayout)findViewById(R.id.drawer_list);
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

        mDrawerToggle = new ActionBarDrawerToggle(
 				this,
 				mDrawerLayout,
 				R.drawable.ic_navigation_drawer,
 				R.string.drawer_open,
 				R.string.drawer_close) {


			public void onDrawerClosed(View view) {
				
				// If the keyboard was open during postcode entry, force it to hide.
				InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

				// check if no view has focus:
				View v = getCurrentFocus();
				if (v == null)
					return;

				inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		        
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
        
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

		mMap.getUiSettings().setAllGesturesEnabled(true);
		mMap.getUiSettings().setZoomControlsEnabled(false);
		mMap.setMyLocationEnabled(true);
		
		// Set initial map location to the last known location of this device.
		Criteria criteria = new Criteria();
		LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
		String provider = locationManager.getBestProvider(criteria, true);
		Location location = locationManager.getLastKnownLocation(provider);
		LatLng initialLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLatLng, 13));  

        // Set a click listener for the markers to display the position overlay
        mMap.setOnMarkerClickListener(new OnMarkerClickListener()
        {
			@Override
			public boolean onMarkerClick(Marker marker) {
				marker.showInfoWindow();
				
				return true;
			}
        });
        
        notifications.add(Notification.ADD_MAP_MARKERS);
        notifications.add(Notification.SLEUTH_BUTTON_PRESSED);
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
	
	/*
	 * Show / Hide navigation drawer
	 */
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
	
	@Override
	protected void onStop()
	{
		if (mLocationClient.isConnected())
		{
			mLocationClient.removeLocationUpdates(this);
		}
		
		mLocationClient.disconnect();
		
		for (String s : notifications)
        {
        	ObservingService.getInstance().removeObserver(s, this);
        }

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
	
	private void addMapMarkers(String data)
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
			List<StreetCrimeData> removedData = new ArrayList<StreetCrimeData>();

			if (crimesDataList.size() == 0)
			{
				Toast.makeText(this, "No crimes reported in that area with the supplied filters.", Toast.LENGTH_LONG).show();
				return;
			}
			/*
			 * TODO - This nested loop arrangement is really horrible...
			 * Perhaps use an Iterator instead?
			 */
			boolean hitCrimeLimit = false;
			for (int i = 0; i < (crimesDataList.size() <= MAX_CRIMES_TO_DISPLAY ? crimesDataList.size() : MAX_CRIMES_TO_DISPLAY); i++)
			{
				if (i == MAX_CRIMES_TO_DISPLAY && crimesDataList.size() > MAX_CRIMES_TO_DISPLAY)
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
			// TODO nicer exception handling for the user
			e.printStackTrace();			
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
	public void onConnectionFailed(ConnectionResult result) 
	{
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
	
	public void onSleuthButtonPressed(int range)
	{
		// Basic code for postcode search
		// No error handling, only basic code to illicit a result
		Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
	    List<Address> address = null;
	    double lat = 0;
	    double lon = 0;
	    boolean postcodeValid = false;
	    LatLng origin = new LatLng(0,0);

	    if (mSharedPreferences.getBoolean(NavigationDrawerHandler.KEY_SEARCH_MY_LOCATION, true))
	    {
	    	postcodeValid = true;
	    	origin = new LatLng(mLocationClient.getLastLocation().getLatitude(), mLocationClient.getLastLocation().getLongitude());
	    }
	    else
	    {
	    	if (!NavigationDrawerHandler.getPostcode().equals(""))
	    	{
		    	try 
		    	{
		    		address = geoCoder.getFromLocationName(NavigationDrawerHandler.getPostcode(), 10);
		        } 
		    	catch (IOException e) 
		        {
		            e.printStackTrace();
		        }
		    	
		        if (address.size() > 0) 
		        {
		        	postcodeValid = true;
		            Address add = address.get(0);
		            lat = add.getLatitude();
		            lon = add.getLongitude();
		        }  
		        
		        origin = new LatLng(lat, lon);
		    }
	    }
	    
	    
	    if (!postcodeValid)
	    {
	    	Toast.makeText(this, "Please enter a valid postcode", Toast.LENGTH_SHORT).show();
	    	return;
	    }
	    //Log.i(TAG, origin.toString());
	    
	   
		float pRange = (float)range / 2.0f;
		List<LatLng> polyList = new ArrayList<LatLng>();
		
		int precision = 8; // number of degree steps to take for the poly line
		for (int i = 0; i < precision; i++)
		{
			int degrees = (360 / precision) * i;
			polyList.add(LatLngHelper.findDestinationWithDistance(pRange, degrees, origin));
		}

		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 13)); //TODO make this animate
		getCrimeData(origin, polyList);
	}

	@Override
	public void update(Observable observable, Object data) 
	{
		Notification pData = (Notification)data;
		if (pData.isNotificationType(Notification.ADD_MAP_MARKERS))
		{
			addMapMarkers((String)pData.get("serverData"));
		}
		
		if (pData.isNotificationType(Notification.SLEUTH_BUTTON_PRESSED))
		{
			onSleuthButtonPressed((Integer)pData.get("range"));
		}
	}
}
