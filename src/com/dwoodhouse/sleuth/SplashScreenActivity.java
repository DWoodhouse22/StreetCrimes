package com.dwoodhouse.sleuth;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.dwoodhouse.streetcrimes.R;

public class SplashScreenActivity extends Activity implements Observer {

	private final String TAG = "SplashScreenActivity";
	private AnimationDrawable loadingAnim;
	private ImageView loadingView;
	private String dates;
	private Activity mActivity;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);	
		mActivity = this;
		setContentView(R.layout.splash_layout);
		
		loadingAnim = new AnimationDrawable();
		loadingView = (ImageView)findViewById(R.id.loading_image);
		for (int i = 0; i < 8; i++)
		{
			Drawable frame = getResources().getDrawable(getResources().getIdentifier("frame_00" + i, "drawable", getPackageName()));
			loadingAnim.addFrame(frame, 125);
		}
		loadingAnim.setOneShot(false);
		loadingView.setBackgroundDrawable(loadingAnim);
		
		ObservingService.getInstance().addObserver(Notification.RETRIEVED_DATES, this);
	}
	
	@Override
	protected void onStart()
	{
		new GetDatesTask().execute();
		loadingAnim.start();
		super.onStart();
	}
	
	private AlertDialog buildCheckConnectionDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Check your internet connection");
		builder.setMessage("An internet connection is required to run Sleuth, check your connection and try again.  WiFi recommended");
		
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int id) 
		    {
				finish();
		    }			     
		});
		
		return builder.create();
	}

	@Override
	public void update(Observable observable, Object data)
	{
		Notification pData = (Notification)data;
		
		// Retrieve the available dates first and use this to get the categories for each date
		if (pData.isNotificationType(Notification.RETRIEVED_DATES))	
		{
			dates = (String)pData.get("response");
			new Handler().postDelayed(new Runnable() 
			{
	            @Override
	            public void run() 
	            {
	            	//overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	            	if (Application.checkConnection(mActivity))
	            	{
	            		Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
		            	i.putExtra("dates", dates);
		            		
		            	loadingAnim.stop();
		            	startActivity(i);
	            	}
	            	else
	            	{
	            		buildCheckConnectionDialog().show();
	            	}
	            }
	        }, 1000);
		}
	}
}
