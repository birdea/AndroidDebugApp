package com.risewide.bdebugapp.communication.reader.projection;

import java.util.ArrayList;
import java.util.List;

import com.risewide.bdebugapp.communication.model.CommMsgData;
import com.risewide.bdebugapp.communication.util.CursorUtil;
import com.risewide.bdebugapp.communication.util.IOCloser;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

/**
 * Created by birdea on 2017-08-21.
 */

public class QueryCanonicalAddressProject {

	public static class CanonicalAddressProject extends AbsQueryProject<CommMsgData> {

		private static final String[] PROJECTION = {
				Telephony.CanonicalAddressesColumns._ID,
				Telephony.CanonicalAddressesColumns.ADDRESS,
		};
		private int idx_id, idx_address;
		@Override
		public void storeProjectColumnIndex(Cursor cursor) {
			idx_id = cursor.getColumnIndex(Telephony.CanonicalAddressesColumns._ID);
			idx_address = cursor.getColumnIndex(Telephony.CanonicalAddressesColumns.ADDRESS);
		}

		@Override
		protected CommMsgData read(Context context, Cursor cursor) {
			CommMsgData item = new CommMsgData(CommMsgData.Type.CONVERSATION);
			item._id = CursorUtil.getLong(cursor, idx_id);
			item.address = CursorUtil.getString(cursor, idx_address);
			return item;
		}

		@Override
		public String[] getProjection() {
			return PROJECTION;
		}

		@Override
		public String getSelection() {
			return null;
		}

		@Override
		public String[] getSelectionArgs() {
			return null;
		}

		@Override
		public Uri getUri() {
			return Uri.parse("content://mms-sms/canonical-addresses");
		}

		@Override
		public List<CommMsgData> readAll(Context context) {
			ContentResolver resolver = context.getContentResolver();
			Cursor cursor = resolver.query(getUri(), getProjection(), getSelection(), getSelectionArgs(), null);

			List<CommMsgData> list = new ArrayList<>();
			if (cursor != null && cursor.moveToFirst()) {
				storeProjectColumnIndex(cursor);
				do {
					CommMsgData item = read(context, cursor);
					list.add(item);
				} while (cursor.moveToNext());
			}
			IOCloser.close(cursor);
			return list;
		}
	}

}
