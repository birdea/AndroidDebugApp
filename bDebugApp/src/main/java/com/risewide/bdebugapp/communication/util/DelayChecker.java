package com.risewide.bdebugapp.communication.util;

import android.content.Context;

import com.risewide.bdebugapp.util.SVLog;

/**
 * Created by birdea on 2017-08-09.
 */

public class DelayChecker {

	private static final String TAG = DelayChecker.class.getSimpleName();

	private String tag;
	private int count;
	private long baseTime;
	private long lastDelayTime;

	public void start(String tag) {
		this.count = 0;
		this.tag = tag;
		baseTime = System.currentTimeMillis();
	}

	public long end() {
		count++;
		lastDelayTime = System.currentTimeMillis() - baseTime;
		SVLog.d(TAG, getMessage());
		return lastDelayTime;
	}

	public void showToast(Context context) {
		TToast.show(context, getMessage());
	}

	private String getMessage() {
		return String.format("delayed[%s][%d]: %d ms", tag, count, lastDelayTime);
	}

	public void endShowToast(Context context) {
		end();
		showToast(context);
	}
}
