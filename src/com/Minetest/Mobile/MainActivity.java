package com.Minetest.Mobile;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.Minetest.Mobile.FREE.R;


public class MainActivity extends Activity {
	Button button3;
	private StartAppAd startAppAd = new StartAppAd(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	}


	public void About(View view) {
		Intent intent = new Intent(this, About.class);
		startActivity(intent);
	}

	public void start(View view) {
		Intent intent = new Intent(this, Start.class);
		startActivity(intent);
	}
}
