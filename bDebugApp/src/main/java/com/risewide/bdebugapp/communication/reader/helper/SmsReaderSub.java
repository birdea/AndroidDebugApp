package com.risewide.bdebugapp.communication.reader.helper;

import com.risewide.bdebugapp.communication.model.MmsSmsMsg;
import com.risewide.bdebugapp.communication.util.IOCloser;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

/**
 * Created by birdea on 2017-08-18.
 */

public class SmsReaderSub {

	public static final String[] PROJECTION_INBOX = {
			Telephony.Sms.Inbox._ID,
			Telephony.Sms.Inbox.THREAD_ID,
			Telephony.Sms.Inbox.ADDRESS,
			Telephony.Sms.Inbox.DATE,
			Telephony.Sms.Inbox.STATUS,
			Telephony.Sms.Inbox.TYPE,
			Telephony.Sms.Inbox.BODY,
			Telephony.Sms.Inbox.READ,
	};

	public MmsSmsMsg getTextMessage(ContentResolver resolver, long threadId, MmsSmsMsg.Type type) {
		MmsSmsMsg msg = new MmsSmsMsg(type);
		String[] projection = PROJECTION_INBOX;
		String selection = Telephony.Sms.THREAD_ID+"="+threadId+" AND read!=1";
		String[] selectionArgs = null;
		String sortOrder = "date DESC LIMIT 1";
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
			do {
				msg._id = cursor.getLong(idx_id);
				msg.thread_id = cursor.getLong(idx_threadId);
				msg.address = cursor.getString(idx_address);
				msg.setDate(cursor.getLong(idx_date));
				msg.status = cursor.getInt(idx_status);
				msg.type = cursor.getInt(idx_type);
				msg.body = cursor.getString(idx_body);
				msg.read = cursor.getInt(idx_read);
			} while (cursor.moveToNext());
		}
		IOCloser.close(cursor);
		return msg;
	}
}
