package com.risewide.bdebugapp.communication.reader.projection;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import com.risewide.bdebugapp.communication.model.SmsMmsMsg;
import com.risewide.bdebugapp.communication.reader.MmsReaderSub;

/**
 * Created by birdea on 2017-08-09.
 */

public class MmsReadProject {

	public static class All extends ReadProjector<SmsMmsMsg> {

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
			return null;//"read!=1";;
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
		public SmsMmsMsg read(Context context, Cursor cursor) {
			SmsMmsMsg item = new SmsMmsMsg();
			item._id = cursor.getLong(idxColumn[0]);
			item.date = cursor.getLong(idxColumn[1]);
			item.read = cursor.getInt(idxColumn[2]);
			item.msg_box = cursor.getInt(idxColumn[3]);
			item.text_only = cursor.getInt(idxColumn[4]);
			item.mms_version = cursor.getInt(idxColumn[5]);
			item.msg_type = cursor.getInt(idxColumn[6]);
			item.subject = cursor.getString(idxColumn[7]);
			item.subject_charset = cursor.getInt(idxColumn[8]);
			//- handle in async
			item.listAddress = mmsReaderSub.getAddressNumber(context.getContentResolver(), (int) item._id);
			//item.body = mmsReaderSub.getTextMessage(context, String.valueOf(item._id));
				/*mmsReaderSub.getAddressNumberAsync(context, (int) item._id, new MmsReaderSub.OnReadListener() {
					@Override
					public void onRead(Object data) {
						item.listAddress = (List<String>) data;
					}
				});
				mmsReaderSub.getMessageOfMmsAsync(context, String.valueOf(item._id), new MmsReaderSub.OnReadListener() {
					@Override
					public void onRead(Object data) {
						item.body = (String) data;
					}
				});*/
			return item;
		}
	}


	public static class Inbox extends ReadProjector<SmsMmsMsg> {

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
		public SmsMmsMsg read(Context context, Cursor cursor) {
			return null;
		}
	}


	public static class Address extends ReadProjector<SmsMmsMsg> {
		@Override
		public String[] getProjection() {
			return new String[0];
		}

		@Override
		public String getSelection() {
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
		public SmsMmsMsg read(Context context, Cursor cursor) {
			return null;
		}
	}

}
