package com.risewide.bdebugapp.communication.reader;

import android.content.Context;
import android.database.ContentObserver;
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

	public ConversationReader(Context context, QueryConfig config) {
		super(context, config);
		project = QueryConversationProject.getProject(context, queryConfig, getConfigSortOrder());
	}

	@Override
	public List<CommMsgData> read(Context context) {
		//- execute to readAll
		return project.readAll(context);
	}
}
