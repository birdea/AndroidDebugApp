package com.risewide.bdebugapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.risewide.bdebugapp.util.SLog;

/**
 * Created by birdea on 2017-05-29.
 */

public class BaseActivity extends AppCompatActivity {

	private static String TAG = "BaseActivity";

	private String getClassName() {
		return getClass().getSimpleName();
	}

	public BaseActivity() {
		TAG = getClassName();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SLog.d(TAG, "onCreate : "+getClassName());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		SLog.d(TAG, "onDestroy : "+getClassName());
	}

	@Override
	protected void onStart() {
		super.onStart();
		SLog.d(TAG, "onStart : "+getClassName());
	}

	@Override
	protected void onResume() {
		super.onResume();
		SLog.d(TAG, "onResume : "+getClassName());
	}

	@Override
	protected void onPause() {
		super.onPause();
		SLog.d(TAG, "onPause : "+getClassName());
	}

	@Override
	protected void onStop() {
		super.onStop();
		SLog.d(TAG, "onStop : "+getClassName());
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		SLog.d(TAG, "onSaveInstanceState : "+getClassName());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		SLog.d(TAG, "onRestoreInstanceState : "+getClassName());
	}
}
