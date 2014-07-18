package com.dwoodhouse.sleuth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreenActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            {
            	//overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            	Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
            	startActivity(i);
            }
        }, 3000); 
	}
}
