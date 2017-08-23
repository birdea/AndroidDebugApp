package com.risewide.bdebugapp.communication;

import java.util.List;

import com.risewide.bdebugapp.communication.model.CommMsgData;
import com.risewide.bdebugapp.communication.reader.AbsMsgReader;

import android.content.Context;

/**
 * Created by birdea on 2017-08-08.
 */

public abstract class AbsMessageReader {

	////////////////////////////////////////////////////////////////////////////////////////////////
	public interface OnReadTextMessageListener {
		void onComplete(List<CommMsgData> list);
		void onError(Throwable e);
	}

	public abstract void read(Context context, OnReadTextMessageListener listener);
	abstract public void registerContentObserver(Context context, boolean notifyForDescendents, AbsMsgReader.OnContentObserver observer);
	abstract public void unregisterContentObserver(Context context, AbsMsgReader.OnContentObserver observer);
}
