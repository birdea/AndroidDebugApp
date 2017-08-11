package com.risewide.bdebugapp.communication.reader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import com.risewide.bdebugapp.communication.model.SmsMmsMsg;
import com.risewide.bdebugapp.communication.reader.projection.ConversationReadProject;
import com.risewide.bdebugapp.communication.reader.projection.ReadProjector;
import com.risewide.bdebugapp.util.SVLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by birdea on 2017-08-03.
 */

public class ConversationReader {

	public List<SmsMmsMsg> read(Context context) {
		ReadProjector<SmsMmsMsg> rp = new ConversationReadProject.All();
		List<SmsMmsMsg> dataList = new ArrayList<>();
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor;
		try {
			cursor = resolver.query(rp.getUri(), rp.getProjection(), rp.getSelection(), null, "date DESC");
		} catch (Exception e) {
			Uri uriExtended = Uri.parse("content://mms-sms/conversations?simple=true");
			cursor = resolver.query(uriExtended, rp.getProjection(), rp.getSelection(), null, "date DESC");
		}
		if (cursor != null && cursor.moveToFirst()) {
			rp.storeColumnIndex(cursor);

			do {
				SmsMmsMsg item = rp.read(context, cursor);
				dataList.add(item);
			} while (cursor.moveToNext());
		}
		cursor.close();
		//
		return dataList;
	}
}
