package com.risewide.bdebugapp.communication.reader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;

import com.risewide.bdebugapp.communication.model.CommMsgData;
import com.risewide.bdebugapp.communication.reader.projection.AbsQueryProject;
import com.risewide.bdebugapp.communication.reader.projection.QueryConfig;

import java.util.List;

/**
 * Created by birdea on 2017-08-16.
 */

public abstract class AbsMsgReader {

	protected QueryConfig queryConfig;
	protected AbsQueryProject<CommMsgData> project;

	public AbsMsgReader(Context context, QueryConfig config) {
		setQueryConfig(config);
	}

	abstract public List<CommMsgData> read(Context context);

	protected void setQueryConfig(QueryConfig config) {
		this.queryConfig = config;
	}

	protected String getConfigSortOrder() {
		if(queryConfig != null) {
			return queryConfig.getComposedSortOrderClause();
		}
		return null;
	}

	public void registerContentObserver(Context context, boolean notifyForDescendents, ContentObserver observer) {
		ContentResolver cr = context.getContentResolver();
		cr.registerContentObserver(project.getUri(), notifyForDescendents, observer);
	}
	public void unregisterContentObserver(Context context, ContentObserver observer) {
		ContentResolver cr = context.getContentResolver();
		cr.unregisterContentObserver(observer);
	}
}
