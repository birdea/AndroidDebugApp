package com.risewide.bdebugapp.communication.reader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.Telephony;

import com.risewide.bdebugapp.communication.reader.projection.QueryConfig;
import com.risewide.bdebugapp.communication.util.CursorUtil;
import com.risewide.bdebugapp.communication.model.MmsSmsMsg;
import com.risewide.bdebugapp.communication.reader.projection.ReadProjector;
import com.risewide.bdebugapp.communication.reader.projection.SmsReadProject;
import com.risewide.bdebugapp.communication.util.IOCloser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by birdea on 2017-08-03.
 */

public class SmsReader extends AbsMsgReader {

	public SmsReader(QueryConfig config) {
		super(config);
	}

	public List<MmsSmsMsg> read(Context context) {
//		ReadProjector project = new SmsReadProject.All();
		ReadProjector project = new SmsReadProject.Inbox();
//		ReadProjector project = new SmsReadProject.Sent();
		project.setExtraLoadMessageData(queryConfig.isExtraLoadMessageData());
		project.setExtraLoadAddressData(queryConfig.isExtraLoadAddressData());
		project.setSelectLoadOnlyUnread(queryConfig.isSelectLoadOnlyUnread());

		List<MmsSmsMsg> dataList = new ArrayList<>();
		ContentResolver resolver = context.getContentResolver();
		String sortOrder = getConfigSortOrder();
		Cursor cursor = resolver.query(project.getUri(), project.getProjection(), project.getSelection(), project.getSelectionArgs(), sortOrder);
		if (cursor != null && cursor.moveToFirst()) {
			do {
				MmsSmsMsg item = new MmsSmsMsg(MmsSmsMsg.Type.SMS);
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
