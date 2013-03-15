package com.happymoments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getSimpleName();

	public static final int RETURN_FROM_ADD_HAPPY_MOMENT = 1;

	private HappyMomentsSQLiteOpenHelper helper;

	private ImageButton happinessJarButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "++onCreate");
		setContentView(R.layout.activity_main);
		
		if (((MainApplication)this.getApplication()).isLiteVersion()) {
			// TODO
			// ... impose limitations of the lite version ...
		}

		helper = new HappyMomentsSQLiteOpenHelper(this);

		happinessJarButton = (ImageButton) findViewById(R.id.happiness_jar);
		happinessJarButton.setOnClickListener(new HappinessJarClickListener());
	}

	class HappinessJarClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent(MainActivity.this, AddHappyMomentActivity.class);
			startActivityForResult(intent, RETURN_FROM_ADD_HAPPY_MOMENT);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override  
	protected void onDestroy() {
		Log.d(TAG, "++onDestroy");
		super.onDestroy();
		helper.close();
	}

}
