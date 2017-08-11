package com.risewide.bdebugapp.communication.reader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import com.risewide.bdebugapp.communication.model.MessageItem;
import com.risewide.bdebugapp.communication.reader.projection.MmsReadProject;
import com.risewide.bdebugapp.communication.reader.projection.MmsSmsReadProject;
import com.risewide.bdebugapp.communication.reader.projection.ReadProjector;
import com.risewide.bdebugapp.util.SVLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by birdea on 2017-08-03.
 */

public class MmsSmsReader {

	public List<MessageItem> read(Context context) {

		ReadProjector rp = new MmsSmsReadProject.All();

		List<MessageItem> dataList = new ArrayList<>();
		ContentResolver resolver = context.getContentResolver();
		String selection = null;
		Cursor cursor;
		try {
			cursor = resolver.query(rp.getUri(), rp.getProjection(), selection, null, "date DESC");
		} catch (Exception e) {
			Uri uriExtended = Uri.parse("content://mms-sms/conversations?simple=true");
			cursor = resolver.query(uriExtended, rp.getProjection(), selection, null, "date DESC");
		}
		if (cursor != null && cursor.moveToFirst()) {
			int idx_ct_t = 0;
			//
			String[] columnNames = cursor.getColumnNames();
			for (int i=0;i<columnNames.length;i++) {
				SVLog.d("columnType:"+cursor.getType(i)+", columnName:"+columnNames[i]);
			}
			StringBuilder sb = new StringBuilder();
			while (cursor.moveToNext()) {
				sb.setLength(0);
				for(int i=0;i<columnNames.length;i++) {
					sb.append(columnNames[i]).append(":").append(cursor.getString(i)).append(", ");
				}
				SVLog.d("val:"+sb.toString());
				//
				MessageItem item = rp.read(context, cursor);
//				if ("application/vnd.wap.multipart.related".equals(string)) {
//					// it's MMS
//					item.body = "MMS";
//				} else {
//					// it's SMS
//					item.body = "SMS";
//				}
				dataList.add(item);
			}
		}
		cursor.close();
		//
		return dataList;
	}
}
