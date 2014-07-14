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
	private Context mContext;
	private String[] mTitle;
	private String[] mSubTitle;
	private LayoutInflater mInflater;
	private int mRangeBarProgress;

	public MenuListAdapter(Context context, String[] title, String[] subtitle) {
		mContext = context;
		mTitle = title;
		mSubTitle = subtitle;
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

		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		final View itemView;
		if (mTitle[position].equals("Search Range")) {
			itemView = mInflater.inflate(R.layout.drawer_seekbar_layout, parent,
					false);
			rangeBar = (SeekBar) itemView.findViewById(R.id.range_bar);
			
			
			
			
		} 
		else
		{
			itemView = mInflater.inflate(R.layout.drawer_list_item, parent,false);
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