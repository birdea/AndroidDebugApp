package com.risewide.bdebugapp.communication.reader.helper;

import com.risewide.bdebugapp.communication.model.CommMsgData;
import com.risewide.bdebugapp.communication.util.DateUtil;
import com.risewide.bdebugapp.communication.util.IOCloser;
import com.risewide.bdebugapp.util.SLog;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

/**
 * Created by birdea on 2017-08-18.
 */

public class SmsReaderHelper {

	public static final String[] PROJECTION_INBOX = {
			Telephony.Sms._ID,
			Telephony.Sms.THREAD_ID,
			Telephony.Sms.ADDRESS,
			Telephony.Sms.DATE,
			Telephony.Sms.STATUS,
			Telephony.Sms.TYPE,
			Telephony.Sms.BODY,
			Telephony.Sms.READ,
	};

	public CommMsgData getTextMessage(ContentResolver resolver, long threadId, long timeStamp, CommMsgData.Type type) {
		CommMsgData msg = new CommMsgData(type);
		String[] projection = PROJECTION_INBOX;
		String selection = Telephony.Sms.THREAD_ID+"="+threadId;//+" AND read!=1";
		String[] selectionArgs = null;
		String sortOrder = "date DESC";// LIMIT 1";
		Uri uri = Telephony.Sms.CONTENT_URI;

		Cursor cursor = resolver.query(uri, projection, selection, selectionArgs, sortOrder);
		if (cursor != null && cursor.moveToFirst()) {
			//
			int idx_id = cursor.getColumnIndex(projection[0]); //_ID
			int idx_threadId = cursor.getColumnIndex(projection[1]); //THREAD_ID
			int idx_address = cursor.getColumnIndex(projection[2]); //ADDRESS
			int idx_date = cursor.getColumnIndex(projection[3]); //DATE
			int idx_status = cursor.getColumnIndex(projection[4]); //STATUS
			int idx_type = cursor.getColumnIndex(projection[5]); //TYPE
			int idx_body = cursor.getColumnIndex(projection[6]); //BODY
			int idx_read = cursor.getColumnIndex(projection[7]); //READ
			//
			long date;
			SLog.i("*getBodyFor:"+DateUtil.getSimpleDate(timeStamp)+", raw:"+timeStamp);
			do {
				date = cursor.getLong(idx_date);
				msg.body = cursor.getString(idx_body);
				SLog.i("*body:"+msg.body+", date:"+date);
				if (CommMsgData.isEqualDateValueOnNormalize(timeStamp, date)) {
					msg.setDate(date);
					msg._id = cursor.getLong(idx_id);
					msg.thread_id = cursor.getLong(idx_threadId);
					msg.address = cursor.getString(idx_address);
					msg.status = cursor.getInt(idx_status);
					msg.type = cursor.getInt(idx_type);
					msg.body = cursor.getString(idx_body);
					msg.read = cursor.getInt(idx_read);
					break;
				}
			} while (cursor.moveToNext());
		}
		if (CommMsgData.isEqualDateValueOnNormalize(timeStamp, msg.getDate())==false) {
			msg = null;
		}
		IOCloser.close(cursor);
		return msg;
	}
}
