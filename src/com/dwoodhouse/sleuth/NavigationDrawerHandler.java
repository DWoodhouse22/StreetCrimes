package com.dwoodhouse.sleuth;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.dwoodhouse.streetcrimes.R;

public class NavigationDrawerHandler {

	public static final String KEY_SEARCH_MY_LOCATION = "KEY_SEARCH_MY_LOCATION";
	private final String TAG = "NavigationDrawerHandler";
	private static LinearLayout mDrawerLayout;
	private SeekBar mRangeBar;
	private int mRangeBarProgress;
	
	private RadioButton mRbMyLocation;
	private RadioButton mRbPostcode;
	
	private Button mSleuthButton;
	
	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor mSharedPrefEditor;
	public NavigationDrawerHandler(LinearLayout layout, SharedPreferences sharedPreferences) {
		mDrawerLayout = layout;
		mSharedPreferences = sharedPreferences;
		mSharedPrefEditor = mSharedPreferences.edit();
		initialiseRangeBar();
		initialiseRadioButtons();
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
		return editText.getText().toString();
	}

	private void initialiseSleuthButton() {
		mSleuthButton = (Button)mDrawerLayout.findViewById(R.id.button_sleuth);
		mSleuthButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Notification n = new Notification();
				n.put("range", mRangeBarProgress);
				ObservingService.getInstance().postNotification(Notification.SLEUTH_BUTTON_PRESSED, n);
			}	
		});
	}

	private void initialiseRangeBar() {
		mRangeBar = (SeekBar) mDrawerLayout.findViewById(R.id.range_bar);

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
				s += "Km";
				
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
}
