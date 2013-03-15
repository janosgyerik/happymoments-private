package com.happymoments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AddHappyMomentActivity extends Activity {

	private static final String TAG = AddHappyMomentActivity.class.getSimpleName();

	private HappyMomentsSQLiteOpenHelper helper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "+++ ON CREATE +++");
		setContentView(R.layout.add_happy_moment);

		helper = new HappyMomentsSQLiteOpenHelper(this);

		LayoutParams params = getWindow().getAttributes();
		params.width = LayoutParams.FILL_PARENT;
		getWindow().setAttributes(params);

		Button btnAddHappyMoment = (Button) findViewById(R.id.btn_add);
		btnAddHappyMoment.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TextView happyMomentView = (TextView) findViewById(R.id.happy_moment_text);
				String happyMoment = happyMomentView.getText().toString().trim();
				if (happyMoment.equals("")) {
					finish();
				}
				else {
					if (helper.addHappyMoment(happyMoment)) {
						Toast.makeText(AddHappyMomentActivity.this, "Saved Happy Moment :)", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(AddHappyMomentActivity.this, "Could not save Happy Moment :(", Toast.LENGTH_SHORT).show();
					}
				}
				finish();
			}
		});
	}

	@Override  
	protected void onDestroy() {
		Log.d(TAG, "++onDestroy");
		super.onDestroy();
		helper.close();
	}

}
