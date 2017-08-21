package com.risewide.bdebugapp.communication.reader;

import java.util.List;

import com.risewide.bdebugapp.communication.model.CommMsgData;
import com.risewide.bdebugapp.communication.reader.projection.AbsQueryProject;
import com.risewide.bdebugapp.communication.reader.projection.QueryConfig;
import com.risewide.bdebugapp.util.SVLog;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Telephony;

/**
 * Created by birdea on 2017-08-16.
 */

public abstract class AbsMsgReader {

	protected QueryConfig queryConfig;
	protected AbsQueryProject<CommMsgData> project;
	protected OnContentObserver onContentObserver;
	protected ContentObserver contentObserver;

	public AbsMsgReader(Context context, QueryConfig config) {
		init();
		setQueryConfig(config);
	}

	public interface OnContentObserver {
		void onChange();
	}

	private void init() {
		contentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {

			private String uriMonitor;
			private String uriConversation;

			private String getUriMonitor() {
				if (uriMonitor == null && project != null) {
					uriMonitor = project.getUri().toString();
				}
				return uriMonitor;
			}
			private String getUriConversations() {
				if (uriConversation == null) {
					uriConversation =  Telephony.MmsSms.CONTENT_CONVERSATIONS_URI.toString();
				}
				return uriConversation;
			}

			@Override
			public void onChange(boolean selfChange, Uri uri) {
				super.onChange(selfChange, uri);
				String uriChange = uri.toString();
				String uriMonitor = getUriMonitor();
				String uriConversation = getUriConversations();
				SVLog.i(String.format("ContentObserver.onChange > selfChange:%s, Uri:%s, uriChange:%s, uriMonitor:%s",selfChange, uri, uriChange, uriMonitor));
				if (uriChange.equals(uriMonitor) || uriChange.contains(uriConversation)) {
					if (onContentObserver != null) {
						onContentObserver.onChange();
					}
				}
			}
		};
	}
	abstract public List<CommMsgData> read(Context context);

	public void setQueryConfig(QueryConfig config) {
		this.queryConfig = config;
	}

	protected String getConfigSortOrder() {
		if(queryConfig != null) {
			return queryConfig.getComposedSortOrderClause();
		}
		return null;
	}

	public void registerContentObserver(Context context, boolean notifyForDescendents, OnContentObserver observer) {
		onContentObserver = observer;
		ContentResolver cr = context.getContentResolver();
		cr.registerContentObserver(project.getUri(), notifyForDescendents, contentObserver);
		SVLog.d("registerContentObserver uri:"+project.getUri());
	}
	public void unregisterContentObserver(Context context, OnContentObserver observer) {
		ContentResolver cr = context.getContentResolver();
		cr.unregisterContentObserver(contentObserver);
		SVLog.d("unregisterContentObserver uri:"+project.getUri());
	}
}
