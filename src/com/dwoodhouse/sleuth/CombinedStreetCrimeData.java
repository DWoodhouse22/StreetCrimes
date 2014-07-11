package com.dwoodhouse.sleuth;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dwoodhouse.streetcrimes.R;
import com.google.android.gms.maps.model.LatLng;

public class CombinedStreetCrimeData {
	private List<StreetCrimeData> mCrimes;
	
	private LatLng mLocation;
	private String mLocationName;
	private String mMonth;
	private String mCategory;
	private Activity mActivity;
	
	public CombinedStreetCrimeData(StreetCrimeData nData, Activity context)
	{
		mCrimes = new ArrayList<StreetCrimeData>();
		mCrimes.add(nData);
		
		mLocation = nData.getLocation().toLatLng();
		mLocationName = nData.getLocation().street.name;
		mMonth = nData.getMonth();
		mCategory = nData.getCategory();
		
		mActivity = context;
	}
	
	public void addCrimeData(StreetCrimeData nData)
	{
		mCrimes.add(nData);
	}
	
	public List<StreetCrimeData> getmCrimes() {
		return mCrimes;
	}

	public LatLng getmLocation() {
		return mLocation;
	}

	public String getmLocationName() {
		return mLocationName;
	}
	
	public String getmCategory()
	{
		return mCategory;
	}

	public View getView() {
		View windowLayout = mActivity.getLayoutInflater().inflate(R.layout.map_custom_info_window, null);
		
		TextView title = (TextView) windowLayout.findViewById(R.id.info_title);
        TextView subtitle = (TextView) windowLayout.findViewById(R.id.info_subtitle);
        LinearLayout crimeList = (LinearLayout)windowLayout.findViewById(R.id.info_list);
        
        title.setText("Crime " + mLocationName);
        subtitle.setText(Integer.toString(mCrimes.size()) + " crimes reported here in " + mMonth);

        for (StreetCrimeData data : mCrimes)
        {
        	TextView crimeInformation = new TextView(mActivity);
        	crimeInformation.setText(MainActivity.mMapMarkerTitleMap.get(data.getCategory()));
        	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        	
        	crimeInformation.setLayoutParams(params);
        	crimeList.addView(crimeInformation);
        }
        
		return windowLayout;
	}

}
