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
				mQueryProject = new QuerySmsProject.All();
				break;
			case Inbox:
				mQueryProject = new QuerySmsProject.Inbox();
				break;
			case Sent:
				mQueryProject = new QuerySmsProject.Sent();
				break;
		}
	}

	@Override
	public List<CommMsgData> read(Context context) {
		//- set configurations
		mQueryProject.setExtraLoadMessageData(mQueryConfig.isExtraLoadMessageData());
		mQueryProject.setExtraLoadAddressData(mQueryConfig.isExtraLoadAddressData());
		mQueryProject.setLoadOnlyUnreadData(mQueryConfig.isSelectLoadOnlyUnread());
		mQueryProject.setConfigSortOrder(getConfigSortOrder());
		mQueryProject.setSelection(" thread_id=="+ mQueryConfig.getThreadId()+" ");
		//- execute to readAll
		return mQueryProject.readAll(context);
	}
}
