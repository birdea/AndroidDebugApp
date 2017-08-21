package com.risewide.bdebugapp.communication;

import android.content.Context;
import android.database.ContentObserver;

import com.risewide.bdebugapp.communication.model.CommMsgData;
import com.risewide.bdebugapp.communication.reader.AbsMsgReader;

import java.util.List;

/**
 * Created by birdea on 2017-08-08.
 */

public abstract class AbsMessageReader {

	////////////////////////////////////////////////////////////////////////////////////////////////
	public interface OnReadTextMessageListener {
		void onComplete(List<CommMsgData> list);
	}

	public abstract void read(Context context, OnReadTextMessageListener listener);
	abstract public void registerContentObserver(Context context, boolean notifyForDescendents, AbsMsgReader.OnContentObserver observer);
	abstract public void unregisterContentObserver(Context context, AbsMsgReader.OnContentObserver observer);
}
