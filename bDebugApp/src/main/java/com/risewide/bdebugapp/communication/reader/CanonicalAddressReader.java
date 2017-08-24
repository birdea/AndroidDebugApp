package com.risewide.bdebugapp.communication.reader;

import java.util.List;

import com.risewide.bdebugapp.communication.model.CommMsgData;
import com.risewide.bdebugapp.communication.reader.projection.QueryCanonicalAddressProject;
import com.risewide.bdebugapp.communication.reader.projection.QueryConfig;

import android.content.Context;

/**
 * Created by birdea on 2017-08-21.
 */

public class CanonicalAddressReader extends AbsMsgReader {

	public CanonicalAddressReader(Context context, QueryConfig config) {
		super(context, config);
		mQueryProject = new QueryCanonicalAddressProject.CanonicalAddressProject();
	}

	@Override
	public List<CommMsgData> read(Context context) {
		//- set configurations
		mQueryProject.setExtraLoadMessageData(mQueryConfig.isExtraLoadMessageData());
		mQueryProject.setExtraLoadAddressData(mQueryConfig.isExtraLoadAddressData());
		mQueryProject.setLoadOnlyUnreadData(mQueryConfig.isSelectLoadOnlyUnread());
		mQueryProject.setConfigSortOrder(getConfigSortOrder());
		//- execute to readAll
		return mQueryProject.readAll(context);
	}
}
