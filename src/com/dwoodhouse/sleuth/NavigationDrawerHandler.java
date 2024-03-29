package com.dwoodhouse.sleuth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.dwoodhouse.streetcrimes.R;
import com.google.gson.Gson;

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
	
	private static CrimeCategories[] mAvailableCategories;
	
	// Controls the update of the sleuth button text for some user feedback
	private boolean mSleuthing = false;
	private final int UPDATE_DELAY = 200;
	private final String[] mSleuthUpdateText = new String[] {"Sleuthing...", "Sleuthing", "Sleuthing.", "Sleuthing.."};
	private int mSleuthUpdateCount = 0;
	private Handler mSleuthButtonUpdateHandler;
	private Runnable mSleuthButtonUpdateRunnable = new Runnable()
	{
		@Override
		public void run() 
		{
			mSleuthUpdateCount++;
			if (mSleuthUpdateCount > 3)
			{
				mSleuthUpdateCount = 0;
			}
			
			if (mSleuthing)
			{
				mSleuthButton.setText(mSleuthUpdateText[mSleuthUpdateCount]);
				mSleuthButtonUpdateHandler.postDelayed(mSleuthButtonUpdateRunnable, UPDATE_DELAY);
			}
			else
			{
				mSleuthButton.setText(R.string.button_sleuth);
				mSleuthButtonUpdateHandler.removeCallbacks(this);
			}
			
			udpateView(mSleuthButton);	
		}
	};
	
	private ArrayList<String> mNotifications;
	
	private void udpateView(final View v) 
	{
		mActivity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				v.invalidate();
			}
		});
	}	
	
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

		mNotifications = new ArrayList<String>();
		mNotifications.add(Notification.MAP_MARKERS_ADDED);
		mNotifications.add(Notification.RETRIEVED_CRIME_CATEGORIES);
		mNotifications.add(Notification.SLEUTH_ERROR);
		
		for (String s : mNotifications)
			ObservingService.getInstance().addObserver(s, this);
		
		mSleuthButtonUpdateHandler = new Handler();
        
		initialiseRangeBar();
		initialiseDateSpinner();
		initialiseRadioButtons();
		//initialiseCategoryBoxes();
		initialiseSleuthButton();
	}

	private void initialiseRadioButtons() 
	{
		mRbMyLocation = (RadioButton)mDrawerLayout.findViewById(R.id.radio_my_location);
		mRbPostcode = (RadioButton)mDrawerLayout.findViewById(R.id.radio_by_postcode);
		
		mRbMyLocation.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				onRadioButtonClicked(v);
			}
		});
		mRbMyLocation.setChecked(true);
		mSharedPrefEditor.putBoolean(KEY_SEARCH_MY_LOCATION, true).commit();
		
		mRbPostcode.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
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
		
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, dates);
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
		return editText.getText().toString().replaceAll("\\s+", ""); // remove all whitespace
	}
	
	
	// Shamelessly stole this from the internet
	public static
	<T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c)
	{
	  List<T> list = new ArrayList<T>(c);
	  java.util.Collections.sort(list);
	  return list;
	}
	
	private void initialiseCategoryBoxes()
	{
		final LinearLayout catList = (LinearLayout) mDrawerLayout.findViewById(R.id.category_list);
		catList.removeAllViews();
		mCategoriesToShow.clear();
		for (int i = 0; i < mAvailableCategories.length; i++)
		{
			String categoryName = mAvailableCategories[i].getName();
			String categoryId = mAvailableCategories[i].getId();
			
			mCategoriesToShow.put(categoryId, true);
			//Log.i(TAG, categoryId);
			CheckBox box = new CheckBox(mContext);
			box.setText( categoryName);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			box.setLayoutParams(params);
			box.setTag(categoryId);
			box.setChecked(true);
			box.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					CheckBox clickedBox = (CheckBox) v;
					boolean isChecked = clickedBox.isChecked();
					
					if (clickedBox.getTag().equals("all-crime"))
					{
						for (String key : mCategoriesToShow.keySet())
						{
							CheckBox b = (CheckBox)catList.findViewWithTag(key);
							b.setChecked(isChecked);
							mCategoriesToShow.put(key, isChecked);
						}
					}
					else
					{
						mCategoriesToShow.put((String)v.getTag(), isChecked);
						
						// Uncheck the all crimes box
						if (!isChecked)
						{
							CheckBox b = (CheckBox)catList.findViewWithTag("all-crime");
							b.setChecked(false);
							mCategoriesToShow.put("all-crime", false);
						}
						else
						{
							// We've made something true, check all other boxes and update all crimes as appropriate
							boolean allCheckedTrue = true;
							for (String key : mCategoriesToShow.keySet())
							{
								if (key.equals("all-crime"))
									continue;
								
								if (!mCategoriesToShow.get(key))
								{
									allCheckedTrue = false;
									break;
								}
							}
							
							if (allCheckedTrue)
							{
								CheckBox b = (CheckBox)catList.findViewWithTag("all-crime");
								b.setChecked(true);
							}
						}
					}
				}
			});
			catList.addView(box);
			
			mCategoriesToShow.put(categoryId, true);
		}
	}

	private AlertDialog buildCheckConnectionDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("Check your connection");
		builder.setMessage("An internet connection is required to Sleuth, check your connection and try again.");
		
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				dialog.dismiss();
			}
		});
		return builder.create();
	}
	private void initialiseSleuthButton() 
	{
		mSleuthButton = (Button)mDrawerLayout.findViewById(R.id.button_sleuth);
		mSleuthButton.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View view) 
			{
				if (Application.checkConnection(mActivity))	
				{
					mSleuthing = true;
					mSleuthButtonUpdateHandler.post(mSleuthButtonUpdateRunnable);
					mSleuthButton.setEnabled(false);
					Notification n = new Notification();
					n.put("range", mRangeBarProgress);
					n.put("date", mDateSpinner.getSelectedItem());
					
					ObservingService.getInstance().postNotification(Notification.SLEUTH_BUTTON_PRESSED, n);
				}
				else
				{
					buildCheckConnectionDialog().show();
				}
			}	
		});
	}

	private void initialiseRangeBar()
	{
		mRangeBar = (SeekBar) mDrawerLayout.findViewById(R.id.range_bar);
		mRangeBarProgress = 1;
		mRangeBar.setOnTouchListener(new ListView.OnTouchListener()
		{
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				int action = event.getAction();

				switch (action) 
				{
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
		
		mRangeBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mRangeBarProgress = seekBar.getProgress() + 1;
				String s;
				s = Integer.toString(mRangeBarProgress);
				s += mRangeBarProgress == 1 ? " mile" : " miles";
				
				TextView tS = (TextView) mDrawerLayout.findViewById(R.id.range_bar_subtitle);
				tS.setText(s);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// Do nothing
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// Do nothing
			}
		});
	}

	@Override
	public void update(Observable observable, Object data)
	{
		Notification pData = (Notification)data;
		if (pData.isNotificationType(Notification.MAP_MARKERS_ADDED))
		{
			mSleuthing = false;
			mSleuthButton.setEnabled(true);
			mSleuthUpdateCount = 0;
			
			//mSleuthButtonUpdateHandler.post(mSleuthButtonUpdateRunnable);
		}
		
		if (pData.isNotificationType(Notification.RETRIEVED_CRIME_CATEGORIES))
		{
			try
			{
				String jsonData = (String) pData.get("response");
				mAvailableCategories = new Gson().fromJson(jsonData, CrimeCategories[].class);

				initialiseCategoryBoxes();
			}
			catch (Exception e)
			{
				Toast.makeText(mContext, "Something went wrong, try again", Toast.LENGTH_LONG).show();
				ObservingService.getInstance().postNotification(Notification.SLEUTH_ERROR);
			}
		}
		
		if (pData.isNotificationType(Notification.SLEUTH_ERROR))
		{
			mSleuthing = false;
			mSleuthButton.setEnabled(true);
			mSleuthUpdateCount = 0;
		}
	}

	public static String getCategoryNameForId(String key) 
	{
		for (int i = 0; i < mAvailableCategories.length; i++)
		{
			if (key.equals(mAvailableCategories[i].getId()))
			{
				return mAvailableCategories[i].getName();
			}
		}
		
		return null;
	}
}