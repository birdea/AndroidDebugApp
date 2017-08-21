package com.risewide.bdebugapp.communication.reader;

import java.util.List;

import com.risewide.bdebugapp.communication.model.CommMsgData;
import com.risewide.bdebugapp.communication.reader.projection.AbsQueryProject;
import com.risewide.bdebugapp.communication.reader.projection.QueryMmsProject;
import com.risewide.bdebugapp.communication.reader.projection.QueryConfig;

import android.content.Context;
import android.database.ContentObserver;

/**
 * Created by birdea on 2017-08-03.
 */

public class MmsReader extends AbsMsgReader {

	public MmsReader(Context context, QueryConfig config) {
		super(context, config);
		project = new QueryMmsProject.All();
	}

	@Override
	public List<CommMsgData> read(Context context) {
		//- set configurations
		project.setExtraLoadMessageData(queryConfig.isExtraLoadMessageData());
		project.setExtraLoadAddressData(queryConfig.isExtraLoadAddressData());
		project.setLoadOnlyUnreadData(queryConfig.isSelectLoadOnlyUnread());
		project.setConfigSortOrder(getConfigSortOrder());
		//- execute to readAll
		return project.readAll(context);
	}
}
