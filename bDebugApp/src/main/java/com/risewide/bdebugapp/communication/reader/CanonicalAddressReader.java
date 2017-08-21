package com.risewide.bdebugapp.communication.reader;

import android.content.Context;

import com.risewide.bdebugapp.communication.model.CommMsgData;
import com.risewide.bdebugapp.communication.reader.projection.QueryCanonicalAddressProject;
import com.risewide.bdebugapp.communication.reader.projection.QueryConfig;

import java.util.List;

/**
 * Created by birdea on 2017-08-21.
 */

public class CanonicalAddressReader extends AbsMsgReader {

	public CanonicalAddressReader(Context context, QueryConfig config) {
		super(context, config);
		project = new QueryCanonicalAddressProject.CanonicalAddressProject();
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
