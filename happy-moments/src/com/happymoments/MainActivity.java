package com.happymoments;

import java.io.IOException;

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
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getSimpleName();

	private static final int RETURN_FROM_ADD_HAPPY_MOMENT = 1;
	private static final int FILE_SELECTED = 2;

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
			Log.d(TAG, "LITE version");
		}
		boolean success = false;
		try {
			success = HappyMomentsFileManager.backupDatabaseFile(getPackageName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (success) {
			Log.d(TAG, "SUCCESS!");
			Toast.makeText(getBaseContext(), R.string.msg_backup_created, Toast.LENGTH_LONG).show();
		}
		else {
			Log.d(TAG, "FAILURE!");
			Toast.makeText(getBaseContext(), R.string.error_backup_failed, Toast.LENGTH_LONG).show();
		}

		helper = new HappyMomentsSQLiteOpenHelper(this);
		helper.getHappyMoments(); 

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

	@Override  
	protected void onDestroy() {
		Log.d(TAG, "++onDestroy");
		super.onDestroy();
		helper.close();
	}

}
