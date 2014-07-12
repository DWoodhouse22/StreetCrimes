package com.dwoodhouse.sleuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
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
        	String category = data.getCategory();
        	TextView crimeInformation;
        	if (mCrimeCategoryCount.containsKey(data.getCategory()))
        	{
        		mCrimeCategoryCount.put(category, mCrimeCategoryCount.get(category) + 1);
        		crimeInformation = (TextView)crimeList.findViewWithTag(data.getCategory());
        	}
        	else
        	{
        		mCrimeCategoryCount.put(category, 1);
        		crimeInformation = new TextView(mActivity);
        		crimeInformation.setTag(category);
        		crimeList.addView(crimeInformation);
        		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            	crimeInformation.setLayoutParams(params);
        	}
        	
        	String s = MainActivity.mMapMarkerTitleMap.get(data.getCategory());
    		s += ": ";
    		s += Integer.toString(mCrimeCategoryCount.get(category));
    		crimeInformation.setText(s);
        }
        
		return windowLayout;
	}

}
