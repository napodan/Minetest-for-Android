package com.Minetest.Mobile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.Minetest.Mobile.FREE.R;
import com.kskkbys.rate.RateThisApp;

public class MainActivity extends Activity {
	Button button3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// init rate plugin
		RateThisApp.onStart(this);
		RateThisApp.showRateDialogIfNeeded(this);

		button3 = (Button) findViewById(R.id.button3);
		button3.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("http://minetest.net/"));
				startActivity(intent);
			}
		});
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