package com.dwoodhouse.sleuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dwoodhouse.streetcrimes.R;
import com.google.android.gms.maps.model.LatLng;

public class CombinedStreetCrimeData {
	private List<StreetCrimeData> mCrimes;
	private final String TAG = "CombinedStreetCrimeData";
	private LatLng mLocation;
	private String mLocationName;
	private String mMonth;
	private String mCategory;
	private Activity mActivity;
	
	private Map<String, Integer> mCrimeCategoryCount;
	
	public CombinedStreetCrimeData(StreetCrimeData nData, Activity context)
	{
		mCrimes = new ArrayList<StreetCrimeData>();
		mCrimes.add(nData);
		
		mCrimeCategoryCount = new HashMap<String, Integer>();
		mLocation = nData.getLocation().toLatLng();
		mLocationName = nData.getLocation().street.name;
		mMonth = nData.getMonth();
		mCategory = nData.getCategory();
		
		mCrimeCategoryCount.put(mCategory, 1);
		
		
		mActivity = context;
	}
	
	public void addCrimeData(StreetCrimeData nData)
	{
		mCrimes.add(nData);
		int count = 1;
		if (mCrimeCategoryCount.containsKey(nData.getCategory()))
		{
			count += mCrimeCategoryCount.get(nData.getCategory());
		}
		
		mCrimeCategoryCount.put(nData.getCategory(), count);
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

        for (String key : mCrimeCategoryCount.keySet())
        {
        	TextView crimeInformation = (TextView) mActivity.getLayoutInflater().inflate(R.layout.info_window_information_item, null);
        	crimeList.addView(crimeInformation);
        	
        	String tvString = MainActivity.mMapMarkerTitleMap.get(key);
    		tvString += ": ";
    		tvString += Integer.toString(mCrimeCategoryCount.get(key));
    		crimeInformation.setText(tvString);
        }
        
		return windowLayout;
	}

}
