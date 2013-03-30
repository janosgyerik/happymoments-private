package com.happymoments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import android.util.SparseArray;

public class HappyMomentsSQLiteOpenHelper extends SQLiteOpenHelper {

	private static final String TAG = HappyMomentsSQLiteOpenHelper.class
			.getSimpleName();

	private static final String DATABASE_NAME = "sqlite3.db";
	private static final int DATABASE_VERSION = 1;

	private static final String HAPPYMOMENTS_TABLE_NAME = "happymoments_happymoment";

	private List<String> sqlCreateStatements;
	private SparseArray<List<String>> sqlUpgradeStatements;

	HappyMomentsSQLiteOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		// context.deleteDatabase(DATABASE_NAME);

		sqlCreateStatements = getSqlStatements(context, "sql_create.sql");
		sqlUpgradeStatements = new SparseArray<List<String>>();
		// sqlUpgradeStatements.put(2, getSqlStatements(context,
		// "sql_upgrade2.sql"));
	}

	private List<String> getSqlStatements(Context context, String assetName) {
		List<String> statements;
		try {
			statements = readSqlStatements(context, assetName);
		} catch (IOException e) {
			statements = Collections.emptyList();
			e.printStackTrace();
		}
		return statements;
	}

	static List<String> readSqlStatements(Context context, String assetName)
			throws IOException {
		List<String> statements = new ArrayList<String>();
		InputStream stream = context.getAssets().open(assetName);
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(stream));
		String line;
		StringBuilder builder = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			builder.append(line);
			if (line.trim().endsWith(";")) {
				statements.add(builder.toString());
				builder = new StringBuilder();
			}
		}
		return statements;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for (String sql : sqlCreateStatements) {
			db.execSQL(sql);
			// TODO check success
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		for (int i = oldVersion; i < newVersion; ++i) {
			upgradeToVersion(db, i + 1);
		}
	}

	private void upgradeToVersion(SQLiteDatabase db, int version) {
		Log.d(TAG, "upgrade to version " + version);
		for (String sql : sqlUpgradeStatements.get(version)) {
			db.execSQL(sql);
		}
	}

	public String addHappyMoment(String happyMoment) {
		ContentValues values = new ContentValues();
		values.put("text", happyMoment);
		long createdDt = new Date().getTime();
		values.put("created_dt", createdDt);
		values.put("updated_dt", createdDt);

		long ret = getWritableDatabase().insert(
				HAPPYMOMENTS_TABLE_NAME, null, values);
		Log.d(TAG, "insert happy moment ret = " + ret);
		if (ret >= 0) {
			String happyMomentId = String.valueOf(ret);
			return happyMomentId;
		} else {
			return null;
		}
	}

	public List<HappyMoment> getHappyMoments() {
		List<HappyMoment> happyMoments = new ArrayList<HappyMoment>();

		Cursor cursor = getHappyMomentsListCursor();
		final int idIndex = cursor.getColumnIndex(BaseColumns._ID);
		final int textIndex = cursor.getColumnIndex("text");
		final int filenameIndex = cursor.getColumnIndex("filename");
		final int colorIndex = cursor.getColumnIndex("color");
		final int createdDateIndex = cursor.getColumnIndex("created_dt");
		
		while (cursor.moveToNext()) {
			HappyMoment happyMoment = new HappyMoment();
			String id = cursor.getString(idIndex);
			happyMoment.setId(id);
			happyMoment.setText(cursor.getString(textIndex));
			happyMoment.setFilename(cursor.getString(filenameIndex));
			happyMoment.setColor(cursor.getString(colorIndex));
			happyMoment.setCreatedDate(cursor.getLong(createdDateIndex));
			happyMoments.add(happyMoment);
			Log.d(TAG, happyMoment.toString());
		}
		cursor.close();

		return happyMoments;
	}
	
	public Cursor getHappyMomentsListCursor() {
		Log.d(TAG, "get all happy moments");
		Cursor cursor = getReadableDatabase().rawQuery(
				"select _id, text, filename, color, created_dt "
						+ " from happymoments_happymoment "
						+ " order by updated_dt desc", null);
		Log.d(TAG, "get all happy moments -> " + cursor.getCount());
		return cursor;
	}
}
