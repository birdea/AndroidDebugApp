package com.risewide.bdebugapp;

import android.os.Bundle;

public class ActivityTestCrashOnOtherProcess extends BaseActivity {

	private Thread.UncaughtExceptionHandler deUncaughtExceptionHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_other_proc);

		deUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				SVLog.w("ActivityTestCrashOnOtherProcess.uncaughtException :"+e.getLocalizedMessage());
				deUncaughtExceptionHandler.uncaughtException(t, e);
			}
		});
	}

	/*public void onClickExecute(View view) {
		switch (view.getId()) {
			case R.id.btnExecute1: {
				break;
			}
			case R.id.btnExecute2: {
				break;
			}
			default:
				break;
		}
	}*/
}
