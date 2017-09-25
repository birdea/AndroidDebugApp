package com.risewide.bdebugapp.process;

import android.os.Bundle;

import com.risewide.bdebugapp.BaseActivity;
import com.risewide.bdebugapp.R;
import com.risewide.bdebugapp.util.SLog;

public class ActivityTestCrashOnSameProcess extends BaseActivity {

	private Thread.UncaughtExceptionHandler deUncaughtExceptionHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_same_proc);

		deUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				SLog.w("ActivityTestCrashOnSameProcess.uncaughtException :"+e.getLocalizedMessage());
				deUncaughtExceptionHandler.uncaughtException(t, e);
			}
		});
	}

	/*public void onClickExecute(View view) {
		switch (view.getId()) {
			case R._id.btnExecute1: {
				break;
			}
			case R._id.btnExecute2: {
				break;
			}
			default:
				break;
		}
	}*/
}
