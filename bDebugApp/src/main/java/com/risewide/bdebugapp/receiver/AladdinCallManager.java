package com.risewide.bdebugapp.receiver;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.android.internal.telephony.ITelephony;
import com.risewide.bdebugapp.R;
import com.risewide.bdebugapp.util.ArrayHelper;
import com.risewide.bdebugapp.util.AudioManagerHelper;
import com.risewide.bdebugapp.util.BluetoothHelper;
import com.risewide.bdebugapp.util.ContactInfoManager;
import com.risewide.bdebugapp.util.DateTimeHelper;
import com.risewide.bdebugapp.util.PhoneNumberHelper;
import com.risewide.bdebugapp.util.SLog;
import com.risewide.bdebugapp.util.SystemServiceHelper;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * 전화 수발신 동작 관련 처리.
 * - 전화 수발신 관련 이벤트 구별.
 * - 화면 복귀 동작.
 *
 * @author birdea, hyunho.mo
 *
 * @since 2017.07.11 created by bridea.
 * @since 2017.08.11 refactoring to apply AladdinManagerBase by hyunho.mo
 */
public class AladdinCallManager{
    private final static String TAG = AladdinCallManager.class.getSimpleName();

    private static final int DELAY_TO_HANDLE_INCOMING_CALL = 5000;  // 5 sec.
    private static final int INTERVAL_TIME_TO_CHECK_SPEAKER_PHONE_ON = 300; // 300 ms.
    private static final int INTERVAL_TIME_TO_CEHCK_FOREGROUND = 1000; // 1 sec.
    private static final int DELAY_TO_RELEASE_OUR_OUTGOING_CALL = 10000;    // 10 sec.

    private Context mContext = null;
    private AudioManager mAudioManager = null;

    private String mLastRecvDispName = null;
    private String mLastRecvPhoneNumber = null;

    private boolean isMuteBellAsRingerMode = true;
    private int mPrevRingerMode = -1;
    private int mPrevRingVolume = -1;
    private int mPrevAlarmVolume = -1;

    private String mForegroundableActivity = null;
    private Thread mForegroundThread = null;
    private Thread mSpeakerPhoneThread = null;

    private Handler mHandler = new Handler();
    private Runnable mIncomingCallRunnable = null;

    private boolean mIsOurOutgoingCall = false;
    private boolean mIsIncomingCallEnabled = true;

    private Runnable mReleaseOurOutgoingCallRunnable = new Runnable() {
        /**
         */
        @Override
        public void run() {
            SLog.i(TAG, "mReleaseOurOutgoingCallRunnable.run()");

            mIsOurOutgoingCall = false;
        }
    };

    private ArrayList<WaitingCallInfo> mWaitingCallList = new ArrayList<>();
    private BluetoothHelper bluetoothHelper;


    /**
     */
    public AladdinCallManager(Context context) {
        mContext = context;
        mAudioManager = SystemServiceHelper.getAudioManager(mContext);

        registerCallStateReceiver();

        bluetoothHelper = BluetoothHelper.getInstance(mContext);
        bluetoothHelper.registerHeadsetReceiver();
		
        // O OS 버전에서 전화수신시에 AudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT)가 동작하지 않아
        // O OS 버전에서만 Volume Down(0 Level)로 벨소리를 Mute 처리.
        isMuteBellAsRingerMode = (Build.VERSION.SDK_INT != 26);
    }

    /**
     */
    private void registerCallStateReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        filter.addAction(CallHelper.ACTION_INNER_OUTGOING_CALL);
        mContext.registerReceiver(mCallStateReceiver, filter);
    }

    private void unregisterCallStateReceiver() {
		try {
			mContext.unregisterReceiver(mCallStateReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /**
     */
    private CallStateReceiver mCallStateReceiver = new CallStateReceiver() {

        /**
         * @param callState
         * @param callSubState
         * @param phoneNumber
         * @param startTime
         * @param endTime
         */
        @Override
        protected void onCallStateChanged(CallState callState, CallSubState callSubState,
                String phoneNumber, long startTime, long endTime) {

            SLog.d(TAG, String.format("onCallStateChanged() : callState(%s), CallSubState(%s), phoneNumber(%s), time(%s, %s)",
                    callState, callSubState, phoneNumber,
                    DateTimeHelper.format(DateTimeHelper.DATE_TIME_FORMAT_PATTERN_1, startTime),
                    DateTimeHelper.format(DateTimeHelper.DATE_TIME_FORMAT_PATTERN_1, endTime)));

            // 특정 단말에서 발신자 표시제한 번호로 전화수신시에 phoneNumber 정보에 임의의 Text(anonymouse)가 설정된 상태로 전달되는 경우가 있어
            // 전달되는 phoneNumber에 대한 유효성을 확인하여 유효하지 않은 phoneNumber에 대해서는 discard 처리 한다.
            if (!PhoneNumberHelper.isValidPhoneNumger(phoneNumber)) {
                SLog.i(TAG, String.format("onCallStateChanged() : %s phoneNumber is invalid.", phoneNumber));
                phoneNumber = null;
            }

            // 대기통화 수신 처리.
            if (CallSubState.WAITING_CALL_RECEIVED.equals(callSubState)) {
                if (mIsIncomingCallEnabled) {
                    SLog.d(TAG, "onCallStateChanged() : handle a call waiting.");

                    // 대기통화 Disconnect.
                    CallHelper.disconnectCall(mContext);

                    // 대기통화가 수신되어 전화앱이 다시 실행되는 경우를 위해 Foregroundable Activity를 Foreground로 올림.
                    startForegroundActivityThread();

                    String waitingCallName = ContactInfoManager.getInstance(mContext).getContactNameWithPhoneNumber(phoneNumber);
                    String waitingCallPhoneNumber = PhoneNumberHelper.convertFormattedPhoneNumber(phoneNumber);

                    mWaitingCallList.add(new WaitingCallInfo(waitingCallName, waitingCallPhoneNumber, startTime));

                    notifyOnCallStateChanged(callState, callSubState, waitingCallName, waitingCallPhoneNumber, startTime, endTime);
                }
                return;
            }

            //getAladdinAiCloudManager().onCallStateChanged(callState);

            mLastRecvDispName = ContactInfoManager.getInstance(mContext).getContactNameWithPhoneNumber(phoneNumber);
            mLastRecvPhoneNumber = PhoneNumberHelper.convertFormattedPhoneNumber(phoneNumber);


            switch (callSubState) {
                case INCOMING_CALL_RECEIVED:
                    if (mIsIncomingCallEnabled && mIncomingCallRunnable == null) {
                        if (CallHelper.isForeground(mForegroundableActivity)) {
                            // 볼륨값 읽어올 때 타이밍 문제 존재하여 saveRingVolume()을 여기에 위치 시킴.
                            if (!isMuteBellAsRingerMode) {
                                saveRingVolume();
                            }

                            // Incoming Call 발생시 곧 수행할 Incoming Call에 대한 동작을 위해
                            // 발화중인 TTS와 다른 발화를 받지 못하도록 켜져 있는 ASR을 Cancel 시키며,
                            // 진행중인 MultiTurn 동작을 Stop 시킨다.
                            cancelTTS();
                            cancelAsr();
							//getAladdinMultiTurnManager().stopInternalMultiTurn();

							postHandleIncomingCallReceived(mLastRecvDispName, mLastRecvPhoneNumber);

                            startForegroundActivityThread();
                        }
                    }
                    break;

                case INCOMING_CALL_ANSWERED:
                    if (mIsIncomingCallEnabled) {
                        if (CallHelper.isForeground(mForegroundableActivity)) {
                            startSpeakerphoneOnThread(true);
                            startForegroundActivityThread();

                            bluetoothHelper.connectHeadset(false);
                        }

                        restoreIncomingCallReceived();

                        clearWaitingCallList();
                    }
                    break;

                case INCOMING_CALL_ENDED:
                    if (mIsIncomingCallEnabled) {
                        stopForegroundActivityThread();

                        restoreIncomingCallReceived();
                    }

                    if (CallHelper.isForeground(mForegroundableActivity)) {
                        if (mIsIncomingCallEnabled) {
                            notifyWaitingCallOnAIServiceResult();
                        }

                        List<BluetoothDevice> bluetoothDeviceList = bluetoothHelper.getBluetoothHeadsetList();
                        if (bluetoothDeviceList != null && bluetoothDeviceList.size() > 1) {
                            bluetoothHelper.disConnectHeadset(false);
                        }
                    }
                    break;

                case MISSED_CALL:
                    if (mIsIncomingCallEnabled) {
                        stopForegroundActivityThread();

                        restoreIncomingCallReceived();

                        bluetoothHelper.connectHeadset(true);
                    }
                    break;

                case OUTGOING_CALL_STARTED:
                    if (mIsOurOutgoingCall) {
                        mHandler.removeCallbacks(mReleaseOurOutgoingCallRunnable);
                        mIsOurOutgoingCall = false;

                        startSpeakerphoneOnThread(true);
                        startForegroundActivityThread();

                        clearWaitingCallList();
                    } else {
                        SLog.i(TAG, "onCallStateChanged() : Outgoing call is not ours.");
                    }
                    break;

                case OUTGOING_CALL_ENDED:
                    if (CallHelper.isForeground(mForegroundableActivity)) {
                        notifyWaitingCallOnAIServiceResult();
                    }
                    break;

                default:
                    break;
            }

            notifyOnCallStateChanged(callState, callSubState, mLastRecvDispName,
                    mLastRecvPhoneNumber, startTime, endTime);
        }
    };

    /**
     * @param callState
     * @param callSubState
     * @param name
     * @param phoneNumber
     * @param startTime
     * @param endTime
     */
    private void notifyOnCallStateChanged(CallState callState, CallSubState callSubState,
            String name, String phoneNumber, long startTime, long endTime) {
        /*try {
            IAladdinServiceMonitorCallback monitor = getMonitor();
            if (monitor != null) {
                SLog.d(TAG, String.format("notifyOnCallStateChanged() : callState(%s), CallSubState(%s), name(%s), phoneNumber(%s), time(%s, %s)",
                        callState, callSubState, name, phoneNumber,
                        DateTimeHelper.format(DateTimeHelper.DATE_TIME_FORMAT_PATTERN_1, startTime),
                        DateTimeHelper.format(DateTimeHelper.DATE_TIME_FORMAT_PATTERN_1, endTime)));

                monitor.onCallStateChanged(callState, callSubState, name, phoneNumber, startTime, endTime);
            }
        } catch (RemoteException e) {
            SLog.e(TAG, String.format("notifyOnCallStateChanged() : RemoteException(%s)", e.getMessage()));
        }*/
    }

    /**
     * @return
     */
    public String getLastReceivedDisplayName() {
        return mLastRecvDispName;
    }

    /**
     * @return
     */
    public String getLastReceivedPhoneNumber() {
        return mLastRecvPhoneNumber;
    }

    /**
     * 전달받은 전화 수신 메시지를 TTS로 발화함.
     * 세부적으로 다음 작업들 수행.
     * - 전화벨 볼륨을 줄임
     * - TTS 발화
     * 참고. 일부 LG단말에서 무음처리 시 전화수신 미니모드 창이 사라지는 문제가 있어 볼륨을 줄이도록 처리함.
     *
     * @param contactName
     * @param phoneNumber
     */
    private void postHandleIncomingCallReceived(final String contactName, final String phoneNumber) {
        SLog.d(TAG, "postHandleIncomingCallReceived()");

        mIncomingCallRunnable = new Runnable() {
            /**
             */
            @Override
            public void run() {
                SLog.d(TAG, "postHandleIncomingCallReceived().run() : run mIncomingCallRunnable.");

                mIncomingCallRunnable = null;

                /*CallState callState = getAladdinCallManager().getCallState();
                if (callState != CallState.RINGING) {
                    return;
                }*/

                // 벨소리 무음 또는 줄이기
                if (isMuteBellAsRingerMode) {
                    setSilentRingerMode();
                } else {
                    setRingVolume(0.f);
                }

                // 전화수신용 TTS 발화를 위한 Alarm 볼륜을 저장하고 Music 볼륨의 값으로 설정.
                saveAndSetAlarmVolume();

                // BT(통화 기능)가 연결 되었을 경우 해당 디바이스 disconnect
                bluetoothHelper.clearBluetoothHeadsetList();
                bluetoothHelper.disConnectHeadset(true);

                /*NuguSDKErrorManager nuguSDKErrorManager = getNuguSDKErrorManager();
                if (nuguSDKErrorManager.hasPermissionAndNotifyOnError(mContext, CommunicationConst.CommunicationPermissionType.READ_CALL_LOG)) {
                    String[] loadPhoneNumbers = {StringHelper.removeCharAll(phoneNumber, '-')};
                    CallLogSearcher.getInstance(mContext).findLastCallLogInfo(loadPhoneNumbers, new CallLogSearcher.OnFindListener() {
                        *//**
                         * @param resultList
                         * @param resultType
                         *//*
                        @Override
                        public void onFindCompleted(ArrayList<CallLogInfo> resultList, CallLogSearcher.ResultType resultType) {
                            boolean again = false;
                            switch (resultType) {
                                case FOUND_SUCCESS:
                                    CallLogInfo callLogInfo = resultList.get(0);
                                    if (callLogInfo != null) {
                                        CallLogConst.CallType callType = callLogInfo.getCallType();
                                        long callDate = callLogInfo.getCallDate();
                                        long todayStartTime = DateTimeHelper.getTimeInMillisWithDateAmount(0);

                                        // 가장 최근에 발생된 통화가 Missed Call 이고 현재일 00시 00분 00시 이내에 발생된 통화일 경우,
                                        // '또' 문구가 들어간 TTS가 송출되도록 함.
                                        again = CallLogConst.CallType.MISSED.equals(callType) && (callDate >= todayStartTime);
                                    }
                                    break;

                                case NOT_FOUND_AS_CALLLOG_INFO_EMPTY:
                                case NOT_FOUND_AS_LOAD_CALLLOG_INFO_CANCELED:
                                default:
                                    break;
                            }
                            String tts = getIncomingCallTTS(contactName, phoneNumber, again);
                            notifyIncomingCallReceivedOnAIServiceResult(tts);
                        }
                    });
                } else {
                    String tts = getIncomingCallTTS(contactName, phoneNumber, false);
                    notifyIncomingCallReceivedOnAIServiceResult(tts);
                }*/
            }
        };
        mHandler.postDelayed(mIncomingCallRunnable, DELAY_TO_HANDLE_INCOMING_CALL);
    }

    /**
     * @param contactName
     * @param phoneNumber
     * @param again
     * @return
     */
    private String getIncomingCallTTS(String contactName, String phoneNumber, boolean again) {
        // TTS 발화
        String fromName;
        if (TextUtils.isEmpty(phoneNumber)) {
            fromName = mContext.getString(R.string.from_hidden_number);
        } else if (TextUtils.isEmpty(contactName)) {
            fromName = mContext.getString(R.string.from_unknown_number);
        } else {
            fromName = String.format(mContext.getString(R.string.from_name), contactName);
        }

        int ttsResId = (!again) ? R.string.tts_incoming_call_received : R.string.tts_incoming_call_received_again;
        return mContext.getString(ttsResId, fromName);
    }

    /**
     * 전화 수신 메시지 TTS 발화를 중지함.
     *
     * 세부적으로 다음 작업들 수행.
     * - 벨소리 볼륨 복구
     * - TTS 중지
     */
    private void restoreIncomingCallReceived() {
        SLog.i(TAG, "restoreIncomingCallReceived()");

        if (mIncomingCallRunnable != null) {
            SLog.d(TAG, "restoreIncomingCallReceived() : remove mIncomingCallRunnable.");

            mHandler.removeCallbacks(mIncomingCallRunnable);
            mIncomingCallRunnable = null;
        }

        // 벨소리 볼륨 복구
        if (isMuteBellAsRingerMode) {
            restoreRingerMode();
        } else {
            restoreRingVolume();
        }

        // 전화수신용 TTS 발화를 위한 Alarm 볼륜의 설정을 복원.
        restoreAlarmVolume();

        cancelAsr();
    }

    /**
     * 무음 처리.
     */
    private void setSilentRingerMode() {
        int ringerMode = mAudioManager.getRingerMode();
        if (ringerMode != AudioManager.RINGER_MODE_NORMAL) {
            return;
        }

        String ringerModeLabel = AudioManagerHelper.getRingerModeLabel(ringerMode);
        SLog.i(TAG, String.format("setSilentRingerMode() : ringerMode(%s)", ringerModeLabel));

        mPrevRingerMode = ringerMode;
        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }

    /**
     */
    private void restoreRingerMode() {
        if (mPrevRingerMode != -1) {
            String ringerModeLabel = AudioManagerHelper.getRingerModeLabel(mPrevRingerMode);
            SLog.i(TAG, String.format("restoreRingerMode() : mPrevRingerMode(%s)", ringerModeLabel));

            mAudioManager.setRingerMode(mPrevRingerMode);
            mPrevRingerMode = -1;
        }
    }

    /**
     * 기본 전화벨 볼륨을 저장함
     */
    private void saveRingVolume() {
        mPrevRingVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
        SLog.i(TAG, String.format("saveRingVolume() : mPrevRingVolume(%s)", mPrevRingVolume));
    }

    /**
     * @param maxVolumeRatio
     */
    private void setRingVolume(float maxVolumeRatio) {
        if (maxVolumeRatio < 0.f) {
            return;
        }

        int ringerMode = mAudioManager.getRingerMode();
        if (ringerMode != AudioManager.RINGER_MODE_NORMAL) {
            return;
        }
        int ringMaxVolumeLevel = (int)((float)mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING) * maxVolumeRatio);
        int volumeLevel = (maxVolumeRatio != 0.f) ? Math.max(ringMaxVolumeLevel, 1) : 0;
        mAudioManager.setStreamVolume(AudioManager.STREAM_RING, volumeLevel, AudioManager.FLAG_PLAY_SOUND);
    }

    /**
     * 무음처리나 줄였던 전화벨 볼륨을 기존 상태로 복구함.
     */
    private void restoreRingVolume() {
        if (mPrevRingVolume != -1) {
            SLog.i(TAG, String.format("restoreRingVolume() : mPrevRingVolume(%d)", mPrevRingVolume));

            mAudioManager.setStreamVolume(AudioManager.STREAM_RING, mPrevRingVolume, AudioManager.FLAG_PLAY_SOUND);
            mPrevRingVolume = -1;
        }
    }

    /**
     * 전화수신용 TTS를 Media Volume의 크기로 Alarm Stream을 통해서 송출하기 위해
     * 현 Alarm Volume을 저장하고 Alarm Volume을 Media Volume으로 설정한다.
     */
    private void saveAndSetAlarmVolume() {
        if (!bluetoothHelper.isConnectedBluetoothHeadsetProfile()) {
            SLog.i(TAG, "saveAndSetAlarmVolume() : Bluetooth is not on.");
            return;
        }

        mPrevAlarmVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        int currentMediaVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, currentMediaVolume, 0);

        //getAladdinTTSManager().setAudioStream(AudioManager.STREAM_ALARM);

        SLog.i(TAG, String.format("saveAndSetAlarmVolume() : mPrevAlarmVolume(%s), currentMediaVolume(%d)",
                mPrevAlarmVolume, currentMediaVolume));
    }

    /**
     */
    private void restoreAlarmVolume() {
        if (mPrevAlarmVolume != -1) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, mPrevAlarmVolume, 0);
            mPrevAlarmVolume = -1;
        }

        //getAladdinTTSManager().setAudioStream(AudioManager.STREAM_MUSIC);

        SLog.i(TAG, String.format("restoreAlarmVolume() : mPrevAlarmVolume(%s)", mPrevAlarmVolume));
    }

    /**
     * TTS가 발화중이면 TTS를 Cancel 시킨다.
     */
    private void cancelTTS() {
        SLog.d(TAG, "restoreIncomingCallReceived() : cancel tts.");
        //getAladdinTTSManager().cancel();
    }

    /**
     * 음성입력모드 상태이면 ASR을 Cancel 시킨다.
     */
    private void cancelAsr() {
        // 음성입력모드 상태이면 Recognizer를 Cancel 시킨다.
        //AladdinAiCloudManager aiCloudManager = getAladdinAiCloudManager();
        //if (aiCloudManager.isRecognizing()) {
        //    SLog.d(TAG, "cancelAsr() : cancel asr.");
        //    aiCloudManager.cancelAsr();
        //}
    }

    /**
     * 이어폰이나 블루투스헤드셋 연결 안 된 상태에서 전화 받는 경우 스피커폰 활성화.
     * 이어폰이나 블루투스 연결된 상태에서 강제로 스피커폰 켜야되는 경우 추가 수정 필요.
     *
     * @param on
     */
    public void startSpeakerphoneOnThread(boolean on) {
        boolean isWiredHeadsetOn = mAudioManager.isWiredHeadsetOn();
        boolean isBluetoothOn = bluetoothHelper.isConnectedBluetoothHeadsetProfile() ||
                bluetoothHelper.isProgressingConnectedBluetoothHeadset();

        SLog.d(TAG, String.format("startSpeakerphoneOnThread(%s) : isWiredHeadsetOn(%s), isBluetoothOn(%s)",
                on, isWiredHeadsetOn, isBluetoothOn));

        if (isWiredHeadsetOn || isBluetoothOn) {
            return;
        }

        if (on) {
            mSpeakerPhoneThread = new Thread() {
                /**
                 */
                public void run() {
                    AudioManagerHelper.setSpeakerphoneOn(mContext, true);

                    int count = 0;
                    while (count < 10) {
                        try {
                            Thread.sleep(INTERVAL_TIME_TO_CHECK_SPEAKER_PHONE_ON);
                        } catch (InterruptedException e) {
                            SLog.e(TAG, String.format("startSpeakerphoneOnThread().run() : InterruptedException(%s)", e.getMessage()));
                            return;
                        }

                        AudioManagerHelper.setSpeakerphoneOn(mContext, true);

                        count++;
                    }
                }
            };
            mSpeakerPhoneThread.start();
        } else {
            SLog.i(TAG, "startSpeakerphoneOnThread() : set OFF");
            mAudioManager.setSpeakerphoneOn(false);
        }
    }

    /**
     */
    private void stopSpeakerphoneOnThread() {
        if (mSpeakerPhoneThread == null) {
            return;
        }

        SLog.i(TAG, "stopSpeakerphoneOnThread()");

        if (mSpeakerPhoneThread.isAlive() && !mSpeakerPhoneThread.isInterrupted()) {
            mSpeakerPhoneThread.interrupt();
            mSpeakerPhoneThread = null;
        }
    }

    /**
     * @param on
     */
    public void setSpeakerphoneOn(boolean on) {
        SLog.i(TAG, String.format("setSpeakerphoneOn(%s)", on));

        stopSpeakerphoneOnThread();

        AudioManagerHelper.setSpeakerphoneOn(mContext, on);
    }

    /**
     * @return
     */
    public CallState getCallState() {
        // SDKTMAP-20
        // LG G4단말에서 화상전화 수신 시 제조사에서 추가한 전화 상태 코드(101, 102)가 내려옴.
        // 나머지 연산자 이용하여 101->1, 102->2로 안드로이드 정상범위의 상태코드로 변환시킴.
        int telephonyCallState = SystemServiceHelper.getTelephonyManager(mContext).getCallState() % 100;
        CallState callState = CallState.values()[telephonyCallState];
        if (callState == null) {
            SLog.e(TAG, String.format("getCallState() : callState is null{telephonyCallState(%d)}.", telephonyCallState));
        }
        return (callState != null) ? CallState.values()[telephonyCallState] : CallState.IDLE;
    }

    /**
     */
    public void setForegroundableActivity(String className) {
        SLog.i(TAG, String.format("setForegroundableActivity() : className(%s)", className));
        mForegroundableActivity = className;
    }

    /**
     */
    public void startForegroundActivityThread() {
        SLog.i(TAG, "startForegroundActivityThread()");

        Intent intent = null;
        if (!TextUtils.isEmpty(mForegroundableActivity)) {
            try {
                Class mainClass = Class.forName(mForegroundableActivity);
                intent = new Intent(mContext, mainClass);
            } catch (ClassNotFoundException e) {
                SLog.e(TAG, String.format("startForegroundActivityThread() : ClassNotFoundException(%s)", e.getMessage()));
            }
        }
        if (intent == null) {
            // 액티비티 지정하지 않았거나 존재하지 않는 클래스이면 기본 액티비티 실행
            PackageManager packageManager = mContext.getPackageManager();
            intent = packageManager.getLaunchIntentForPackage(mContext.getPackageName());
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        final Intent startIntent = intent;
        mForegroundThread = new Thread() {
            /**
             */
            public void run() {
                int count = 0;
                while (count < 10) {
                    try {
                        Thread.sleep(INTERVAL_TIME_TO_CEHCK_FOREGROUND);
                    } catch (InterruptedException e) {
                        SLog.e(TAG, String.format("startForegroundActivityThread().run() : InterruptedException(%s)", e.getMessage()));
                        return;
                    }

                    // 최초 한번은 무조건 실행시킴 (app이 foreground가 아닌데도 foreground로 인식되는 경우 있음)
                    if ((count == 0) || !CallHelper.isForeground(mForegroundableActivity)) {
                        SLog.i(TAG, "startForegroundActivityThread().run() : start activity.");
                        mContext.startActivity(startIntent);
                    }
                    count++;
                }
            }
        };
        mForegroundThread.start();
    }

    /**
     */
    private void stopForegroundActivityThread() {
        if (mForegroundThread == null) {
            return;
        }

        SLog.i(TAG, "stopForegroundActivityThread()");

        if (mForegroundThread.isAlive() && !mForegroundThread.isInterrupted()) {
            mForegroundThread.interrupt();
            mForegroundThread = null;
        }
    }

    /**
     * @return
     */
    private boolean hasWaitingCallList() {
        return !ArrayHelper.isEmpty(mWaitingCallList);
    }

    /**
     */
    private void clearWaitingCallList() {
        if (!ArrayHelper.isEmpty(mWaitingCallList)) {
            SLog.d(TAG, "clearWaitingCallList()");
            mWaitingCallList.clear();
        }
    }

    /**
     * @param tts
     */
    public void notifyIncomingCallReceivedOnAIServiceResult(String tts) {
        SLog.d(TAG, "notifyIncomingCallReceivedOnAIServiceResult()");

        /*pCommandInfoCall commandInfoCall = getCommandInfoForPhoneDomainWithTTS(tts, true);

        getAladdinServiceManager().notifyOnAIServiceResult(commandInfoCall, commandInfoCall.getDomain(),
                ActionCode.RECEIVE_CALL, SubActionCode.COMPLETE);
                */
    }

    /**
     * @param tts
     */
    public void notifyIncomingCallTimeoutOnAIServiceResult(String tts) {
        SLog.d(TAG, "notifyIncomingCallTimeoutOnAIServiceResult()");

        /*pCommandInfoCall commandInfoCall = getCommandInfoForPhoneDomainWithTTS(tts, false);

        getAladdinServiceManager().notifyOnAIServiceResult(commandInfoCall, commandInfoCall.getDomain(),
                ActionCode.RECEIVE_CALL, SubActionCode.COMPLETE);*/
    }

    /**
     * VoiceInput이 없는 상황에서 UI로 AIServiceResult를 보내기 위해
     * 가상의 pCommandInfoCall 객체를 만들어서 세팅하는 메서드
     *
     * @param tts
     * @param listenContinuous
     * @return
     */
    /*private pCommandInfoCall getCommandInfoForPhoneDomainWithTTS(String tts, boolean listenContinuous) {
        pCommandInfoCall commandInfoCall = new pCommandInfoCall(null);

        commandInfoCall.setDomain(Domain.PHONE);

        commandInfoCall.setTitleFromParsedTTS(tts);
        commandInfoCall.setTTS(tts);

        commandInfoCall.enableStartListeningContinuously(listenContinuous);

        return commandInfoCall;
    }*/

    /**
     */
    public void notifyWaitingCallOnAIServiceResult() {
        if (!hasWaitingCallList()) {
            return;
        }

        SLog.d(TAG, "notifyWaitingCallOnAIServiceResult()");

        /*String domain = Domain.PHONE;
        pCommandInfoCall commandInfoCall = new pCommandInfoCall(null);

        commandInfoCall.setDomain(domain);

        String tts = StringHelper.format(mContext.getString(R.string.tts_waiting_call_received),
                Integer.toString(mWaitingCallList.size()));
        commandInfoCall.setTitleFromParsedTTS(tts);
        commandInfoCall.setTTS(tts);

        String nextTTS = StringHelper.getStringFromStringArrayRandomly(mContext, R.array.tts_make_phone_call);
        commandInfoCall.setNextTTS(nextTTS);
        commandInfoCall.setNextTitleFromParsedTTS(nextTTS);

        commandInfoCall.getSearchResultContainer().addSearchResultInfo(mContext, mWaitingCallList, SearchResultInfoType.WAITING_CALL);

        getAladdinServiceManager().notifyOnAIServiceResult(commandInfoCall, domain,
                ActionCode.WAITING_CALL_RECEIVED, SubActionCode.DISPLAY_SELECT);*/
    }

    /**
     * @param phoneNumber
     */
    public void connectCallWithPhoneNumber(String phoneNumber) {
        SLog.d(TAG, String.format("connectCallWithPhoneNumber() : phoneNumber(%s)", phoneNumber));

        CallHelper.connectCallWithPhoneNumber(mContext, phoneNumber);

        mIsOurOutgoingCall = true;

        mHandler.removeCallbacks(mReleaseOurOutgoingCallRunnable);
        mHandler.postDelayed(mReleaseOurOutgoingCallRunnable, DELAY_TO_RELEASE_OUR_OUTGOING_CALL);
    }

    /**
     */
    public void disconnectCall() {
       CallHelper.disconnectCall(mContext);
    }

    /**
     */
    public void acceptCall() {
        CallHelper.acceptCall(mContext);
    }

    /**
     * @param enabled
     */
    public void enableIncomingCall(boolean enabled) {
        mIsIncomingCallEnabled = enabled;
    }

    /**
     * @return
     */
    public boolean isIncomingCallEnabled() {
        return mIsIncomingCallEnabled;
    }

    /**
     * 전화 연결 중 설정된 전화 앱의 InCall 화면 보여주기
     * @param showDialpad
     * @return
     */
    public boolean showInCallScreen(boolean showDialpad) {
        boolean result = false;
        SLog.d(TAG,"showInCallScreen() Build.VERSION.SDK_INT : " + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            //NuguSDKErrorManager nuguSDKErrorManager = getNuguSDKErrorManager();
            //if (nuguSDKErrorManager.hasPermissionAndNotifyOnError(mContext, CommunicationPermissionType.READ_PHONE_STATE)) {
                TelecomManager telecomManager = (TelecomManager) mContext.getSystemService(Context.TELECOM_SERVICE);
                //noinspection MissingPermission
                telecomManager.showInCallScreen(showDialpad);
                result = true;
            //}
        } else {
            try {
                ITelephony telephonyService = CallHelper.getTelephony(mContext);
                result = telephonyService.showCallScreenWithDialpad(showDialpad);
            } catch (ClassNotFoundException e) {
                SLog.e(TAG,e.toString());
            } catch (NoSuchMethodException e) {
                SLog.e(TAG,e.toString());
            } catch (IllegalAccessException e) {
                SLog.e(TAG,e.toString());
            } catch (InvocationTargetException e) {
                SLog.e(TAG,e.toString());
            }
        }

        // showInCallScreen() 호출 했을 경우 foreground 로 올리는 동작을 stop
        if (result) {
            stopForegroundActivityThread();
        }
        return result;
    }

    /**
     */
    public void destroy() {
		unregisterCallStateReceiver();
        bluetoothHelper.unRegisterHeadsetReceiver();
        mContext = null;
    }
}
