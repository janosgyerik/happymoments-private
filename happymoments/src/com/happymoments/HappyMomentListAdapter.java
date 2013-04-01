package com.happymoments;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class HappyMomentListAdapter extends CursorAdapter {

	private final LayoutInflater mLayoutInflater;
	private final int mIdIndex;
	private final int mTextIndex;

	public HappyMomentListAdapter(Context context, Cursor cursor) {
		super(context, cursor, true);

		mIdIndex = cursor.getColumnIndex("_id");
		mTextIndex = cursor.getColumnIndex("text");

		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView _ID = (TextView) view.findViewById(R.id._ID);
		_ID.setText(cursor.getString(mIdIndex));

		TextView text = (TextView) view.findViewById(R.id.text);
		text.setText(cursor.getString(mTextIndex));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return mLayoutInflater.inflate(R.layout.happymomentlist_item, null);
	}

}
