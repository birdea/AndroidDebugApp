package com.risewide.bdebugapp.communication.reader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import com.risewide.bdebugapp.communication.reader.projection.QueryConfig;
import com.risewide.bdebugapp.communication.reader.projection.AbsQueryProject;
import com.risewide.bdebugapp.communication.model.CommMsgData;
import com.risewide.bdebugapp.communication.reader.projection.QuerySmsProject;
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

	@Override
	public List<CommMsgData> read(Context context) {
//		AbsQueryProject project = new QuerySmsProject.All();
		AbsQueryProject<CommMsgData> project = new QuerySmsProject.Inbox();
//		AbsQueryProject project = new QuerySmsProject.Sent();
		project.setExtraLoadMessageData(queryConfig.isExtraLoadMessageData());
		project.setExtraLoadAddressData(queryConfig.isExtraLoadAddressData());
		project.setSelectLoadOnlyUnread(queryConfig.isSelectLoadOnlyUnread());

		List<CommMsgData> dataList = new ArrayList<>();
		ContentResolver resolver = context.getContentResolver();
		String sortOrder = getConfigSortOrder();
		Cursor cursor = resolver.query(project.getUri(), project.getProjection(), project.getSelection(), project.getSelectionArgs(), sortOrder);
		if (cursor != null && cursor.moveToFirst()) {
			project.storeColumnIndex(cursor);
			do {
				CommMsgData item = project.read(context, cursor);
				dataList.add(item);
			} while (cursor.moveToNext());
		}
		IOCloser.close(cursor);
		//
		return dataList;
	}

}
