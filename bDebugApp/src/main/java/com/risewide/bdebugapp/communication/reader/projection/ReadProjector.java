package com.risewide.bdebugapp.communication.reader.projection;

import com.risewide.bdebugapp.communication.model.MessageItem;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.List;

/**
 * Created by birdea on 2017-08-09.
 */

public abstract class ReadProjector<T> {
	public abstract String[] getProjection();
	public abstract Uri getUri();
	public abstract List<T> query(Context context);
	public abstract T read(Context context, Cursor cursor);
}
