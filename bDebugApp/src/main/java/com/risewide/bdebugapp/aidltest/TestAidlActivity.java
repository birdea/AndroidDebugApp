package com.risewide.bdebugapp.aidltest;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;

import com.risewide.bdebugapp.BaseActivity;
import com.risewide.bdebugapp.R;
import com.risewide.bdebugapp.communication.util.TToast;

import java.util.Map;

/**
 * Created by birdea on 2017-09-05.
 */

public class TestAidlActivity extends BaseActivity {

	TestAidlConnector testAidlConnector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_aidl);
		//
		initView();
		initCont();
	}

	private void initView() {
	}

	private void initCont() {
		testAidlConnector = new TestAidlConnector();
	}

	public void onClickView(View view) {
		switch (view.getId()) {
			case R.id.btnConnect:
				handleConnect();
				break;
			case R.id.btnDisconnect:
				handleDisconnect();
				break;
			case R.id.btnGetSimpleMessage:
				handleGetSimpleMessage();
				break;
			case R.id.btnGetComplexMessage:
				handleGetComplexMessage();
				break;

		}
	}

	private void handleGetComplexMessage() {
	}

	private void handleGetSimpleMessage() {
	}

	private void handleDisconnect() {
		//testAidlConnector.disconnect(this);
	}

	private void handleConnect() {
		//testAidlConnector.connect(this);
	}
}
