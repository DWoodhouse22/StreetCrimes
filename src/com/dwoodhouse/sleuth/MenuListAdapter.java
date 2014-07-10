package com.dwoodhouse.sleuth;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.dwoodhouse.streetcrimes.R;

public class MenuListAdapter extends BaseAdapter {

	private final String TAG = "MenuListAdapter";
	private Context context;
	private String[] mTitle;
	private String[] mSubTitle;
	private LayoutInflater inflater;
	private int mRangeBarProgress;

	public MenuListAdapter(Context context, String[] title, String[] subtitle) {
		this.context = context;
		this.mTitle = title;
		this.mSubTitle = subtitle;
		mRangeBarProgress = 1;
	}

	@Override
	public int getCount() {
		return mTitle.length;
	}

	@Override
	public Object getItem(int position) {
		return mTitle[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Declare Variables
		TextView txtTitle;
		TextView txtSubTitle = null;
		//ImageView imgIcon;
		SeekBar rangeBar;

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		final View itemView;
		if (mTitle[position].equals("Search Range")) {
			itemView = inflater.inflate(R.layout.drawer_seekbar_layout, parent,
					false);
			rangeBar = (SeekBar) itemView.findViewById(R.id.range_bar);
			rangeBar.setOnTouchListener(new ListView.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int action = event.getAction();

					switch (action) {
					case MotionEvent.ACTION_DOWN:
						// Disallow Drawer to intercept touch events.
						v.getParent().requestDisallowInterceptTouchEvent(true);
						break;

					case MotionEvent.ACTION_UP:
						// Allow Drawer to intercept touch events.
						v.getParent().requestDisallowInterceptTouchEvent(false);
						break;
					}

					// Handle seekbar touch events.
					v.onTouchEvent(event);
					return true;
				}
			});
			
			rangeBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					mRangeBarProgress = seekBar.getProgress() + 1;
					String s;
					s = Integer.toString(mRangeBarProgress);
					s += "Km";
					
					TextView tS = (TextView) itemView.findViewById(R.id.subtitle);
					tS.setText(s);
					
					Log.i(TAG, s);
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
		else
		{
			itemView = inflater.inflate(R.layout.drawer_list_item, parent,false);
		}
			

		// Locate the TextViews
		txtTitle = (TextView) itemView.findViewById(R.id.title);
		txtSubTitle = (TextView) itemView.findViewById(R.id.subtitle);
		//imgIcon = (ImageView) itemView.findViewById(R.id.imgIcon);

		// Set the data
		txtTitle.setText(mTitle[position]);
		if (txtSubTitle != null)
			txtSubTitle.setText(mSubTitle[position]);
		//imgIcon.setImageResource(R.drawable.ic_launcher);

		return itemView;

	}
	
	public int getBarProgress()
	{
		return mRangeBarProgress;
	}

}