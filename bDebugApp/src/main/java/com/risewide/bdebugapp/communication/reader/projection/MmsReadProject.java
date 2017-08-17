package com.risewide.bdebugapp.communication.reader.projection;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import com.risewide.bdebugapp.communication.model.MmsSmsMsg;
import com.risewide.bdebugapp.communication.reader.MmsReaderSub;

/**
 * Created by birdea on 2017-08-09.
 */

public class MmsReadProject {

	public static class All extends ReadProjector<MmsSmsMsg> {

		private static final String[] PROJECTION = {
				Telephony.Mms._ID,
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

		private MmsReaderSub mmsReaderSub = new MmsReaderSub();

		@Override
		public MmsSmsMsg read(final Context context, Cursor cursor) {
			final MmsSmsMsg item = new MmsSmsMsg(MmsSmsMsg.Type.MMS);
			int idx = 0;
			item._id = cursor.getLong(idxColumn[idx++]);
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
			if (isExtraLoadMessageData) {
				item.body = mmsReaderSub.getTextMessage(cr, String.valueOf(item._id));
			}
			return item;
		}
	}


	public static class Inbox extends ReadProjector<MmsSmsMsg> {

		private static final String[] PROJECTION = {
				Telephony.Mms.Inbox._ID,
				Telephony.Mms.Inbox.THREAD_ID,
				Telephony.Mms.Inbox.CREATOR,
				Telephony.Mms.Inbox.DATE,
				Telephony.Mms.Inbox.DATE_SENT,
				Telephony.Mms.Inbox.READ,
				Telephony.Mms.Inbox.STATUS,
				Telephony.Mms.Inbox.SUBJECT,
				Telephony.Mms.Inbox.LOCKED,
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
			return null;
		}

		@Override
		public void storeColumnIndex(Cursor cursor) {
		}

		@Override
		public MmsSmsMsg read(Context context, Cursor cursor) {
			return null;
		}
	}


	public static class Address extends ReadProjector<MmsSmsMsg> {
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
		public MmsSmsMsg read(Context context, Cursor cursor) {
			return null;
		}
	}

}
