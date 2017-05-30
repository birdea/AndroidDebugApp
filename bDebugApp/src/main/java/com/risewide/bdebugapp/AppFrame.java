package com.risewide.bdebugapp;

import android.app.Application;

import com.risewide.bdebugapp.util.SVLog;

/**
 * Created by sktechx on 2017-05-29.
 */

public class AppFrame extends Application{

	@Override
	public void onCreate() {
		super.onCreate();
		SVLog.d("AppFrame.onCreate()");
	}
}
