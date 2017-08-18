package com.risewide.bdebugapp.communication.reader;

import android.content.Context;

import com.risewide.bdebugapp.communication.model.CommMsgData;
import com.risewide.bdebugapp.communication.reader.projection.QueryConfig;

import java.util.List;

/**
 * Created by birdea on 2017-08-16.
 */

public abstract class AbsMsgReader {

	protected QueryConfig queryConfig;

	public AbsMsgReader(QueryConfig config) {
		this.queryConfig = config;
	}

	protected String getConfigSortOrder() {
		if(queryConfig != null) {
			return queryConfig.getComposedSortOrderClause();
		}
		return null;
	}

	abstract public List<CommMsgData> read(Context context);
}
