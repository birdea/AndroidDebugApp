package com.risewide.bdebugapp.communication.reader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.Telephony;

import com.risewide.bdebugapp.communication.model.MessageItem;
import com.risewide.bdebugapp.communication.reader.projection.ReadProjector;
import com.risewide.bdebugapp.communication.reader.projection.SmsReadProject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by birdea on 2017-08-03.
 */

public class SmsReader {

	public List<MessageItem> read(Context context) {
//		ReadProjector rp = new SmsReadProject.All();
		ReadProjector rp = new SmsReadProject.Inbox();
//		ReadProjector rp = new SmsReadProject.Sent();

		List<MessageItem> dataList = new ArrayList<>();
		ContentResolver resolver = context.getContentResolver();
		String selection = null;
		Cursor cursor = resolver.query(rp.getUri(), rp.getProjection(), selection, null, Telephony.Sms.DEFAULT_SORT_ORDER);
		while (cursor.moveToNext()) {
			MessageItem item = rp.read(context, cursor);
			dataList.add(item);
		}
		cursor.close();
		//
		return dataList;
	}

}
