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
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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

	/*
	 * 
	public List<HappyMoment> getQuestions() {
		List<HappyMoment> questions = new ArrayList<IQuestion>();

		Map<String, QuestionData> questionDataMap = new HashMap<String, QuestionData>();

		{
			Cursor cursor = getQuestionListCursor();
			final int idIndex = cursor.getColumnIndex(BaseColumns._ID);
			final int textIndex = cursor.getColumnIndex("text");
			final int categoryIndex = cursor.getColumnIndex("category");
			final int explanationIndex = cursor.getColumnIndex("explanation");
			while (cursor.moveToNext()) {
				QuestionData data = new QuestionData();
				String id = cursor.getString(idIndex);
				data.setId(id);
				data.setText(cursor.getString(textIndex));
				data.setCategory(cursor.getString(categoryIndex));
				data.setExplanation(cursor.getString(explanationIndex));
				questionDataMap.put(id, data);
			}
			cursor.close();
		}

		{
			Cursor cursor = getAnswerListCursor();
			final int questionIdIndex = cursor.getColumnIndex("question_id");
			final int textIndex = cursor.getColumnIndex("text");
			final int isCorrectIndex = cursor.getColumnIndex("is_correct");
			while (cursor.moveToNext()) {
				String questionId = cursor.getString(questionIdIndex);
				String answer = cursor.getString(textIndex);
				int isCorrect = cursor.getInt(isCorrectIndex);
				QuestionData data = questionDataMap.get(questionId);
				data.addAnswer(answer, isCorrect > 0);
			}
			cursor.close();
		}

		for (QuestionData data : questionDataMap.values()) {
			IQuestion question = new Question(data.category, data.text,
					data.explanation, data.correctAnswer, data.choices);
			questions.add(question);
		}

		return questions;
	}
	 */
}
