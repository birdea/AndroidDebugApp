package com.risewide.bdebugapp.communication.reader.projection;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.text.TextUtils;

import com.risewide.bdebugapp.communication.model.CommMsgData;
import com.risewide.bdebugapp.communication.reader.helper.MmsReaderHelper;

/**
 * Created by birdea on 2017-08-09.
 */

public class QueryMmsProject {

	public static class All extends AbsQueryProject<CommMsgData> {

		private static final String[] PROJECTION = {
				Telephony.Mms._ID,
				Telephony.Mms.MESSAGE_ID,
				Telephony.Mms.DATE,
				Telephony.Mms.READ,
				Telephony.Mms.MESSAGE_BOX,
				Telephony.Mms.TEXT_ONLY,
				Telephony.Mms.MMS_VERSION,
				Telephony.Mms.MESSAGE_TYPE,
				Telephony.Mms.SUBJECT,
				Telephony.Mms.SUBJECT_CHARSET,
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
			return Telephony.Mms.CONTENT_URI;
		}

		@Override
		public void storeColumnIndex(Cursor cursor) {
			int count = cursor.getColumnCount();
			idxColumn = new int[count];
			for (int i=0;i<count;i++) {
				idxColumn[i] = cursor.getColumnIndex(PROJECTION[i]);
			}
		}

		private MmsReaderHelper mmsReaderSub = new MmsReaderHelper();

		@Override
		public CommMsgData read(final Context context, Cursor cursor) {
			final CommMsgData item = new CommMsgData(CommMsgData.Type.MMS);
			int idx = 0;
			item._id = cursor.getLong(idxColumn[idx++]);
			item.m_id = cursor.getString(idxColumn[idx++]);
			item.setDate(cursor.getLong(idxColumn[idx++]));
			item.read = cursor.getInt(idxColumn[idx++]);
			item.msg_box = cursor.getInt(idxColumn[idx++]);
			item.text_only = cursor.getInt(idxColumn[idx++]);
			item.mms_version = cursor.getInt(idxColumn[idx++]);
			item.msg_type = cursor.getInt(idxColumn[idx++]);
			item.subject = cursor.getString(idxColumn[idx++]);
			item.subject_charset = cursor.getInt(idxColumn[idx++]);
			//- handle in async
			ContentResolver cr = context.getContentResolver();
			if (isExtraLoadAddressData) {
				item.listAddress = mmsReaderSub.getAddressNumber(cr, (int) item._id);
			}
			if (TextUtils.isEmpty(item.m_id) || "null".equals(item.m_id)) {
				item.body = cursor.getString(cursor.getColumnIndex("body"));
			} else {
				if (isExtraLoadMessageData) {
					String mid = ""+item._id;//mmsReaderSub.getMessageId(context.getContentResolver(), item.thread_id, item.m_id);
					item.body = mmsReaderSub.getTextMessage(context.getContentResolver(), mid);
				}
			}
			return item;
		}
	}


	public static class Inbox extends AbsQueryProject<CommMsgData> {

		private static final String[] PROJECTION = {
				Telephony.Mms.Inbox._ID,
				Telephony.Mms.Inbox.MESSAGE_ID,
				Telephony.Mms.Inbox.DATE,
				Telephony.Mms.Inbox.READ,
				Telephony.Mms.Inbox.MESSAGE_BOX,
				Telephony.Mms.Inbox.TEXT_ONLY,
				Telephony.Mms.Inbox.MMS_VERSION,
				Telephony.Mms.Inbox.MESSAGE_TYPE,
				Telephony.Mms.Inbox.SUBJECT,
				Telephony.Mms.Inbox.SUBJECT_CHARSET,
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
			return Telephony.Mms.Inbox.CONTENT_URI;
		}

		@Override
		public void storeColumnIndex(Cursor cursor) {
			int count = cursor.getColumnCount();
			idxColumn = new int[count];
			for (int i=0;i<count;i++) {
				idxColumn[i] = cursor.getColumnIndex(PROJECTION[i]);
			}
		}

		private MmsReaderHelper mmsReaderSub = new MmsReaderHelper();

		@Override
		public CommMsgData read(Context context, Cursor cursor) {
			final CommMsgData item = new CommMsgData(CommMsgData.Type.MMS);
			int idx = 0;
			item._id = cursor.getLong(idxColumn[idx++]);
			item.m_id = cursor.getString(idxColumn[idx++]);
			item.setDate(cursor.getLong(idxColumn[idx++]));
			item.read = cursor.getInt(idxColumn[idx++]);
			item.msg_box = cursor.getInt(idxColumn[idx++]);
			item.text_only = cursor.getInt(idxColumn[idx++]);
			item.mms_version = cursor.getInt(idxColumn[idx++]);
			item.msg_type = cursor.getInt(idxColumn[idx++]);
			item.subject = cursor.getString(idxColumn[idx++]);
			item.subject_charset = cursor.getInt(idxColumn[idx++]);
			//- handle in async
			ContentResolver cr = context.getContentResolver();
			if (isExtraLoadAddressData) {
				item.listAddress = mmsReaderSub.getAddressNumber(cr, (int) item._id);
			}
			if (TextUtils.isEmpty(item.m_id) || "null".equals(item.m_id)) {
				item.body = cursor.getString(cursor.getColumnIndex("body"));
			} else {
				if (isExtraLoadMessageData) {
					String mid = ""+item._id;//mmsReaderSub.getMessageId(context.getContentResolver(), item.thread_id, item.m_id);
					item.body = mmsReaderSub.getTextMessage(context.getContentResolver(), mid);
				}
			}
			return item;
		}
	}


	public static class Address extends AbsQueryProject<CommMsgData> {
		@Override
		public String[] getProjection() {
			return new String[0];
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
			return null;
		}

		@Override
		public void storeColumnIndex(Cursor cursor) {
		}

		@Override
		public CommMsgData read(Context context, Cursor cursor) {
			return null;
		}
	}

}
