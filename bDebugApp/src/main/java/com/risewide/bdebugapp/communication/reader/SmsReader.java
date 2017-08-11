package com.risewide.bdebugapp.communication.reader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.Telephony;

import com.risewide.bdebugapp.communication.util.CursorUtil;
import com.risewide.bdebugapp.communication.model.SmsMmsMsg;
import com.risewide.bdebugapp.communication.reader.projection.ReadProjector;
import com.risewide.bdebugapp.communication.reader.projection.SmsReadProject;
import com.risewide.bdebugapp.communication.util.IOCloser;

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
		Cursor cursor = resolver.query(rp.getUri(), rp.getProjection(), rp.getSelection(), null, Telephony.Sms.DEFAULT_SORT_ORDER);
		if (cursor != null && cursor.moveToFirst()) {
			do {
				SmsMmsMsg item = new SmsMmsMsg(SmsMmsMsg.Type.SMS);
				item._id = CursorUtil.getLong(cursor,Telephony.Sms._ID);
				item.address = CursorUtil.getString(cursor,Telephony.Sms.ADDRESS);
				item.setDate(CursorUtil.getLong(cursor,Telephony.Sms.DATE));
				item.read = CursorUtil.getInt(cursor,Telephony.Sms.READ);
				item.type = CursorUtil.getInt(cursor,Telephony.Sms.TYPE);
				item.body = CursorUtil.getString(cursor,Telephony.Sms.BODY);
				dataList.add(item);
			} while (cursor.moveToNext());
		}
		IOCloser.close(cursor);
		//
		return dataList;
	}

}
