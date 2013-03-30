package com.happymoments;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getSimpleName();

	private static final int RETURN_FROM_ADD_HAPPY_MOMENT = 1;
	private static final int FILE_SELECTED = 2;

	private static final int[] BGIMAGES = new int[] {
		R.drawable.bg01,
		R.drawable.bg02,
		R.drawable.bg03,
		R.drawable.bg04,
		R.drawable.bg05,
		R.drawable.bg06,
		R.drawable.bg07,
		R.drawable.bg08,
		R.drawable.bg09,
		R.drawable.bg10,
		R.drawable.bg11,
		R.drawable.bg12,
		R.drawable.bg13,
		R.drawable.bg14,
		R.drawable.bg15,
		R.drawable.bg16,
		R.drawable.bg17,
		R.drawable.bg18,
		R.drawable.bg19,
		R.drawable.bg20,
	};
	private static final Random random = new Random();

	private HappyMomentsSQLiteOpenHelper helper;

	private ImageButton happinessJarButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "++onCreate");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);

		if (((MainApplication)this.getApplication()).isLiteVersion()) {
			// TODO
			// ... impose limitations of the lite version ...
			Log.d(TAG, "LITE version");
		}

		helper = new HappyMomentsSQLiteOpenHelper(this);
		List<HappyMoment> happyMoments = helper.getHappyMoments();
		if (!happyMoments.isEmpty()) {
			Collections.shuffle(happyMoments);
			HappyMoment happyMoment = happyMoments.iterator().next();
			TextView happyMomentView = (TextView) findViewById(R.id.happy_moment);
			happyMomentView.setText(
					String.format("\"%s\"", happyMoment.getText()));
			TextView happyMomentDateView = (TextView) findViewById(R.id.happy_moment_date);
			happyMomentDateView.setText(
					String.format("%s", happyMoment.getCreatedDate().toLocaleString()));
		}

		ImageView imageView = (ImageView) findViewById(R.id.happiness_jar);
		int resId = BGIMAGES[random.nextInt(BGIMAGES.length)];
		imageView.setImageResource(resId);

		happinessJarButton = (ImageButton) findViewById(R.id.add_happy_moment);
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
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.menu_backup) {
			try {
				boolean success = HappyMomentsFileManager.backupDatabaseFile(getPackageName());
				if (success) {
					Toast.makeText(getBaseContext(), R.string.msg_backup_created, Toast.LENGTH_LONG).show();
				}
				else {
					Toast.makeText(getBaseContext(), R.string.error_backup_failed, Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				Toast.makeText(getBaseContext(), R.string.error_backup_exception, Toast.LENGTH_LONG).show();
				Log.e(TAG, "Exception in backupDatabaseFile", e);
			}
			return true;
		}
		if (itemId == R.id.menu_restore) {
			Intent intent = new Intent(this, FileSelectorActivity.class);
			intent.putExtra(FileSelectorActivity.IN_TITLE, getString(R.string.title_select_backupfile));
			intent.putExtra(FileSelectorActivity.IN_DIRPARAM, HappyMomentsFileManager.BACKUPS_DIRPARAM);
			intent.putExtra(FileSelectorActivity.IN_PATTERN, HappyMomentsFileManager.BACKUPFILES_PATTERN);
			intent.putExtra(FileSelectorActivity.IN_ORDER, FileSelectorActivity.ORDER_ZYX);
			intent.putExtra(FileSelectorActivity.IN_CONFIRMATION_TITLE, getString(R.string.title_confirm_restore));
			intent.putExtra(FileSelectorActivity.IN_CONFIRMATION_MESSAGE, getString(R.string.confirm_restore));
			startActivityForResult(intent, FILE_SELECTED);
			return true;
		}
		if (itemId == R.id.menu_quit) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.msg_quit)
			.setCancelable(true)
			.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					finish();
				}
			})
			.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			}).show();
			return true;
		}
		return false;
	}

	private void handleRestoreDatabaseResult(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			String filename = extras.getString(FileSelectorActivity.OUT_FILENAME);
			Log.d(TAG, "selected filename = " + filename);
			if (filename != null) {
				helper.close();
				try {
					if (HappyMomentsFileManager.restoreDatabaseFile(filename, getPackageName())) {
						Toast.makeText(getBaseContext(), R.string.msg_restore_success, Toast.LENGTH_LONG).show();
					}
					else {
						Toast.makeText(getBaseContext(), R.string.error_restore_failed, Toast.LENGTH_LONG).show();
					}
				} catch (IOException e) {
					e.printStackTrace();
					Toast.makeText(getBaseContext(), R.string.error_restore_exception, Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case FILE_SELECTED:
				handleRestoreDatabaseResult(data);
				break;
			}
		}
	}

	@Override  
	protected void onDestroy() {
		Log.d(TAG, "++onDestroy");
		super.onDestroy();
		helper.close();
	}

}
