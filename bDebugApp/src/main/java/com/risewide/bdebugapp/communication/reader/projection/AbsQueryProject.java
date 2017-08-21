package com.risewide.bdebugapp.communication.reader.projection;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.List;

/**
 * Created by birdea on 2017-08-09.
 */

public abstract class AbsQueryProject<T> {

	protected boolean isExtraLoadMessageData = false;
	protected boolean isExtraLoadAddressData = false;
	protected boolean isLoadOnlyUnreadData = false;
	protected String sortOrder;
	protected abstract void storeProjectColumnIndex(Cursor cursor);
	protected abstract T read(Context context, Cursor cursor);

	public abstract String[] getProjection();
	public abstract String getSelection();
	public abstract String[] getSelectionArgs();
	public abstract Uri getUri();
	public abstract List<T> readAll(Context context);

	public void setExtraLoadMessageData(boolean loadable){
		isExtraLoadMessageData = loadable;
	}
	public void setExtraLoadAddressData(boolean loadable){
		isExtraLoadAddressData = loadable;
	}
	public void setLoadOnlyUnreadData(boolean isOnlyUnread){
		isLoadOnlyUnreadData = isOnlyUnread;
	}

	public void setConfigSortOrder(String order) {
		sortOrder = order;
	}
	public String getConfigSortOrder(){
		return sortOrder;
	}
}