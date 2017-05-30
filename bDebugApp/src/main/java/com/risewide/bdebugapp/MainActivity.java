package com.risewide.bdebugapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.risewide.bdebugapp.external.SpeechDemoGoogleActivity;
import com.risewide.bdebugapp.external.SpeechDemoKakaoActivity;
import com.risewide.bdebugapp.external.SpeechDemoNaverActivity;
import com.risewide.bdebugapp.process.ActivityTestCrashOnOtherProcess;
import com.risewide.bdebugapp.process.ActivityTestCrashOnSameProcess;
import com.risewide.bdebugapp.util.SVLog;

public class MainActivity extends BaseActivity {

	private Thread.UncaughtExceptionHandler deUncaughtExceptionHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		deUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				SVLog.w("MainActivity.uncaughtException :"+e.getLocalizedMessage());
				deUncaughtExceptionHandler.uncaughtException(t, e);
			}
		});
	}

	public void onClickExecute(View view) {
		switch (view.getId()) {
			case R.id.btnExecuteSameProc: {
				Intent intent = new Intent(this, ActivityTestCrashOnSameProcess.class);
				startActivity(intent);
				break;
			}
			case R.id.btnExecuteOtherProc: {
				Intent intent = new Intent(this, ActivityTestCrashOnOtherProcess.class);
				startActivity(intent);
				break;
			}
			case R.id.btn_go_naver_demo: {
				Intent intent = new Intent(MainActivity.this, SpeechDemoNaverActivity.class);
				startActivity(intent);
				break;
			}
			case R.id.btn_go_kakao_demo: {
				Intent intent = new Intent(MainActivity.this, SpeechDemoKakaoActivity.class);
				startActivity(intent);
				break;
			}
			case R.id.btn_go_google_demo: {
				Intent intent = new Intent(MainActivity.this, SpeechDemoGoogleActivity.class);
				startActivity(intent);
				break;
			}
			default:
				break;
		}
	}
}
