package com.risewide.bdebugapp.communication.reader.projection;

import com.risewide.bdebugapp.communication.model.MessageItem;
import com.risewide.bdebugapp.communication.reader.MmsReaderSub;
import com.risewide.bdebugapp.util.SVLog;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import java.util.List;

/**
 * Created by birdea on 2017-08-09.
 */

public class MmsReadProject {

	public static class All extends ReadProjector<MessageItem> {

		private static final String[] PROJECTION_MMS = {
				Telephony.Mms._ID,
				Telephony.Mms.CREATOR,
				//Telephony.Mms.Addr.ADDRESS,//Telephony.Mms.ADDRESS,
				Telephony.Mms.DATE,
				Telephony.Mms.DATE_SENT,
				//Telephony.Mms.PROTOCOL,
				//Telephony.Mms.ERROR_CODE,
				Telephony.Mms.READ,
				Telephony.Mms.STATUS,
				//Telephony.Mms.TYPE,
				Telephony.Mms.SUBJECT,
				//Telephony.Mms.Inbox.BODY,
				//Telephony.Mms.SERVICE_CENTER,
				Telephony.Mms.LOCKED,
				//Telephony.Mms.MMS_VERSION,
		};

		@Override
		public String[] getProjection() {
			return PROJECTION_MMS;
		}

		@Override
		public Uri getUri() {
			return Telephony.Mms.CONTENT_URI;
		}

		@Override
		public List query(Context context) {
			return null;
		}

		private MmsReaderSub mmsReaderSub = new MmsReaderSub();

		private int version = -9999;

		@Override
		public MessageItem read(Context context, Cursor cursor) {
			final MessageItem item = new MessageItem();
			item.id = cursor.getLong(cursor.getColumnIndex(Telephony.Mms._ID));
			item.creator = cursor.getString(cursor.getColumnIndex(Telephony.Mms.CREATOR));
			//item.person = cursor.getInt(cursor.getColumnIndex(Telephony.Mms.PERSON));
			item.date = cursor.getLong(cursor.getColumnIndex(Telephony.Mms.DATE)) * 1000;
			item.dateSent = cursor.getLong(cursor.getColumnIndex(Telephony.Mms.DATE_SENT));
			//item.protocol = cursor.getInt(cursor.getColumnIndex(Telephony.Mms.PROTOCOL));
			//item.errorCode = cursor.getInt(cursor.getColumnIndex(Telephony.Mms.ERROR_CODE));
			item.read = cursor.getInt(cursor.getColumnIndex(Telephony.Mms.READ));
			item.status = cursor.getInt(cursor.getColumnIndex(Telephony.Mms.STATUS));
			//item.type = cursor.getInt(cursor.getColumnIndex(Telephony.Mms.TYPE));
			item.subject = cursor.getString(cursor.getColumnIndex(Telephony.Mms.SUBJECT));
			//item.serviceCenter = cursor.getString(cursor.getColumnIndex(Telephony.Mms.SERVICE_CENTER));
			item.locked = cursor.getInt(cursor.getColumnIndex(Telephony.Mms.LOCKED));
			//if (version == -9999) {
			//	version = cursor.getInt(cursor.getColumnIndex(Telephony.Mms.MMS_VERSION));
			//	SVLog.d("mms version:"+version);
			//}
			//- handle in async
			//item.listAddress = mmsReaderSub.getAddressNumber(context, (int) item.id);
			//item.body = mmsReaderSub.getTextMessage(context, String.valueOf(item.id));
			/*mmsReaderSub.getAddressNumberAsync(context, (int) item.id, new MmsReaderSub.OnReadListener() {
				@Override
				public void onRead(Object data) {
					item.listAddress = (List<String>) data;
				}
			});
			mmsReaderSub.getMessageOfMmsAsync(context, String.valueOf(item.id), new MmsReaderSub.OnReadListener() {
				@Override
				public void onRead(Object data) {
					item.body = (String) data;
				}
			});*/
			return item;
		}
	}


	public static class Inbox extends ReadProjector<MessageItem> {

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
		public Uri getUri() {
			return null;
		}

		@Override
		public List query(Context context) {
			return null;
		}

		@Override
		public MessageItem read(Context context, Cursor cursor) {
			return null;
		}
	}


	public static class Address extends ReadProjector<MessageItem> {

		@Override
		public String[] getProjection() {
			return new String[0];
		}

		@Override
		public Uri getUri() {
			return null;
		}

		@Override
		public List query(Context context) {
			return null;
		}

		@Override
		public MessageItem read(Context context, Cursor cursor) {
			/*Uri uri = Uri.parse("content://mms-sms/conversations/" + mThreadId);
			String[] projection = new String[] { "body", "person", "sub",
					"subject", "retr_st", "type", "date", "ct_cls", "sub_cs",
					"_id", "read", "ct_l", "st", "msg_box", "reply_path_present",
					"m_cls", "read_status", "ct_t", "status", "retr_txt_cs",
					"d_rpt", "error_code", "m_id", "date_sent", "m_type", "v",
					"exp", "pri", "service_center", "address", "rr", "rpt_a",
					"resp_txt", "locked", "resp_st", "m_size" };
			String sortOrder = "normalized_date";

			Cursor mCursor = getActivity().getContentResolver().query(uri,projection, null, null, sortOrder);

			String messageAddress;
			int type;
			while (mCursor.moveToNext()) {
				String messageId = mCursor.getString(mCursor.getColumnIndex("_id"));

				Uri.Builder builder = Uri.parse("content://mms").buildUpon();
				builder.appendPath(messageId).appendPath("addr");
				Cursor c = mContext.getContentResolver().query(builder.build(), new String[] {
						"*"
				}, null, null, null);
				while (c.moveToNext()) {
					messageAddress = c.getString(c.getColumnIndex("address"));

					if (!messageAddress.equals("insert-address-token")) {
						type = c.getInt(c.getColumnIndex("type"));
						c.moveToLast();
					}
				}
				c.close();
			}*/
			return null;
		}
	}

}
