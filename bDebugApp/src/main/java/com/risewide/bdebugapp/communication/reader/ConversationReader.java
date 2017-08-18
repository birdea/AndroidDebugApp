package com.risewide.bdebugapp.communication.reader;

import android.content.Context;
import android.database.Cursor;

import com.risewide.bdebugapp.communication.model.CommMsgData;
import com.risewide.bdebugapp.communication.reader.projection.AbsQueryProject;
import com.risewide.bdebugapp.communication.reader.projection.QueryConversationProject;
import com.risewide.bdebugapp.communication.reader.projection.QueryConfig;
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

	@Override
	public List<CommMsgData> read(Context context) {
		AbsQueryProject<CommMsgData> project = QueryConversationProject.getProject(context, queryConfig, getConfigSortOrder());
		List<CommMsgData> dataList = new ArrayList<>();
		Cursor cursor = project.getQueriedCursor();
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
