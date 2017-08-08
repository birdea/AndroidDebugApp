package com.risewide.bdebugapp.communication.reader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.Telephony;

import com.risewide.bdebugapp.communication.model.MessageItem;
import com.risewide.bdebugapp.communication.reader.projection.SmsReadProject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by birdea on 2017-08-03.
 */

public class SmsReader {

	public List<MessageItem> read(Context context) {
//		SmsReadProject.ReadProjector rp = new SmsReadProject.All();
//		SmsReadProject.ReadProjector rp = new SmsReadProject.Inbox();
		SmsReadProject.ReadProjector rp = new SmsReadProject.Sent();

		List<MessageItem> contactInfoList = new ArrayList<>();
		ContentResolver resolver = context.getContentResolver();
		String selection = null;//") GROUP BY ("+Telephony.Sms.CONTACT_ID;
		Cursor cursor = resolver.query(rp.getUri(), rp.getProjection(), selection, null, Telephony.Sms.DEFAULT_SORT_ORDER);
		while (cursor.moveToNext()) {
			MessageItem item = rp.read(cursor);
			contactInfoList.add(item);
		}
		cursor.close();
		//
		return contactInfoList;
	}

}
