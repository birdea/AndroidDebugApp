package com.risewide.bdebugapp.communication.helper;

import android.database.Cursor;

/**
 * Created by birdea on 2017-08-08.
 */

public class CursorHelper {

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

}
