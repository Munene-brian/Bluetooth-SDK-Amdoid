package com.evolute.lilygendemoapp;

import com.evolute.lilydemo.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

public class Act_SplashScreen extends Activity {

	// Splash screen timer
	private static int SPLASH_TIME_OUT = 3000;
	ScaleAnimation scale;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		//ImageView img_view =(ImageView)findViewById(R.id.imgLogo);
		//scale=new ScaleAnimation( -2 ,2 ,-2 ,2);
		//scale.setDuration(500);
		//img_view.startAnimation(scale);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent i = new Intent(Act_SplashScreen.this, Act_Main.class);
				startActivity(i);
				finish();
			}
		}, SPLASH_TIME_OUT);
	}
}
