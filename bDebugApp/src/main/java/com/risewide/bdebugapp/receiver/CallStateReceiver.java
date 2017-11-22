package com.risewide.bdebugapp.receiver;

import com.risewide.bdebugapp.util.DateTimeHelper;
import com.risewide.bdebugapp.util.PhoneNumberHelper;
import com.risewide.bdebugapp.util.SLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * @author birdea, hyunho.mo
 *
 * @since 2017.07.11 created by bridea.
 * @since 2018.08.17 refactoring by hyunho.mo
 */
public abstract class CallStateReceiver extends BroadcastReceiver {
    private final static String TAG = CallStateReceiver.class.getSimpleName();

    // The receiver will be recreated whenever android feels like it.
    // We need a static variable to remember data between instantiations because the passed incoming
    // is only valid in ringing.
    private static CallSubState sLastCallSubState = CallSubState.IDLE;
    private static String sSavedPhoneNumber = null;
    private static long sCallStartTime = -1;


    /**
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // The new outgoing call state only tells us of an outgoing call state..
        // We use it to get the phone number.

        String outgoingPhoneNumber;
        String action = intent.getAction();
        switch (action) {
            case CallHelper.ACTION_INNER_OUTGOING_CALL:
                outgoingPhoneNumber = intent.getStringExtra(CallHelper.BUNDLE_KEY_INNER_OUTGOING_CALL_PHONE_NUMBER);

                SLog.d(TAG, String.format("onReceive() : action(CallHelper.ACTION_INNER_OUTGOING_CALL), intent(%s), sSavedPhoneNumber(%s), outgoingPhoneNumber(%s)",
                        intent, sSavedPhoneNumber, outgoingPhoneNumber));

                savePhoneNumber(outgoingPhoneNumber, false);
                break;

            case Intent.ACTION_NEW_OUTGOING_CALL:
                outgoingPhoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

                SLog.d(TAG, String.format("onReceive() : action(Intent.ACTION_NEW_OUTGOING_CALL), intent(%s), sSavedPhoneNumber(%s), outgoingPhoneNumber(%s)",
                        intent, sSavedPhoneNumber, outgoingPhoneNumber));

                savePhoneNumber(outgoingPhoneNumber, false);
                break;

            default:
                Bundle extras = intent.getExtras();
                String stateValue = extras.getString(TelephonyManager.EXTRA_STATE);
                String incomingNumber = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

                CallState callState = CallState.IDLE;
                if (TelephonyManager.EXTRA_STATE_IDLE.equals(stateValue)) {
                    callState = CallState.IDLE;
                } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(stateValue)) {
                    callState = CallState.OFFHOOK;
                } else if (TelephonyManager.EXTRA_STATE_RINGING.equals(stateValue)) {
                    callState = CallState.RINGING;
                }

                SLog.d(TAG, String.format("onReceive() : action(DEFAULT), intent(%s), incomingNumber(%s), callState(%s), sLastCallSubState(%s)",
                        intent, incomingNumber, callState, sLastCallSubState));

                notifyCallStateChanged(callState, incomingNumber);
                break;
        }
    }

    /**
     * Deals with actual events
     *
     * @param phoneNumber
     */
    private void savePhoneNumber(String phoneNumber, boolean force) {
        if (!force && TextUtils.isEmpty(phoneNumber)) {
            return;
        }
        sSavedPhoneNumber = PhoneNumberHelper.convertFormattedPhoneNumber(phoneNumber);
    }

    /**
     * Incoming CallSubState goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up.
     * Outgoing CallSubState goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up.
     *
     * @param callState
     * @param incomingPhoneNumber
     */
    public void notifyCallStateChanged(CallState callState, String incomingPhoneNumber) {
        long startTime = -1;
        long endTime = -1;
        CallSubState callSubState = CallSubState.IDLE;

        switch (callState) {
            case RINGING:
                // 전화가 연결되어 있는 상태에서 전화수신 발생시에 CallSubState.WAITING_CALL_RECEIVED 상태를 전달한다.
                if ((sLastCallSubState == CallSubState.INCOMING_CALL_ANSWERED)
                        || (sLastCallSubState == CallSubState.OUTGOING_CALL_STARTED)) {
                    onCallStateChanged(callState, CallSubState.WAITING_CALL_RECEIVED,
                            incomingPhoneNumber, DateTimeHelper.getCurrentTime(), -1);
                    return;
                }

                callSubState = CallSubState.INCOMING_CALL_RECEIVED;

                savePhoneNumber(incomingPhoneNumber, true);

                sCallStartTime = DateTimeHelper.getCurrentTime();

                startTime = sCallStartTime;
                endTime = -1;
                break;

            case OFFHOOK:
                // 중복해서 전화 발신 및 수신연결 상태가 발생하는 경우 처리하지 않음.
                if (CallSubState.OUTGOING_CALL_STARTED.equals(sLastCallSubState)
                        || CallSubState.INCOMING_CALL_ANSWERED.equals(sLastCallSubState)) {
                    return;
                }

                if (CallSubState.IDLE.equals(sLastCallSubState)) { // 전화발신.
                    callSubState = CallSubState.OUTGOING_CALL_STARTED;
                } else if (CallSubState.INCOMING_CALL_RECEIVED.equals(sLastCallSubState)) { // 전화수신.
                    callSubState = CallSubState.INCOMING_CALL_ANSWERED;
                }

                savePhoneNumber(incomingPhoneNumber, false);

                sCallStartTime = DateTimeHelper.getCurrentTime();

                startTime = sCallStartTime;
                endTime = -1;
                break;

            case IDLE:
                // Went to IDLE according to a CallSubState what type depends on previous CallSubState.
                if (CallSubState.INCOMING_CALL_RECEIVED.equals(sLastCallSubState)) {
                    callSubState = CallSubState.MISSED_CALL;
                } else if (CallSubState.INCOMING_CALL_ANSWERED.equals(sLastCallSubState)) {
                    callSubState = CallSubState.INCOMING_CALL_ENDED;
                } else if (CallSubState.OUTGOING_CALL_STARTED.equals(sLastCallSubState)) {
                    callSubState = CallSubState.OUTGOING_CALL_ENDED;
                }

                startTime = sCallStartTime;
                endTime = DateTimeHelper.getCurrentTime();
                break;

            default:
                break;
        }

        SLog.d(TAG, String.format("notifyCallStateChanged() : callState(%s), CallSubState(%s), sSavedPhoneNumber(%s), time(%s, %s)",
                callState, callSubState, sSavedPhoneNumber,
                DateTimeHelper.format(DateTimeHelper.DATE_TIME_FORMAT_PATTERN_1, startTime),
                DateTimeHelper.format(DateTimeHelper.DATE_TIME_FORMAT_PATTERN_1, endTime)));

        onCallStateChanged(callState, callSubState, sSavedPhoneNumber, startTime, endTime);

        if (CallState.IDLE.equals(callState)) {
            callSubState = CallSubState.IDLE;
        }

        sLastCallSubState = callSubState;
    }

    /**
     * Derived classes should override these to respond to specific events of interest.
     *
     * @param callState
     * @param callSubState
     * @param phoneNumber
     * @param startTime
     * @param endTime
     */
    protected abstract void onCallStateChanged(CallState callState, CallSubState callSubState, String phoneNumber, long startTime, long endTime);
}
