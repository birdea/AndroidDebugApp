package com.risewide.bdebugapp.communication.reader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.Telephony;

import com.risewide.bdebugapp.communication.model.MessageItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by birdea on 2017-08-03.
 */

public class SmsReader {

	private static final String[] PROJECTION_SMS_MESSAGE = {
			Telephony.Sms._ID,
			Telephony.Sms.CREATOR,
			Telephony.Sms.ADDRESS,
			Telephony.Sms.PERSON,
			Telephony.Sms.DATE,
			Telephony.Sms.DATE_SENT,
			Telephony.Sms.PROTOCOL,
			Telephony.Sms.ERROR_CODE,
			Telephony.Sms.READ,
			Telephony.Sms.STATUS,
			Telephony.Sms.TYPE,
			Telephony.Sms.SUBJECT,
			Telephony.Sms.BODY,
			Telephony.Sms.SERVICE_CENTER,
			Telephony.Sms.LOCKED,
	};

	public List<MessageItem> read(Context context) {
		List<MessageItem> contactInfoList = new ArrayList<>();
		ContentResolver resolver = context.getContentResolver();
		String selection = null;//") GROUP BY ("+Telephony.Sms.CONTACT_ID;
		Cursor msg = resolver.query(Telephony.Sms.CONTENT_URI, PROJECTION_SMS_MESSAGE, selection, null, Telephony.Sms.DEFAULT_SORT_ORDER);
		while (msg.moveToNext())
		{
			MessageItem item = new MessageItem();

			item.id = msg.getLong(msg.getColumnIndex(Telephony.Sms._ID));
			item.creator = msg.getString(msg.getColumnIndex(Telephony.Sms.CREATOR));
			item.address = msg.getString(msg.getColumnIndex(Telephony.Sms.ADDRESS));
			item.person = msg.getInt(msg.getColumnIndex(Telephony.Sms.PERSON));
			item.date = msg.getLong(msg.getColumnIndex(Telephony.Sms.DATE));
			item.dateSent = msg.getLong(msg.getColumnIndex(Telephony.Sms.DATE_SENT));
			item.protocol = msg.getInt(msg.getColumnIndex(Telephony.Sms.PROTOCOL));
			item.errorCode = msg.getInt(msg.getColumnIndex(Telephony.Sms.ERROR_CODE));
			item.read = msg.getInt(msg.getColumnIndex(Telephony.Sms.READ));
			item.status = msg.getInt(msg.getColumnIndex(Telephony.Sms.STATUS));
			item.type = msg.getInt(msg.getColumnIndex(Telephony.Sms.TYPE));
			item.subject = msg.getString(msg.getColumnIndex(Telephony.Sms.SUBJECT));
			item.body = msg.getString(msg.getColumnIndex(Telephony.Sms.BODY));
			item.serviceCenter = msg.getString(msg.getColumnIndex(Telephony.Sms.SERVICE_CENTER));
			item.locked = msg.getInt(msg.getColumnIndex(Telephony.Sms.LOCKED));

			contactInfoList.add(item);
		}
		msg.close();
		//
		return contactInfoList;
	}

}
