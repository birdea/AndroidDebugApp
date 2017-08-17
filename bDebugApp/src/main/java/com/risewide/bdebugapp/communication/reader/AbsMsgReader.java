package com.risewide.bdebugapp.communication.reader;

import com.risewide.bdebugapp.communication.reader.projection.QueryConfig;

/**
 * Created by birdea on 2017-08-16.
 */

public class AbsMsgReader {

	protected QueryConfig queryConfig;

	public AbsMsgReader(QueryConfig config) {
		queryConfig = config;
	}

	protected String getConfigSortOrder() {
		if(queryConfig != null) {
			return queryConfig.getComposedSortOrderClause();
		}
		return null;
	}

}
