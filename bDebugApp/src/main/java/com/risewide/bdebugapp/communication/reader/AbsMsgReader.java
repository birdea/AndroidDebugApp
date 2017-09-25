package com.risewide.bdebugapp.communication.reader;

import java.util.List;

import com.risewide.bdebugapp.communication.model.CommMsgData;
import com.risewide.bdebugapp.communication.reader.projection.AbsQueryProject;
import com.risewide.bdebugapp.communication.reader.projection.QueryConfig;
import com.risewide.bdebugapp.util.SLog;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Telephony;

/**
 * <p>Content Provider(*cp) 에서 문자 읽기 기능 수행을 위한 추상 클래스
 * {@link QueryConfig} cp query 수행시 사용될 설정 클래스
 * {@link CommMsgData} 문자 읽기 관련 데이터 클래스
 * {@link OnContentObserver} cp (change) event 발생을 처리하기 위한 인터페이스
 * Created by birdea on 2017-08-16.
 */

public abstract class AbsMsgReader {

	protected QueryConfig mQueryConfig;
	protected AbsQueryProject<CommMsgData> mQueryProject;
	protected OnContentObserver mOnContentObserver;
	protected ContentObserver mContentObserver;

	public AbsMsgReader(Context context, QueryConfig config) {
		init();
		setQueryConfig(config);
	}

	public interface OnContentObserver {
		void onChange();
	}

	private void init() {
		mContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {

			private String mUriMonitor;
			private String mUriConversation;

			private String getUriMonitor() {
				if (mUriMonitor == null && mQueryProject != null) {
					mUriMonitor = mQueryProject.getUri().toString();
				}
				return mUriMonitor;
			}

			private String getUriConversations() {
				if (mUriConversation == null) {
					mUriConversation =  Telephony.MmsSms.CONTENT_CONVERSATIONS_URI.toString();
				}
				return mUriConversation;
			}

			public void onChange(boolean selfChange, Uri uri) {
				super.onChange(selfChange, uri);
				String uriChange = uri.toString();
				String uriMonitor = getUriMonitor();
				String uriConversation = getUriConversations();
				SLog.i(String.format("ContentObserver.onChange > selfChange:%s, Uri:%s, uriChange:%s, mUriMonitor:%s",selfChange, uri, uriChange, uriMonitor));
				if (uriChange.equals(uriMonitor) || uriChange.contains(uriConversation)) {
					if (mOnContentObserver != null) {
						mOnContentObserver.onChange();
					}
				}
			}
		};
	}
	abstract public List<CommMsgData> read(Context context);

	public void setQueryConfig(QueryConfig config) {
		mQueryConfig = config;
	}

	protected String getConfigSortOrder() {
		if(mQueryConfig != null) {
			return mQueryConfig.getComposedSortOrderClause();
		}
		return null;
	}

	public void registerContentObserver(Context context, boolean notifyForDescendents, OnContentObserver observer) {
		mOnContentObserver = observer;
		ContentResolver cr = context.getContentResolver();
		cr.registerContentObserver(mQueryProject.getUri(), notifyForDescendents, mContentObserver);
		SLog.d("registerContentObserver uri:"+ mQueryProject.getUri());
	}
	public void unregisterContentObserver(Context context, OnContentObserver observer) {
		ContentResolver cr = context.getContentResolver();
		cr.unregisterContentObserver(mContentObserver);
		SLog.d("unregisterContentObserver uri:"+ mQueryProject.getUri());
	}
}
