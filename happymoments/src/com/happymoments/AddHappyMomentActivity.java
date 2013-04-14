package com.happymoments;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AddHappyMomentActivity extends Activity {

	private static final String TAG = AddHappyMomentActivity.class.getSimpleName();

	private static final int RETURN_FROM_ADD_PHOTO = 1;

	private HappyMomentsSQLiteOpenHelper helper;

	private File photoFile;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "++onCreate");
		setContentView(R.layout.add_happy_moment);

		helper = new HappyMomentsSQLiteOpenHelper(this);

		LayoutParams params = getWindow().getAttributes();
		params.width = LayoutParams.FILL_PARENT;
		getWindow().setAttributes(params);

		Button btnAddHappyMoment = (Button) findViewById(R.id.btn_add);
		btnAddHappyMoment.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TextView happyMomentView = (TextView) findViewById(R.id.happy_moment_text);
				String happyMoment = capitalize(happyMomentView.getText().toString());
				if (happyMoment != null) {
					if (helper.addHappyMoment(happyMoment) != null) {
						Toast.makeText(AddHappyMomentActivity.this, "Saved Happy Moment :)", Toast.LENGTH_SHORT).show();
						setResult(Activity.RESULT_OK);
					} else {
						Toast.makeText(AddHappyMomentActivity.this, "Could not save Happy Moment :(", Toast.LENGTH_SHORT).show();
					}
				}
				finish();
			}
		});

		Button btnAddPhoto = (Button) findViewById(R.id.btn_add_photo);
		btnAddPhoto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dispatchTakePictureIntent();
			}
		});
	}

	static String capitalize(String name) {
		if (name == null || name.trim().length() < 1) return null;
		name = name.trim();
		return Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

	private void dispatchTakePictureIntent() {
		try {
			photoFile = HappyMomentsFileManager.newPhotoFile();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "Could not create photo file", e);
			photoFile = null;
		}
		if (photoFile != null) {
			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
			startActivityForResult(takePictureIntent, RETURN_FROM_ADD_PHOTO);
		}
		else {
			new AlertDialog.Builder(this)
			.setTitle(R.string.title_unexpected_error)
			.setMessage(R.string.error_allocating_photo_file)
			.setCancelable(true)
			.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			})
			.show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "onActivityResult");
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case RETURN_FROM_ADD_PHOTO:
				Log.i(TAG, "OK take photo");
				handleSmallCameraPhoto(data);
				break;
			default:
				Log.i(TAG, "OK ???");
			}
		}
		else {
			switch (requestCode) {
			case RETURN_FROM_ADD_PHOTO:
				Log.i(TAG, "CANCEL add photo");
				if (photoFile != null && photoFile.isFile()) {
					photoFile.delete();
				}
				break;
			default:
				Log.i(TAG, "CANCEL ???");
			}
		}
	}

	private void handleSmallCameraPhoto(Intent intent) {
		if (photoFile != null && photoFile.isFile()) {
			// TODO save in database
			Log.d(TAG, "successfully saved photo: " + photoFile);
		}
		else {
			Log.e(TAG, "Something's wrong with the photo file: " + photoFile);
		}
	}

	@Override  
	protected void onDestroy() {
		Log.d(TAG, "++onDestroy");
		super.onDestroy();
		helper.close();
	}

}
