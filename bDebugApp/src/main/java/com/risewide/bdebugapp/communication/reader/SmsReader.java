package com.risewide.bdebugapp.communication.reader;

import java.util.List;

import com.risewide.bdebugapp.communication.model.CommMsgData;
import com.risewide.bdebugapp.communication.reader.projection.QueryConfig;
import com.risewide.bdebugapp.communication.reader.projection.QuerySmsProject;

import android.content.Context;

/**
 * Created by birdea on 2017-08-03.
 */

public class SmsReader extends AbsMsgReader {

	public SmsReader(Context context, QueryConfig config) {
		super(context, config);
//		project = new QuerySmsProject.All();
		project = new QuerySmsProject.Inbox();
//		project = new QuerySmsProject.Sent();
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
