package com.risewide.bdebugapp.communication;

import android.content.Context;

import com.risewide.bdebugapp.communication.model.CommMsgData;

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
}
