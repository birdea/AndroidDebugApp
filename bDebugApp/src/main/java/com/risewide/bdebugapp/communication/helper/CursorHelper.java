package com.risewide.bdebugapp.communication.helper;

import android.database.Cursor;

import com.risewide.bdebugapp.util.SVLog;

/**
 * Created by birdea on 2017-08-08.
 */

public class CursorHelper {

	private static final String TAG = CursorHelper.class.getSimpleName();

	public static int getInt(Cursor cursor, String colName) {
		int val = -1;
		try {
			val = cursor.getInt(cursor.getColumnIndex(colName));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return val;
	}

	public static long getLong(Cursor cursor, String colName) {
		long val = -1;
		try {
			val = cursor.getLong(cursor.getColumnIndex(colName));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return val;
	}

	public static String getString(Cursor cursor, String colName) {
		String val = null;
		try {
			val = cursor.getString(cursor.getColumnIndex(colName));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return val;
	}

	public static void printOutCursorInfo(Cursor cursor) {
		if (cursor == null) {
			SVLog.d(TAG, "cursor == null");
		}

		String[] columnNames = cursor.getColumnNames();
		StringBuilder sb = new StringBuilder();
		int length = columnNames.length;
		for (int i=0;i<length;i++) {
			sb.append("[").append(i).append("]:")
					.append(columnNames[i])
					.append(",");
		}
		SVLog.d("*row : "+sb.toString());
		if (cursor.moveToFirst()) {
			while (cursor.moveToNext()) {
				sb.setLength(0);
				for (int i=0;i<length;i++) {
					sb.append("[").append(i).append("]:")
							.append(getCursorValue(cursor, i))
							.append(",");
				}
				SVLog.d("*row : "+sb.toString());
			}
		}
		if (cursor != null) {
			cursor.close();
		}
	}

	private static Object getCursorValue(Cursor cursor, int index) {
		int type = cursor.getType(index);
		switch (type) {
			case Cursor.FIELD_TYPE_BLOB:
				return cursor.getBlob(index);
			case Cursor.FIELD_TYPE_FLOAT:
				return cursor.getFloat(index);
			case Cursor.FIELD_TYPE_INTEGER:
				return cursor.getInt(index);
			case Cursor.FIELD_TYPE_NULL:
				return null;
			case Cursor.FIELD_TYPE_STRING:
				return cursor.getString(index);
			default:
				return cursor.getString(index);
		}
	}
}
