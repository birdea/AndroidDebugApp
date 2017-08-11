package com.risewide.bdebugapp.communication.reader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.Telephony;

import com.risewide.bdebugapp.communication.helper.CursorHelper;
import com.risewide.bdebugapp.communication.model.SmsMmsMsg;
import com.risewide.bdebugapp.communication.reader.projection.ReadProjector;
import com.risewide.bdebugapp.communication.reader.projection.SmsReadProject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by birdea on 2017-08-03.
 */

public class SmsReader {

	public List<SmsMmsMsg> read(Context context) {
//		ReadProjector rp = new SmsReadProject.All();
		ReadProjector rp = new SmsReadProject.Inbox();
//		ReadProjector rp = new SmsReadProject.Sent();

		List<SmsMmsMsg> dataList = new ArrayList<>();
		ContentResolver resolver = context.getContentResolver();
		String selection = null;
		Cursor cursor = resolver.query(rp.getUri(), rp.getProjection(), selection, null, Telephony.Sms.DEFAULT_SORT_ORDER);
		while (cursor.moveToNext()) {
			SmsMmsMsg item = new SmsMmsMsg();
			item._id = CursorHelper.getLong(cursor,Telephony.Sms._ID);
			item.address = CursorHelper.getString(cursor,Telephony.Sms.ADDRESS);
			item.date = CursorHelper.getLong(cursor,Telephony.Sms.DATE);
			item.read = CursorHelper.getInt(cursor,Telephony.Sms.READ);
			item.type = CursorHelper.getInt(cursor,Telephony.Sms.TYPE);
			item.body = CursorHelper.getString(cursor,Telephony.Sms.BODY);

			dataList.add(item);
		}
		cursor.close();
		//
		return dataList;
	}

}
