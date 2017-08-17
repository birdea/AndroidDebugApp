package com.risewide.bdebugapp.communication.reader;

import java.util.ArrayList;
import java.util.List;

import com.risewide.bdebugapp.communication.reader.projection.QueryConfig;
import com.risewide.bdebugapp.communication.util.IOCloser;
import com.risewide.bdebugapp.communication.model.MmsSmsMsg;
import com.risewide.bdebugapp.communication.reader.projection.MmsReadProject;
import com.risewide.bdebugapp.communication.reader.projection.ReadProjector;

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

	public List<MmsSmsMsg> read(Context context) {
		ReadProjector<MmsSmsMsg> project = new MmsReadProject.All();
		project.setExtraLoadMessageData(queryConfig.isExtraLoadMessageData());
		project.setExtraLoadAddressData(queryConfig.isExtraLoadAddressData());
		project.setSelectLoadOnlyUnread(queryConfig.isSelectLoadOnlyUnread());
		//
		List<MmsSmsMsg> dataList = new ArrayList<>();
		ContentResolver resolver = context.getContentResolver();
		String sortOrder = getConfigSortOrder();
		Cursor cursor = resolver.query(project.getUri(), project.getProjection(), project.getSelection(), project.getSelectionArgs(), sortOrder);
		if (cursor != null && cursor.moveToFirst()) {
			project.storeColumnIndex(cursor);
			do {
				MmsSmsMsg item = project.read(context, cursor);
				dataList.add(item);
			}while (cursor.moveToNext());
		}
		IOCloser.close(cursor);
		return dataList;
	}
}
