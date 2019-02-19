package com.risewide.bdebugapp;

import com.github.florent37.viewanimator.ViewAnimator;
import com.risewide.bdebugapp.aidltest.TestAidlActivity;
import com.risewide.bdebugapp.aidltest.TestAidlService;
import com.risewide.bdebugapp.communication.MessageReaderTestActivity;
import com.risewide.bdebugapp.communication.MessageSenderTestActivity;
import com.risewide.bdebugapp.communication.util.TToast;
import com.risewide.bdebugapp.external.SpeechDemoGoogleActivity;
import com.risewide.bdebugapp.external.SpeechDemoKakaoActivity;
import com.risewide.bdebugapp.external.SpeechDemoNaverActivity;
import com.risewide.bdebugapp.process.ActivityTestCrashOnOtherProcess;
import com.risewide.bdebugapp.process.ActivityTestCrashOnSameProcess;
import com.risewide.bdebugapp.process.ExecuterAdbShellCommand;
import com.risewide.bdebugapp.receiver.AladdinCallManager;
import com.risewide.bdebugapp.receiver.CallHelper;
import com.risewide.bdebugapp.reflect.TestReflect;
import com.risewide.bdebugapp.util.AudioFocusManager;
import com.risewide.bdebugapp.util.PermissionHelper;
import com.risewide.bdebugapp.util.SLog;
import com.risewide.bdebugapp.util.TelephonyHelper;
import com.risewide.bdebugapp.util.stringconverter.KoreanJosaStringConverterTest;
import com.skt.prod.voice.v2.aidl.ISmartVoice;
import com.skt.prod.voice.v2.aidl.ITextToSpeechCallback;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import custom.ChromeBarType;
import custom.ChromeBarView;

public class MainActivity extends BaseActivity implements AudioManager.OnAudioFocusChangeListener{

	private static final String TAG = "MainActivity";

	private Thread.UncaughtExceptionHandler deUncaughtExceptionHandler;
	private ISmartVoice iSmartVoice;
	private AladdinCallManager aladdinCallManager;
	private ChromeBarView chromeBarView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		AudioFocusManager.getInstance().init(this, this);

		deUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				SLog.w(TAG, "MainActivity.uncaughtException :"+e.getLocalizedMessage());
				deUncaughtExceptionHandler.uncaughtException(t, e);
			}
		});

		aladdinCallManager = new AladdinCallManager(this);

		if (!PermissionHelper.hasPermission(this, Manifest.permission.CALL_PHONE)) {
			SLog.w(TAG, "requestPermissions CALL_PHONE permission ");
			ActivityCompat.requestPermissions(this,
					new String[] {Manifest.permission.CALL_PHONE},
					1);
		}

        if (!PermissionHelper.hasPermission(this, Manifest.permission.READ_CALL_LOG)) {
            SLog.w(TAG, "requestPermissions CALL_PHONE permission ");
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS},
                    1);
        }

        if (!PermissionHelper.hasPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            SLog.w(TAG, "requestPermissions CALL_PHONE permission ");
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_PHONE_STATE},
                    1);
        }

        chromeBarView = findViewById(R.id.chrome_bar_view);

		checkPermission();
	}

	private void checkPermission() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			boolean needRequestPermission = false;
			for (String permission : PermissionHelper.PERMISSION_REQUEST_LIST) {
				if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
					needRequestPermission = true;
					break;
				}
			}
			if (needRequestPermission) {
				ActivityCompat.requestPermissions(this, PermissionHelper.PERMISSION_REQUEST_LIST, 1);
				return;
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService();
		aladdinCallManager.destroy();

		AudioFocusManager.getInstance().release(this, this);

		deUncaughtExceptionHandler = null;
		iSmartVoice = null;
		iTextToSpeechCallback = null;
		mConnection = null;
		//System.gc();
	}

	AudioManager mAudioManager;
	private AudioManager getAudioManager() {
		if (mAudioManager == null) {
			mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		}
		return mAudioManager;
	}


//	public static final int MODE_INVALID            = -2;
//	public static final int MODE_CURRENT            = -1;
//	public static final int MODE_NORMAL             = 0;
//	public static final int MODE_RINGTONE           = 1;
//	public static final int MODE_IN_CALL            = 2;
//	public static final int MODE_IN_COMMUNICATION   = 3;
//	public static final int NUM_MODES               = 4;

	private String[] mAudioModes = {
		"MODE_CURRENT",
		"MODE_NORMAL",
		"MODE_RINGTONE",
		"MODE_IN_CALL",
		"MODE_IN_COMMUNICATION"
	};

	private void setAudioMode() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("pick a message")
				.setItems(mAudioModes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						TToast.show(getApplicationContext(), "selected:"+which+"->"+mAudioModes[which]);
						getAudioManager().setMode(which-1);
					}
				});
		builder.create().show();
	}

	private void getCallLog() {
		String[] projection = { CallLog.Calls.CONTENT_TYPE, CallLog.Calls.NUMBER, CallLog.Calls.DURATION, CallLog.Calls.DATE };

		Cursor cur = managedQuery(CallLog.Calls.CONTENT_URI, null, CallLog.Calls.TYPE + "= ?",
				new String[]{ String.valueOf(CallLog.Calls.OUTGOING_TYPE) },                CallLog.Calls.DEFAULT_SORT_ORDER);

		log("db count=" + String.valueOf(cur.getCount()));
		log("db count=" + CallLog.Calls.CONTENT_ITEM_TYPE);
		log("db count=" + CallLog.Calls.CONTENT_TYPE);

		if(cur.moveToFirst() && cur.getCount() > 0) {
			while(cur.isAfterLast() == false) {
				StringBuffer sb = new StringBuffer();

				sb.append("call type=").append(cur.getString(cur.getColumnIndex(CallLog.Calls.TYPE)));
				sb.append(", cashed name=").append(cur.getString(cur.getColumnIndex(CallLog.Calls.CACHED_NAME)));
				sb.append(", content number=").append(cur.getString(cur.getColumnIndex(CallLog.Calls.NUMBER)));
				sb.append(", duration=").append(cur.getString(cur.getColumnIndex(CallLog.Calls.DURATION)));
				sb.append(", new=").append(cur.getString(cur.getColumnIndex(CallLog.Calls.NEW)));
				//sb.append(", date=").append(timeToString(cur.getLong(cur.getColumnIndex(CallLog.Calls.DATE)))).append("]");
				cur.moveToNext();
				log("call history["+sb.toString());

			}
		}
	}

    private void getContactList() {
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        log("Name: " + name);
                        log("Phone Number: " + phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }
    }

    private void selectChromeType() {
        final String[] types = ChromeBarType.getTypes();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("pick one..");
        builder.setItems(types, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                chromeBarView.setBarType(ChromeBarType.valueOf(types[which]));
                dialog.dismiss();
            }
        });
        builder.show();
    }

	public static final String AUTHORITY    = "com.skt.aidev.nugujuniorphone.provider";

	/** ContentProvider 제공 클래스에서 받을 uri.getPathSegments()를 등록해 준다
	 *  << content://" + AUTHORITY + PATH_FIRST_RUN>> 다음부터 getPathSegments[0] = PATH_FIRST_RUN,
	 * [1], [2], [3]... 순으로 나간다.
	 */
	public static final String PATH_FIRST_RUN = "/first_run/boolean";
	public static final String PATH_IMAGE_TYPE = "/flickup_image_type";

	/** CotentProvider 접근을 위한 ContentResolver 객체를 생성할 때 넣어 주는 매개변수에
	 *  URI를 사용 한다.
	 */
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + PATH_FIRST_RUN);
	public static final Uri CONTENT_URI2 = Uri.parse("content://" + AUTHORITY + PATH_IMAGE_TYPE);

    private void getContentProviderValue() {
		ContentResolver cr = getContentResolver();
		Cursor cursor = cr.query(CONTENT_URI, null, null, null, null);
		int result1 = -1, result2 = -1;
		if (cursor != null) {
			if (cursor.moveToNext()) {
				result1 = cursor.getInt(0);
			}
		}
		log("result1 = " + result1);

		Cursor cursor2 = cr.query(CONTENT_URI2, null, null, null, null);
		if (cursor2 != null) {
			if (cursor2.moveToNext()) {
				result2 = cursor2.getInt(0);
			}
		}
		log("result2 = " + result2);
		TToast.show(this, "getContentProviderValue:"+result1+","+result2);
	}

	private void setAudioVolume() {
		AudioManager audioManager = getAudioManager();
		int beepStream = AudioManager.STREAM_NOTIFICATION;
		int volume = 3;
		int volBeep = audioManager.getStreamVolume(beepStream);
		int volMax = audioManager.getStreamMaxVolume(beepStream);
		SLog.w(TAG, "onChanged() volBeep:"+volBeep+", volMax:"+volMax+", volMid:"+volume);
		audioManager.setStreamVolume(beepStream, volume, 0);
	}

	StatePhoneReceiver myPhoneStateListener;

	private void callSpeakerPhone(String phoneNumber, boolean speakOn) {

		//AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		//audioManager.setMode(AudioManager.MODE_IN_CALL);
		//audioManager.setSpeakerphoneOn(speakOn);

		if (myPhoneStateListener == null) {
			myPhoneStateListener = new StatePhoneReceiver(this);
		}
		myPhoneStateListener.setSpeakerOn(speakOn);

		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		tm.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

		TelephonyHelper.connectCallWithPhoneNumber(this, phoneNumber);
	}

	// Monitor for changes to the state of the phone
	public class StatePhoneReceiver extends PhoneStateListener {
		private boolean flagSpeakerOn = false;
		private boolean flagCallHook = false;
		private TelephonyManager tm;

		public StatePhoneReceiver(Context context) {
			tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		}

		public void setSpeakerOn(boolean flag) {
			flagSpeakerOn = flag;
			flagCallHook = false;
		}

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			SLog.w(TAG, "onCallStateChanged() state:"+state);
			switch (state) {
				case TelephonyManager.CALL_STATE_OFFHOOK: //Call is established
					if (flagSpeakerOn) {
						flagSpeakerOn=false;
						flagCallHook=true;

						try {
							Thread.sleep(500); // Delay 0,5 seconds to handle better turning on loudspeaker
						} catch (InterruptedException e) {
						}

						//Activate loudspeaker
						AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
						audioManager.setMode(AudioManager.MODE_IN_CALL);
						audioManager.setSpeakerphoneOn(true);
					}
					break;
				case TelephonyManager.CALL_STATE_IDLE: //Call is finished
					if (flagCallHook) {
						flagCallHook=false;
						AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
						audioManager.setMode(AudioManager.MODE_NORMAL); //Deactivate loudspeaker
						tm.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);// Remove listener
					}
					break;
			}
		}
	}

	private void showCurrentAssistInfo() {
		Context context = getApplicationContext();
		ComponentName cpName;
		cpName = MainAssistActivity.getCurrentAssist(context);
		String out1 = (cpName!=null)?cpName.flattenToShortString():"null";
		cpName = MainAssistActivity.getCurrentAssistWithReflection(context);
		String out2 = (cpName!=null)?cpName.flattenToShortString():"null";
		TToast.show(context, "[currentAssist]\n> "+out1+"\n> "+out2);
	}

	public void onClickView(View view) {
		switch (view.getId()) {
			case R.id.btnGetAssistApp: {
				showCurrentAssistInfo();
				break;
			}
			case R.id.btnSetAssistApp: {
				startActivity(new Intent(android.provider.Settings.ACTION_VOICE_INPUT_SETTINGS));
				break;
			}
			case R.id.btnCallSpeakerPhoneOn: {
				callSpeakerPhone("01020259580",true);
				break;
			}
			case R.id.btnCallSpeakerPhoneOff: {
				callSpeakerPhone("01020259580",false);
				break;
			}
			case R.id.btnSetAudioVolume: {
				setAudioVolume();
				break;
			}
			case R.id.btnGetContentProviderValue: {
				getContentProviderValue();
				break;
			}
			case R.id.btnTestVoiceChrome: {
                selectChromeType();
				break;
			}
			case R.id.btnSeeCallLog: {
				getCallLog();
				break;
			}
			case R.id.btnSeeContact: {
                getContactList();
				break;
			}
			case R.id.btnGoCallStart: {
				CallHelper.connectCallWithPhoneNumber(MainActivity.this, "01020259580");
				break;
			}
			case R.id.btnGoCallEnd: {
				CallHelper.disconnectCall(getApplicationContext());
				break;
			}
			case R.id.btnGoCallLogView: {
				/*Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setType(CallLog.Calls.CONTENT_TYPE);
				startActivity(intent);*/
				//
				// Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("content://call_log/calls"));
				// com.android.dialer/.DialtactsActivity

				Intent i = new Intent();
				i.setComponent(new ComponentName("com.android.dialer", "com.android.dialer.DialtactsActivity"));
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);

				break;
			}
			case R.id.btnGoContactView: {
				Intent i = new Intent();
				//i.setComponent(new ComponentName("com.samsung.android.contacts", "com.android.dialer.DialtactsActivity"));
				i.setComponent(new ComponentName("com.android.contacts", "com.android.contacts.activities.PeopleActivity"));
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);

				// com.android.contacts/.activities.PeopleActivity
				//
				/*Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
				startActivity(intent);*/
				//
				/*Intent intent = new Intent(ContactsContract.Intents.SHOW_OR_CREATE_CONTACT,
						Uri.parse("tel:"));
				intent.putExtra(ContactsContract.Intents.EXTRA_FORCE_CREATE, true);
				startActivity(intent);*/
				//
				/*Intent i = new Intent(Intent.ACTION_INSERT_OR_EDIT);
				i.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
				startActivity(i);*/
				break;
			}
			case R.id.btnSpeakerOn: {
				AudioManager am = getAudioManager();
				am.setSpeakerphoneOn(true);
				break;
			}
			case R.id.btnSpeakerOff: {
				AudioManager am = getAudioManager();
				am.setSpeakerphoneOn(false);
				break;
			}
			case R.id.btnBtScoOn: {
				AudioManager am = getAudioManager();
				am.setBluetoothScoOn(true);
				am.startBluetoothSco();
				break;
			}
			case R.id.btnBtScoOff: {
				AudioManager am = getAudioManager();
				am.stopBluetoothSco();
				am.setBluetoothScoOn(false);
				break;
			}
			case R.id.btnAudioMode: {
				setAudioMode();
				break;
			}
			case R.id.btnTestAnim: {
				ViewAnimator.animate(view).shake().interpolator(new LinearInterpolator()).start();
				break;
			}
			case R.id.btnSetOnReflect: {
				TestReflect.changeByReflection(getApplicationContext());
				break;
			}
			case R.id.btnExecuteAdbCommand: {
				EditText etAdbCommand = (EditText) findViewById(R.id.etAdbCommand);
				String cmd = etAdbCommand.getText().toString();
				ExecuterAdbShellCommand.exec(cmd);
				break;
			}
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
		SLog.d(TAG, msg);
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
