package com.happymoments;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class HappyMomentListActivity extends ListActivity {

	private static final String TAG = HappyMomentListActivity.class.getSimpleName();

	private HappyMomentsSQLiteOpenHelper helper;
	private Cursor cursor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "++onCreate");

		setContentView(R.layout.happymomentlist);

		initCursor();

		getListView().setOnItemClickListener(new MyOnItemClickListener());
		getListView().setOnItemLongClickListener(new MyOnItemLongClickListener());
	}

	private void initCursor() {
		helper = new HappyMomentsSQLiteOpenHelper(this);
		cursor = helper.getHappyMomentListCursor();
		startManagingCursor(cursor);
		ListAdapter adapter = new HappyMomentListAdapter(this, cursor);
		setListAdapter(adapter);
	}

	class MyOnItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent intent = new Intent();
			intent.putExtra(BaseColumns._ID, ((TextView)view.findViewById(R.id._ID)).getText());
			setResult(RESULT_OK, intent);
			finish();
		}
	}

	class MyOnItemLongClickListener implements OnItemLongClickListener {
		private void deleteHappyMoment(String happyMomentId) {
			if (helper.deleteHappyMoment(happyMomentId)) {
				// TODO
				//HappyMomentsFileManager.deleteHappyMomentPhotos(happyMomentId);
				cursor.requery();
				Toast.makeText(getBaseContext(), R.string.msg_happymoment_deleted, Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			final String happyMomentId = String.valueOf(arg3);
			new AlertDialog.Builder(HappyMomentListActivity.this)
			.setTitle(R.string.title_delete_happymoment)
			.setMessage(R.string.confirm_are_you_sure)
			.setCancelable(true)
			.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					deleteHappyMoment(happyMomentId);
				}
			})
			.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			})
			.show();
			return true;
		}
	}

	@Override  
	protected void onDestroy() {
		Log.d(TAG, "++onDestroy");
		super.onDestroy();
		stopManagingCursor(cursor);
		cursor.close();
		helper.close();
	}
}
