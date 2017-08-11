package com.risewide.bdebugapp.communication.reader.projection;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import com.risewide.bdebugapp.communication.model.MessageItem;
import com.risewide.bdebugapp.communication.reader.MmsReaderSub;

import java.util.List;

/**
 * Created by birdea on 2017-08-09.
 */

public class MmsSmsReadProject {

	public static class All extends ReadProjector<MessageItem> {

		private static final String[] PROJECTION = {
				"*"
//				Telephony.MmsSms._ID,
//				Telephony.MmsSms.CREATOR,
//				Telephony.Mms.Addr.ADDRESS,//Telephony.Mms.ADDRESS,
//				Telephony.MmsSms.DATE,
//				Telephony.MmsSms.DATE_SENT,
//				Telephony.Mms.PROTOCOL,
//				Telephony.Mms.ERROR_CODE,
//				Telephony.MmsSms.READ,
//				Telephony.MmsSms.STATUS,
//				Telephony.Mms.TYPE,
//				Telephony.MmsSms.SUBJECT,
//				Telephony.Mms.Inbox.BODY,
//				Telephony.Mms.SERVICE_CENTER,
//				Telephony.MmsSms.LOCKED,
		};

		@Override
		public String[] getProjection() {
			return PROJECTION;
		}

		@Override
		public Uri getUri() {
			return Telephony.MmsSms.CONTENT_CONVERSATIONS_URI;
		}

		@Override
		public List query(Context context) {
			return null;
		}

		@Override
		public MessageItem read(Context context, Cursor cursor) {
			MmsReaderSub mmsReaderSub = new MmsReaderSub();
			MessageItem item = new MessageItem();
			item.id = cursor.getLong(cursor.getColumnIndex(Telephony.MmsSms._ID));
			item.date = cursor.getLong(cursor.getColumnIndex("date"));// * 1000;
			item.body = cursor.getString(cursor.getColumnIndex("snippet"));
			item.read = cursor.getInt(cursor.getColumnIndex("read"));
			item.type = cursor.getInt(cursor.getColumnIndex("type"));

//			item.creator = cursor.getString(cursor.getColumnIndex(Telephony.MmsSms.CREATOR));
//			item.address = mmsReaderSub.getAddressNumber(context, (int)item.id);
//			item.person = cursor.getInt(cursor.getColumnIndex(Telephony.Mms.PERSON));
//			item.date = cursor.getLong(cursor.getColumnIndex(Telephony.MmsSms.DATE)) * 1000;
//			item.dateSent = cursor.getLong(cursor.getColumnIndex(Telephony.MmsSms.DATE_SENT));
//			item.protocol = cursor.getInt(cursor.getColumnIndex(Telephony.Mms.PROTOCOL));
//			item.errorCode = cursor.getInt(cursor.getColumnIndex(Telephony.Mms.ERROR_CODE));
//			item.read = cursor.getInt(cursor.getColumnIndex(Telephony.MmsSms.READ));
//			item.status = cursor.getInt(cursor.getColumnIndex(Telephony.MmsSms.STATUS));
//			item.type = cursor.getInt(cursor.getColumnIndex(Telephony.Mms.TYPE));
//			item.subject = cursor.getString(cursor.getColumnIndex(Telephony.MmsSms.SUBJECT));
//			item.body = mmsReaderSub.getTextMessage(context, String.valueOf(item.id));
//			item.serviceCenter = cursor.getString(cursor.getColumnIndex(Telephony.Mms.SERVICE_CENTER));
//			item.locked = cursor.getInt(cursor.getColumnIndex(Telephony.MmsSms.LOCKED));
			return item;
		}
	}
}
