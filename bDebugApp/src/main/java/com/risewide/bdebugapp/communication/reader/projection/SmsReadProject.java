package com.risewide.bdebugapp.communication.reader.projection;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import com.risewide.bdebugapp.communication.util.CursorUtil;
import com.risewide.bdebugapp.communication.model.MmsSmsMsg;

/**
 * Created by birdea on 2017-08-08.
 */

public class SmsReadProject {

	public static class All extends ReadProjector<MmsSmsMsg> {
		private static final String[] PROJECTION = {
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
			if (isSelectLoadOnlyUnread) {
				return Telephony.Mms.READ+"!=?";
			}
			return null;
		}

		@Override
		public String[] getSelectionArgs() {
			if (isSelectLoadOnlyUnread) {
				return new String[]{"1"};
			}
			return null;
		}

		@Override
		public Uri getUri() {
			return Telephony.Sms.CONTENT_URI;
		}

		@Override
		public void storeColumnIndex(Cursor cursor) {
		}

		@Override
		public MmsSmsMsg read(Context context, Cursor cursor) {
			return null;
		}
	}

	public static class Inbox extends ReadProjector<MmsSmsMsg> {
		private static final String[] PROJECTION = {
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

		@Override
		public String[] getProjection() {
			return PROJECTION;
		}

		@Override
		public String getSelection() {
			if (isSelectLoadOnlyUnread) {
				return Telephony.Mms.READ+"!=?";
			}
			return null;
		}

		@Override
		public String[] getSelectionArgs() {
			if (isSelectLoadOnlyUnread) {
				return new String[]{"1"};
			}
			return null;
		}

		@Override
		public Uri getUri() {
			return Telephony.Sms.Inbox.CONTENT_URI;
		}

		@Override
		public void storeColumnIndex(Cursor cursor) {
		}

		@Override
		public MmsSmsMsg read(Context context, Cursor cursor) {
			MmsSmsMsg item = new MmsSmsMsg(MmsSmsMsg.Type.SMS);
			item._id = CursorUtil.getLong(cursor, Telephony.Sms.Inbox._ID);
			item.address = CursorUtil.getString(cursor, Telephony.Sms.Inbox.ADDRESS);
			item.setDate(CursorUtil.getLong(cursor,Telephony.Sms.Inbox.DATE));
			item.read = CursorUtil.getInt(cursor,Telephony.Sms.Inbox.READ);
			item.type = CursorUtil.getInt(cursor,Telephony.Sms.Inbox.TYPE);
			item.body = CursorUtil.getString(cursor,Telephony.Sms.Inbox.BODY);
			return item;
		}
	}

	public static class Sent extends ReadProjector {
		private static final String[] PROJECTION = {
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
		public String[] getProjection() {
			return PROJECTION;
		}

		@Override
		public String getSelection() {
			if (isSelectLoadOnlyUnread) {
				return Telephony.Mms.READ+"!=?";
			}
			return null;
		}

		@Override
		public String[] getSelectionArgs() {
			if (isSelectLoadOnlyUnread) {
				return new String[]{"1"};
			}
			return null;
		}

		@Override
		public Uri getUri() {
			return Telephony.Sms.Sent.CONTENT_URI;
		}

		@Override
		public void storeColumnIndex(Cursor cursor) {
		}

		@Override
		public MmsSmsMsg read(Context context, Cursor cursor) {
			MmsSmsMsg item = new MmsSmsMsg(MmsSmsMsg.Type.SMS);
			item._id = CursorUtil.getLong(cursor, Telephony.Sms.Sent._ID);
			item.address = CursorUtil.getString(cursor, Telephony.Sms.Sent.ADDRESS);
			item.setDate(CursorUtil.getLong(cursor,Telephony.Sms.Sent.DATE));
			item.read = CursorUtil.getInt(cursor,Telephony.Sms.Sent.READ);
			item.type = CursorUtil.getInt(cursor,Telephony.Sms.Sent.TYPE);
			item.body = CursorUtil.getString(cursor,Telephony.Sms.Sent.BODY);
			return item;
		}
	}
}
