package com.risewide.bdebugapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.risewide.bdebugapp.util.SVLog;

/**
 * Created by birdea on 2017-05-29.
 */

public class BaseActivity extends AppCompatActivity {

	private String getClassName() {
		return getClass().getSimpleName();
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SVLog.d("onCreate : "+getClassName());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		SVLog.d("onDestroy : "+getClassName());
	}

	@Override
	protected void onStart() {
		super.onStart();
		SVLog.d("onStart : "+getClassName());
	}

	@Override
	protected void onResume() {
		super.onResume();
		SVLog.d("onResume : "+getClassName());
	}

	@Override
	protected void onPause() {
		super.onPause();
		SVLog.d("onPause : "+getClassName());
	}

	@Override
	protected void onStop() {
		super.onStop();
		SVLog.d("onStop : "+getClassName());
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		SVLog.d("onSaveInstanceState : "+getClassName());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		SVLog.d("onRestoreInstanceState : "+getClassName());
	}
}
