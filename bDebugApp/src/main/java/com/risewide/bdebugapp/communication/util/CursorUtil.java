package com.risewide.bdebugapp.communication.util;

import com.risewide.bdebugapp.util.SVLog;

import android.database.Cursor;

/**
 * Created by birdea on 2017-08-08.
 */

public class CursorUtil {

	private static final String TAG = CursorUtil.class.getSimpleName();

	public static int getInt(Cursor cursor, String colName) {
		int val = Integer.MIN_VALUE;
		try {
			val = cursor.getInt(cursor.getColumnIndex(colName));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return val;
	}
	public static int getInt(Cursor cursor, int index) {
		int val = Integer.MIN_VALUE;
		try {
			val = cursor.getInt(index);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return val;
	}

	public static long getLong(Cursor cursor, String colName) {
		long val = Long.MIN_VALUE;
		try {
			val = cursor.getLong(cursor.getColumnIndex(colName));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return val;
	}
	public static long getLong(Cursor cursor, int index) {
		long val = Long.MIN_VALUE;
		try {
			val = cursor.getLong(index);
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

	public static String getString(Cursor cursor, int index) {
		String val = null;
		try {
			val = cursor.getString(index);
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
