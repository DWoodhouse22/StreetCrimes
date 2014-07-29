package com.dwoodhouse.sleuth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.dwoodhouse.streetcrimes.R;

public class NavigationDrawerHandler implements Observer {

	public static final String KEY_SEARCH_MY_LOCATION = "KEY_SEARCH_MY_LOCATION";
	private static final String TAG = "NavigationDrawerHandler";
	private static LinearLayout mDrawerLayout;
	private SeekBar mRangeBar;
	private int mRangeBarProgress;
	
	private Spinner mDateSpinner;
	private ArrayList<DateManager> nAvailableDates;
	
	private RadioButton mRbMyLocation;
	private RadioButton mRbPostcode;
	
	private Button mSleuthButton;
	private Context mContext;
	private Activity mActivity;
	
	// Controls the update of the sleuth button text for some user feedback
	private boolean mSleuthing = false;
	private final int UPDATE_DELAY = 200;
	private final String[] mSleuthUpdateText = new String[] {"Sleuthing", "Sleuthing.", "Sleuthing..", "Sleuthing..."};
	private int mSleuthUpdateCount = 0;
	private Handler mSleuthButtonUpdateHandler;
	private Runnable mSleuthButtonUpdateRunnable = new Runnable()
	{
		@Override
		public void run() 
		{
			mActivity.runOnUiThread(new Runnable()
			{
				@Override
				public void run() {
					if (mSleuthing)
						mSleuthButton.setText(mSleuthUpdateText[mSleuthUpdateCount]);
					else
						mSleuthButton.setText(R.string.button_sleuth);
					mSleuthButton.invalidate();
				}
			});
			
			mSleuthUpdateCount++;
			if (mSleuthUpdateCount > 3)
			{
				mSleuthUpdateCount = 0;
			}
			
			if (mSleuthing)
			{
				mSleuthButtonUpdateHandler.postDelayed(mSleuthButtonUpdateRunnable, UPDATE_DELAY);
			}
		}	
	};
	
	public static HashMap<String, String> mMapMarkerTitleMap;
	public static HashMap<String, Boolean> mCategoriesToShow;
	
	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor mSharedPrefEditor;
	
	public NavigationDrawerHandler(Context context, LinearLayout layout, SharedPreferences sharedPreferences, ArrayList<DateManager> availableDates) {
		mContext = context;
		mActivity = (Activity) context;
		mDrawerLayout = layout;
		mSharedPreferences = sharedPreferences;
		nAvailableDates = availableDates;
		mSharedPrefEditor = mSharedPreferences.edit();
		mCategoriesToShow = new HashMap<String, Boolean>();
		mMapMarkerTitleMap = new HashMap<String, String>();
		
		ObservingService.getInstance().addObserver(Notification.ADD_MAP_MARKERS, this);
		
		mSleuthButtonUpdateHandler = new Handler();
		
        mMapMarkerTitleMap.put("anti-social-behaviour", "Anti Social Behaviour");
        mMapMarkerTitleMap.put("criminal-damage-arson", "Arson");
        mMapMarkerTitleMap.put("bicycle-theft", "Bicycle Theft");
        mMapMarkerTitleMap.put("burglary", "Burglary");
        mMapMarkerTitleMap.put("drugs", "Drugs");
        mMapMarkerTitleMap.put("other-theft", "Other Theft");
        mMapMarkerTitleMap.put("public-order", "Public Order");
        mMapMarkerTitleMap.put("robbery", "Robbery");
        mMapMarkerTitleMap.put("shoplifting", "Shoplifting");
        mMapMarkerTitleMap.put("theft-from-the-person", "Theft From The Person");
        mMapMarkerTitleMap.put("vehicle-crime", "Vehicle Crime");
        mMapMarkerTitleMap.put("violent-crime", "Violent Crime");
        mMapMarkerTitleMap.put("possession-of-weapons", "Possession Of Weapons");
        mMapMarkerTitleMap.put("other-crime", "Other");
        
		initialiseRangeBar();
		initialiseDateSpinner();
		initialiseRadioButtons();
		initialiseCategoryBoxes();
		initialiseSleuthButton();
	}

	private void initialiseRadioButtons() 
	{
		mRbMyLocation = (RadioButton)mDrawerLayout.findViewById(R.id.radio_my_location);
		mRbPostcode = (RadioButton)mDrawerLayout.findViewById(R.id.radio_by_postcode);
		
		mRbMyLocation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onRadioButtonClicked(v);
			}
		});
		mRbMyLocation.setChecked(true);
		mSharedPrefEditor.putBoolean(KEY_SEARCH_MY_LOCATION, true).commit();
		
		mRbPostcode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onRadioButtonClicked(v);
			}
		});
	}
	
	private void initialiseDateSpinner()
	{
		mDateSpinner = (Spinner) mDrawerLayout.findViewById(R.id.date_spinner);
		ArrayList<String> dates = new ArrayList<String>();
		for (DateManager dm : nAvailableDates)
		{
			dates.add(dm.getDate());
		}
		
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, dates);
		mDateSpinner.setAdapter(spinnerArrayAdapter);

	}
	
	public void onRadioButtonClicked(View v)
	{
		switch (v.getId())
		{
		case R.id.radio_my_location:
			mSharedPrefEditor.putBoolean(KEY_SEARCH_MY_LOCATION, true);
			break;
			
		case R.id.radio_by_postcode:
			mSharedPrefEditor.putBoolean(KEY_SEARCH_MY_LOCATION, false);
			break;
		}
		
		mSharedPrefEditor.commit();
	}
	
	public static String getPostcode()
	{
		EditText editText = (EditText)mDrawerLayout.findViewById(R.id.postcode);
		return editText.getText().toString().trim();
	}
	
	
	// Shamelessly stole this from the internet
	public static
	<T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
	  List<T> list = new ArrayList<T>(c);
	  java.util.Collections.sort(list);
	  return list;
	}
	
	private void initialiseCategoryBoxes()
	{
		LinearLayout catList = (LinearLayout) mDrawerLayout.findViewById(R.id.category_list);
		
		Collection<String> unsorted = mMapMarkerTitleMap.keySet();
		List<String> sortedCatList = asSortedList(unsorted);
		
		for (String categoryId : sortedCatList)
		{
			//Log.i(TAG, categoryId);
			CheckBox box = new CheckBox(mContext);
			box.setText( mMapMarkerTitleMap.get(categoryId));
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			box.setLayoutParams(params);
			box.setTag(categoryId);
			box.setChecked(true);
			box.setOnCheckedChangeListener(new OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
				{
					mCategoriesToShow.put((String)buttonView.getTag(), isChecked);
				}
			});
			catList.addView(box);
			
			mCategoriesToShow.put(categoryId, true);
		}
	}

	private void initialiseSleuthButton() {
		mSleuthButton = (Button)mDrawerLayout.findViewById(R.id.button_sleuth);
		mSleuthButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) 
			{
				mSleuthButton.setEnabled(false);
				Notification n = new Notification();
				n.put("range", mRangeBarProgress);
				n.put("date", mDateSpinner.getSelectedItem());
				ObservingService.getInstance().postNotification(Notification.SLEUTH_BUTTON_PRESSED, n);
				
				mSleuthing = true;
				mSleuthButtonUpdateHandler.post(mSleuthButtonUpdateRunnable);
			}	
		});
	}

	private void initialiseRangeBar() {
		mRangeBar = (SeekBar) mDrawerLayout.findViewById(R.id.range_bar);
		mRangeBarProgress = 1;
		mRangeBar.setOnTouchListener(new ListView.OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();

				switch (action) {
				case MotionEvent.ACTION_DOWN:
					v.getParent().requestDisallowInterceptTouchEvent(true);
					break;

				case MotionEvent.ACTION_UP:
					v.getParent().requestDisallowInterceptTouchEvent(false);
					break;
				}

				// Handle seekbar touch events.
				v.onTouchEvent(event);
				return true;
			}
		});
		
		mRangeBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				mRangeBarProgress = seekBar.getProgress() + 1;
				String s;
				s = Integer.toString(mRangeBarProgress);
				s += "km";
				
				TextView tS = (TextView) mDrawerLayout.findViewById(R.id.range_bar_subtitle);
				tS.setText(s);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
		});
	}

	@Override
	public void update(Observable observable, Object data)
	{
		Notification pData = (Notification)data;
		if (pData.isNotificationType(Notification.ADD_MAP_MARKERS))
		{
			mSleuthing = false;
			mSleuthButton.setEnabled(true);
			mSleuthUpdateCount = 0;
			mActivity.runOnUiThread(new Runnable()
			{
				@Override
				public void run() 
				{
					mSleuthButton.setText(R.string.button_sleuth);
					mSleuthButton.invalidate();
				}
			});
		}
	}
}
