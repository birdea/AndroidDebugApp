package com.risewide.bdebugapp.communication;

import android.content.Context;

/**
 * Created by birdea on 2017-08-08.
 */

public abstract class AbsMessageSender {

	////////////////////////////////////////////////////////////////////////////////////////////////
	public interface OnSendTextMessageListener {
		void onSent(boolean success);
		void onReceived(boolean success);
	}

	public abstract void send(Context context, OnSendTextMessageListener listener);
}
