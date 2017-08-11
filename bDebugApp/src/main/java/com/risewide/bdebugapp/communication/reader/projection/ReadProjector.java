package com.risewide.bdebugapp.communication.reader.projection;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.List;

/**
 * Created by birdea on 2017-08-09.
 */

public abstract class ReadProjector<T> {

	protected int[] idxColumn;

	public abstract String[] getProjection();
	public abstract Uri getUri();
	public abstract void storeColumnIndex(Cursor cursor);
	public abstract T read(Context context, Cursor cursor);
}
