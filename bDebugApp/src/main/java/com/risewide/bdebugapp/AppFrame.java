package com.risewide.bdebugapp;

import android.app.Application;

import com.risewide.bdebugapp.util.SLog;

/**
 * Created by birdea on 2017-05-29.
 */

public class AppFrame extends Application{

	@Override
	public void onCreate() {
		super.onCreate();
		SLog.d("AppFrame.onCreate()");
	}
}
