package com.risewide.bdebugapp.communication.reader.projection;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by birdea on 2017-08-09.
 */

public abstract class ReadProjector<T> {

	protected int[] idxColumn;
	protected boolean isExtraLoadMessageData = false;
	protected boolean isExtraLoadAddressData = false;
	protected boolean isSelectLoadOnlyUnread = false;
	protected Cursor quriedCursor;

	public abstract String[] getProjection();
	public abstract String getSelection();
	public abstract String[] getSelectionArgs();
	public abstract Uri getUri();
	public abstract void storeColumnIndex(Cursor cursor);
	public abstract T read(Context context, Cursor cursor);

	public void setExtraLoadMessageData(boolean loadable){
		isExtraLoadMessageData = loadable;
	}

	public void setExtraLoadAddressData(boolean loadable){
		isExtraLoadAddressData = loadable;
	}

	public void setSelectLoadOnlyUnread(boolean isOnlyUnread){
		isSelectLoadOnlyUnread = isOnlyUnread;
	}

	public void setQueriedCursor(Cursor cursor) {
		quriedCursor = cursor;
	}
	public Cursor getQueriedCursor() {
		return quriedCursor;
	}
}
