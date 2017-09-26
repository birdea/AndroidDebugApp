package com.risewide.bdebugapp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.risewide.bdebugapp.aidltest.TestAidlActivity;
import com.risewide.bdebugapp.aidltest.TestAidlService;
import com.risewide.bdebugapp.communication.MessageReaderTestActivity;
import com.risewide.bdebugapp.communication.MessageSenderTestActivity;
import com.risewide.bdebugapp.external.SpeechDemoGoogleActivity;
import com.risewide.bdebugapp.external.SpeechDemoKakaoActivity;
import com.risewide.bdebugapp.external.SpeechDemoNaverActivity;
import com.risewide.bdebugapp.process.ActivityTestCrashOnOtherProcess;
import com.risewide.bdebugapp.process.ActivityTestCrashOnSameProcess;
import com.risewide.bdebugapp.util.AudioFocusManager;
import com.risewide.bdebugapp.util.SLog;
import com.risewide.bdebugapp.util.stringconverter.KoreanJosaStringConverterTest;
import com.skt.prod.voice.v2.aidl.ISmartVoice;
import com.skt.prod.voice.v2.aidl.ITextToSpeechCallback;

public class MainActivity extends BaseActivity implements AudioManager.OnAudioFocusChangeListener{

	private Thread.UncaughtExceptionHandler deUncaughtExceptionHandler;
	private ISmartVoice iSmartVoice;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		AudioFocusManager.getInstance().init(this, this);

		deUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				SLog.w("MainActivity.uncaughtException :"+e.getLocalizedMessage());
				deUncaughtExceptionHandler.uncaughtException(t, e);
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService();

		AudioFocusManager.getInstance().release(this, this);

		deUncaughtExceptionHandler = null;
		iSmartVoice = null;
		iTextToSpeechCallback = null;
		mConnection = null;
		//System.gc();
	}

	public void onClickView(View view) {
		switch (view.getId()) {
			case R.id.btnKoreanJosaConverterTest: {
				new KoreanJosaStringConverterTest().test();
				break;
			}
			case R.id.btnAidlService: {
				Intent intent = new Intent(this, TestAidlService.class);
				startService(intent);
				break;
			}
			case R.id.btnAidlActivity: {
				Intent intent = new Intent(this, TestAidlActivity.class);
				startActivity(intent);
				break;
			}
			case R.id.btnCommunicationMessageReader: {
				Intent intent = new Intent(this, MessageReaderTestActivity.class);
				startActivity(intent);
				break;
			}
			case R.id.btnCommunicationMessageSender: {
				Intent intent = new Intent(this, MessageSenderTestActivity.class);
				startActivity(intent);
				break;
			}
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
			case R.id.btn_service_bind: {
				bindService();
				break;
			}
			case R.id.btn_service_unbind: {
				unbindService();
				break;
			}
			case R.id.btn_tts_play: {
				playTTS();
				break;
			}
			case R.id.btn_tts_stop: {
				stopTTS();
				break;
			}
			default:
				break;
		}
	}
	private void log(String msg) {
		SLog.i("DebugAppTest", msg);
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			log("bindService [o] onServiceConnected:"+name);
			iSmartVoice = ISmartVoice.Stub.asInterface(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			log("bindService [o] onServiceDisconnected:"+name);
			iSmartVoice = null;
		}
	};


	private void bindService() {
//		if (mConnection != null) {
//			log("bindService [x] (has alread)");
//			return;
//		}
		log("bindService [o] start");
		//
		Intent intent = new Intent();//"com.skt.prod.voice.controller.SmartVoice"
		//intent.setClassName(this, "com.skt.prod.voice.controller.ControlCenterService");
		intent.setClassName("com.skt.prod.voice.v2.voiceapptester", "com.skt.prod.voice.v2.controller.ControlCenterService");
		//intent.setAction("com.skt.prod.voice.aidl.ISmartVoice");
		intent.setPackage("com.skt.prod.voice.v2.voiceapptester");
		boolean isBind = getApplicationContext().bindService(intent, mConnection, BIND_AUTO_CREATE);
		log("bindService end isBind:"+isBind);
	}

	private void unbindService() {
//		if (mConnection == null) {
//			log("unbindService [x] (has null)");
//			return;
//		}

		if (null != mConnection) {
			log("unbindService [o] start");
			try {
				unbindService(mConnection);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//mConnection = null;
			log("unbindService [o] end");
		}
	}

	private ITextToSpeechCallback iTextToSpeechCallback = new ITextToSpeechCallback.Stub() {
		@Override
		public void onStart(String s) throws RemoteException {
			log("remoteTTS[Res]:onStart:" + s);
		}

		@Override
		public void onStop(String s) throws RemoteException {
			log("remoteTTS[Res]:onStop:" + s);
		}

		@Override
		public void onInfo(String s) throws RemoteException {
			log("remoteTTS[Res]:onInfo:" + s);
		}

		@Override
		public void onUnavailable(String s) throws RemoteException {
			log("remoteTTS[Res]:onUnavailable:" + s);
		}

		@Override
		public void onError(int i, String s, String s1) throws RemoteException {
			log("remoteTTS[Res]:onError:" + i+", s:"+s +", s1:"+s1);
		}

		@Override
		public void onFinish(String s) throws RemoteException {
			log("remoteTTS[Res]:onFinish:" + s);
		}

		@Override
		public IBinder asBinder() {
			return this;
		}
	};

	private void playTTS(){
		if(iSmartVoice == null) {
			log("remoteTTS[Req]:err, iSmartVoice is null");
			return;
		}
		log("remoteTTS[Req]:start!");
		String speech = getSpeechText();
		log("remoteTTS[Req]:getSpeechText:"+speech);

		try {
			iSmartVoice.playTTS(speech, true, iTextToSpeechCallback);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void stopTTS() {
		if(iSmartVoice == null) {
			log("remoteTTS[Req]:err, iSmartVoice is null");
			return;
		}
		//
		try {
			iSmartVoice.stopTTS();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private String getSpeechText() {
		final String example = "안녕하세요 테스트 중입니다.";
		EditText et_speech_msg = (EditText) findViewById(R.id.et_speech_msg);
		String speechMsg = et_speech_msg.getText().toString();
		//
		if (TextUtils.isEmpty(speechMsg)) {
			return example;
		}
		//
		return speechMsg;
	}

	@Override
	public void onAudioFocusChange(int focusChange) {
		log("onAudioFocusChange > focusChange : "+focusChange+", "+ AudioFocusManager.getAudioFocusStatus(focusChange));
	}
}
