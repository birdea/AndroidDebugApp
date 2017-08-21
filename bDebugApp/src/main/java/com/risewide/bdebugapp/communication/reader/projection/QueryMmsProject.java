package com.risewide.bdebugapp.communication.reader.projection;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.text.TextUtils;

import com.risewide.bdebugapp.communication.model.CommMsgData;
import com.risewide.bdebugapp.communication.reader.helper.MmsReaderHelper;
import com.risewide.bdebugapp.communication.util.CursorUtil;
import com.risewide.bdebugapp.communication.util.IOCloser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by birdea on 2017-08-09.
 */

public class QueryMmsProject {

	public static class All extends AbsQueryProject<CommMsgData> {

		private MmsReaderHelper mmsReaderSub = new MmsReaderHelper();

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
		protected void storeProjectColumnIndex(Cursor cursor) {
			idx_id = cursor.getColumnIndex(Telephony.Mms._ID);
			idx_m_id = cursor.getColumnIndex(Telephony.Mms.MESSAGE_ID);
			idx_date = cursor.getColumnIndex(Telephony.Mms.DATE);
			idx_read = cursor.getColumnIndex(Telephony.Mms.READ);
			idx_msg_box = cursor.getColumnIndex(Telephony.Mms.MESSAGE_BOX);
			idx_text_only = cursor.getColumnIndex(Telephony.Mms.TEXT_ONLY);
			idx_mms_version = cursor.getColumnIndex(Telephony.Mms.MMS_VERSION);
			idx_msg_type = cursor.getColumnIndex(Telephony.Mms.MESSAGE_TYPE);
			idx_subject = cursor.getColumnIndex(Telephony.Mms.SUBJECT);
			idx_subject_charset = cursor.getColumnIndex(Telephony.Mms.SUBJECT_CHARSET);
			idx_body = cursor.getColumnIndex(Telephony.Mms.SUBJECT_CHARSET);
		}

		private int idx_id, idx_m_id, idx_date, idx_read, idx_msg_box, idx_text_only, idx_mms_version;
		private int idx_msg_type, idx_subject, idx_subject_charset, idx_body;
		@Override
		protected CommMsgData read(final Context context, Cursor cursor) {
			final CommMsgData item = new CommMsgData(CommMsgData.Type.MMS);
			item._id = CursorUtil.getLong(cursor, idx_id);
			item.m_id = CursorUtil.getString(cursor, idx_m_id);
			item.setDate(CursorUtil.getLong(cursor, idx_date));
			item.read = CursorUtil.getInt(cursor, idx_read);
			item.msg_box = CursorUtil.getInt(cursor, idx_msg_box);
			item.text_only = CursorUtil.getInt(cursor, idx_text_only);
			item.mms_version = CursorUtil.getInt(cursor, idx_mms_version);
			item.msg_type = CursorUtil.getInt(cursor, idx_msg_type);
			item.subject = CursorUtil.getString(cursor, idx_subject);
			item.subject_charset = CursorUtil.getInt(cursor, idx_subject_charset);
			//- handle in async
			ContentResolver cr = context.getContentResolver();
			if (isExtraLoadAddressData) {
				item.listAddress = mmsReaderSub.getAddressNumber(cr, (int) item._id);
			}
			if (TextUtils.isEmpty(item.m_id) || "null".equals(item.m_id)) {
				item.body = CursorUtil.getString(cursor, idx_body);
			} else {
				if (isExtraLoadMessageData) {
					String mid = ""+item._id;//mmsReaderSub.getMessageId(context.getContentResolver(), item.thread_id, item.m_id);
					item.body = mmsReaderSub.getTextMessage(context.getContentResolver(), mid);
				}
			}
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
			return Telephony.Mms.CONTENT_URI;
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


	public static class Inbox extends AbsQueryProject<CommMsgData> {

		private MmsReaderHelper mmsReaderSub = new MmsReaderHelper();

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

		private int idx_id, idx_m_id, idx_date, idx_read, idx_msg_box, idx_text_only, idx_mms_version;
		private int idx_msg_type, idx_subject, idx_subject_charset, idx_body;
		@Override
		protected void storeProjectColumnIndex(Cursor cursor) {
			idx_id = cursor.getColumnIndex(Telephony.Mms.Inbox._ID);
			idx_m_id = cursor.getColumnIndex(Telephony.Mms.Inbox.MESSAGE_ID);
			idx_date = cursor.getColumnIndex(Telephony.Mms.Inbox.DATE);
			idx_read = cursor.getColumnIndex(Telephony.Mms.Inbox.READ);
			idx_msg_box = cursor.getColumnIndex(Telephony.Mms.Inbox.MESSAGE_BOX);
			idx_text_only = cursor.getColumnIndex(Telephony.Mms.Inbox.TEXT_ONLY);
			idx_mms_version = cursor.getColumnIndex(Telephony.Mms.Inbox.MMS_VERSION);
			idx_msg_type = cursor.getColumnIndex(Telephony.Mms.Inbox.MESSAGE_TYPE);
			idx_subject = cursor.getColumnIndex(Telephony.Mms.Inbox.SUBJECT);
			idx_subject_charset = cursor.getColumnIndex(Telephony.Mms.Inbox.SUBJECT_CHARSET);
			idx_body = cursor.getColumnIndex("body");
		}

		@Override
		protected CommMsgData read(Context context, Cursor cursor) {
			final CommMsgData item = new CommMsgData(CommMsgData.Type.MMS);

			item._id = CursorUtil.getLong(cursor, idx_id);
			item.m_id = CursorUtil.getString(cursor, idx_m_id);
			item.setDate(CursorUtil.getLong(cursor, idx_date));
			item.read = CursorUtil.getInt(cursor, idx_read);
			item.msg_box = CursorUtil.getInt(cursor, idx_msg_box);
			item.text_only = CursorUtil.getInt(cursor, idx_text_only);
			item.mms_version = CursorUtil.getInt(cursor, idx_mms_version);
			item.msg_type = CursorUtil.getInt(cursor, idx_msg_type);
			item.subject = CursorUtil.getString(cursor, idx_subject);
			item.subject_charset = CursorUtil.getInt(cursor, idx_subject_charset);
			//- handle in async
			ContentResolver cr = context.getContentResolver();
			if (isExtraLoadAddressData) {
				item.listAddress = mmsReaderSub.getAddressNumber(cr, (int) item._id);
			}
			if (TextUtils.isEmpty(item.m_id) || "null".equals(item.m_id)) {
				item.body = CursorUtil.getString(cursor, idx_body);
			} else {
				if (isExtraLoadMessageData) {
					String mid = ""+item._id;//mmsReaderSub.getMessageId(context.getContentResolver(), item.thread_id, item.m_id);
					item.body = mmsReaderSub.getTextMessage(context.getContentResolver(), mid);
				}
			}
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
			return Telephony.Mms.Inbox.CONTENT_URI;
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
}



