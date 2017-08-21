package com.risewide.bdebugapp.communication.reader.projection;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import com.risewide.bdebugapp.communication.util.CursorUtil;
import com.risewide.bdebugapp.communication.model.CommMsgData;
import com.risewide.bdebugapp.communication.util.IOCloser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by birdea on 2017-08-08.
 */

public class QuerySmsProject {

	public static class All extends AbsQueryProject<CommMsgData> {

		public static final String[] PROJECTION = {
				Telephony.Sms._ID,
				Telephony.Sms.ADDRESS,
				Telephony.Sms.DATE,
				Telephony.Sms.READ,
				Telephony.Sms.TYPE,
				Telephony.Sms.BODY,
		};

		@Override
		public String[] getProjection() {
			return PROJECTION;
		}

		@Override
		public String getSelection() {
			if (isLoadOnlyUnreadData) {
				return Telephony.Mms.READ+"!=?";
			}
			return null;
		}

		@Override
		public String[] getSelectionArgs() {
			if (isLoadOnlyUnreadData) {
				return new String[]{"1"};
			}
			return null;
		}

		@Override
		public Uri getUri() {
			return Telephony.Sms.CONTENT_URI;
		}


		@Override
		protected void storeProjectColumnIndex(Cursor cursor) {
		}

		@Override
		public List<CommMsgData> readAll(Context context) {
			ContentResolver resolver = context.getContentResolver();
			Cursor cursor = resolver.query(getUri(), getProjection(), getSelection(), getSelectionArgs(), sortOrder);

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

		@Override
		protected CommMsgData read(Context context, Cursor cursor) {
			CommMsgData item = new CommMsgData(CommMsgData.Type.SMS);
			item._id = CursorUtil.getLong(cursor, Telephony.Sms.Inbox._ID);
			item.address = CursorUtil.getString(cursor, Telephony.Sms.Inbox.ADDRESS);
			item.setDate(CursorUtil.getLong(cursor,Telephony.Sms.Inbox.DATE));
			item.read = CursorUtil.getInt(cursor,Telephony.Sms.Inbox.READ);
			item.type = CursorUtil.getInt(cursor,Telephony.Sms.Inbox.TYPE);
			item.body = CursorUtil.getString(cursor,Telephony.Sms.Inbox.BODY);
			return item;
		}
	}

	public static class Inbox extends AbsQueryProject<CommMsgData> {
		public static final String[] PROJECTION = {
				Telephony.Sms.Inbox._ID,
				Telephony.Sms.Inbox.THREAD_ID,
				Telephony.Sms.Inbox.ADDRESS,
				Telephony.Sms.Inbox.DATE,
				Telephony.Sms.Inbox.DATE_SENT,
				Telephony.Sms.Inbox.STATUS,
				Telephony.Sms.Inbox.TYPE,
				Telephony.Sms.Inbox.BODY,
				Telephony.Sms.Inbox.READ,
				Telephony.Sms.Inbox.SEEN,
		};

		private int idx_id, idx_address, idx_date, idx_read, idx_type, idx_body;
		@Override
		protected void storeProjectColumnIndex(Cursor cursor) {
			idx_id = cursor.getColumnIndex(Telephony.Sms.Inbox._ID);
			idx_address = cursor.getColumnIndex(Telephony.Sms.Inbox.ADDRESS);
			idx_date = cursor.getColumnIndex(Telephony.Sms.Inbox.DATE);
			idx_read = cursor.getColumnIndex(Telephony.Sms.Inbox.READ);
			idx_type = cursor.getColumnIndex(Telephony.Sms.Inbox.TYPE);
			idx_body = cursor.getColumnIndex(Telephony.Sms.Inbox.BODY);
		}
		@Override
		protected CommMsgData read(Context context, Cursor cursor) {
			CommMsgData item = new CommMsgData(CommMsgData.Type.SMS);
			item._id = CursorUtil.getLong(cursor, idx_id);
			item.address = CursorUtil.getString(cursor, idx_address);
			item.setDate(CursorUtil.getLong(cursor, idx_date));
			item.read = CursorUtil.getInt(cursor, idx_read);
			item.type = CursorUtil.getInt(cursor, idx_type);
			item.body = CursorUtil.getString(cursor, idx_body);
			return item;
		}

		@Override
		public String[] getProjection() {
			return PROJECTION;
		}

		@Override
		public String getSelection() {
			if (isLoadOnlyUnreadData) {
				return Telephony.Mms.READ+"!=?";
			}
			return null;
		}

		@Override
		public String[] getSelectionArgs() {
			if (isLoadOnlyUnreadData) {
				return new String[]{"1"};
			}
			return null;
		}

		@Override
		public Uri getUri() {
			return Telephony.Sms.Inbox.CONTENT_URI;
		}

		@Override
		public List<CommMsgData> readAll(Context context) {
			ContentResolver resolver = context.getContentResolver();
			Cursor cursor = resolver.query(getUri(), getProjection(), getSelection(), getSelectionArgs(), sortOrder);

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

	public static class Sent extends AbsQueryProject {
		public static final String[] PROJECTION = {
				Telephony.Sms.Sent._ID,
				Telephony.Sms.Sent.THREAD_ID,
				Telephony.Sms.Sent.ADDRESS,
				Telephony.Sms.Sent.DATE,
				Telephony.Sms.Sent.DATE_SENT,
				Telephony.Sms.Sent.STATUS,
				Telephony.Sms.Sent.TYPE,
				Telephony.Sms.Sent.BODY,
				Telephony.Sms.Sent.READ,
				Telephony.Sms.Sent.SEEN,
		};

		@Override
		protected void storeProjectColumnIndex(Cursor cursor) {

		}

		//[TODO] adjust method of 'storeProjectColumnIndex' to do faster
		@Override
		protected CommMsgData read(Context context, Cursor cursor) {
			CommMsgData item = new CommMsgData(CommMsgData.Type.SMS);
			item._id = CursorUtil.getLong(cursor, Telephony.Sms.Sent._ID);
			item.address = CursorUtil.getString(cursor, Telephony.Sms.Sent.ADDRESS);
			item.setDate(CursorUtil.getLong(cursor,Telephony.Sms.Sent.DATE));
			item.read = CursorUtil.getInt(cursor,Telephony.Sms.Sent.READ);
			item.type = CursorUtil.getInt(cursor,Telephony.Sms.Sent.TYPE);
			item.body = CursorUtil.getString(cursor,Telephony.Sms.Sent.BODY);
			return item;
		}

		@Override
		public String[] getProjection() {
			return PROJECTION;
		}

		@Override
		public String getSelection() {
			if (isLoadOnlyUnreadData) {
				return Telephony.Mms.READ+"!=?";
			}
			return null;
		}

		@Override
		public String[] getSelectionArgs() {
			if (isLoadOnlyUnreadData) {
				return new String[]{"1"};
			}
			return null;
		}

		@Override
		public Uri getUri() {
			return Telephony.Sms.Sent.CONTENT_URI;
		}

		@Override
		public List readAll(Context context) {
			ContentResolver resolver = context.getContentResolver();
			Cursor cursor = resolver.query(getUri(), getProjection(), getSelection(), getSelectionArgs(), sortOrder);

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