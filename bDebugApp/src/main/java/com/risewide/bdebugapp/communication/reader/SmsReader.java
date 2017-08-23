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
		initProjection(config);
	}

	@Override
	public void setQueryConfig(QueryConfig config) {
		super.setQueryConfig(config);
		initProjection(config);
	}

	private void initProjection(QueryConfig config) {
		switch (config.getTableType()) {
			case All:
				project = new QuerySmsProject.All();
				break;
			case Inbox:
				project = new QuerySmsProject.Inbox();
				break;
			case Sent:
				project = new QuerySmsProject.Sent();
				break;
		}
	}

	@Override
	public List<CommMsgData> read(Context context) {
		//- set configurations
		project.setExtraLoadMessageData(queryConfig.isExtraLoadMessageData());
		project.setExtraLoadAddressData(queryConfig.isExtraLoadAddressData());
		project.setLoadOnlyUnreadData(queryConfig.isSelectLoadOnlyUnread());
		project.setConfigSortOrder(getConfigSortOrder());
		project.setSelection(" thread_id=="+queryConfig.getThreadId()+" ");
		//- execute to readAll
		return project.readAll(context);
	}
}
