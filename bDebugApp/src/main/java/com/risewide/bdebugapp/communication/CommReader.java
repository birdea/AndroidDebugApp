package com.risewide.bdebugapp.communication;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.risewide.bdebugapp.communication.data.MessageItem;
import com.risewide.bdebugapp.util.SVLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by birdea on 2017-08-03.
 */

public class CommReader {

	public List<MessageItem> read(Context context) {
		List<MessageItem> list = new ArrayList<>();
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://mms-sms/conversations/");
		Cursor query;
		try {
			query = resolver.query(uri, null, null, null, null);
		} catch (Exception e) {
			uri = Uri.parse("content://mms-sms/conversations?simple=true");
			query = resolver.query(uri, null, null, null, null);
		}
		if (query != null && query.moveToFirst()) {
			int idx_ct_t = 0;
			//
			String[] columnNames = query.getColumnNames();
			for (String name : columnNames) {
				SVLog.d("columnName:"+name);
				if (name.contains("ct")) {
					idx_ct_t = query.getColumnIndex(name);
				}
			}
			StringBuilder sb = new StringBuilder();
			do {
				sb.delete(0, sb.length());
				for(int i=0;i<columnNames.length;i++) {
					sb.append(columnNames[i]).append(":").append(query.getString(i)).append(", ");
				}
				SVLog.d("val:"+sb.toString());
				//
				MessageItem item = new MessageItem();
				String string = query.getString(idx_ct_t);
				if ("application/vnd.wap.multipart.related".equals(string)) {
					// it's MMS
					item.body = "MMS";
				} else {
					// it's SMS
					item.body = "SMS";
				}
				list.add(item);
			} while (query.moveToNext());
		}
		return list;
	}
}
