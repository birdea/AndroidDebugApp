package com.risewide.bdebugapp.communication.reader;

import java.util.ArrayList;
import java.util.List;

import com.risewide.bdebugapp.communication.model.CommMsgData;
import com.risewide.bdebugapp.communication.reader.projection.AbsQueryProject;
import com.risewide.bdebugapp.communication.reader.projection.QueryMmsProject;
import com.risewide.bdebugapp.communication.reader.projection.QueryConfig;
import com.risewide.bdebugapp.communication.util.IOCloser;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

/**
 * Created by birdea on 2017-08-03.
 */

public class MmsReader extends AbsMsgReader {

	public MmsReader(QueryConfig config) {
		super(config);
	}

	@Override
	public List<CommMsgData> read(Context context) {
		AbsQueryProject<CommMsgData> project = new QueryMmsProject.All();
		project.setExtraLoadMessageData(queryConfig.isExtraLoadMessageData());
		project.setExtraLoadAddressData(queryConfig.isExtraLoadAddressData());
		project.setSelectLoadOnlyUnread(queryConfig.isSelectLoadOnlyUnread());
		//
		List<CommMsgData> dataList = new ArrayList<>();
		ContentResolver resolver = context.getContentResolver();
		String sortOrder = getConfigSortOrder();
		Cursor cursor = resolver.query(project.getUri(), project.getProjection(), project.getSelection(), project.getSelectionArgs(), sortOrder);
		if (cursor != null && cursor.moveToFirst()) {
			project.storeColumnIndex(cursor);
			do {
				CommMsgData item = project.read(context, cursor);
				dataList.add(item);
			}while (cursor.moveToNext());
		}
		IOCloser.close(cursor);
		return dataList;
	}
}
