package com.risewide.bdebugapp.communication.reader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.risewide.bdebugapp.communication.model.MmsSmsMsg;
import com.risewide.bdebugapp.communication.reader.projection.ConversationReadProject;
import com.risewide.bdebugapp.communication.reader.projection.QueryConfig;
import com.risewide.bdebugapp.communication.reader.projection.ReadProjector;
import com.risewide.bdebugapp.communication.util.IOCloser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by birdea on 2017-08-03.
 */

public class ConversationReader extends AbsMsgReader {

	public ConversationReader(QueryConfig config) {
		super(config);
	}

	public List<MmsSmsMsg> read(Context context) {
		List<MmsSmsMsg> dataList = new ArrayList<>();
		ReadProjector<MmsSmsMsg> projector = ConversationReadProject.getProject(context, queryConfig, getConfigSortOrder());
		Cursor cursor = projector.getQueriedCursor();
		if (cursor != null && cursor.moveToFirst()) {
			projector.storeColumnIndex(cursor);
			do {
				MmsSmsMsg item = projector.read(context, cursor);
				dataList.add(item);
			} while (cursor.moveToNext());
		}
		IOCloser.close(cursor);
		//
		return dataList;
	}
}
