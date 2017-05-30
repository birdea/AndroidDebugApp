package com.risewide.bdebugapp;

import android.app.Application;

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
