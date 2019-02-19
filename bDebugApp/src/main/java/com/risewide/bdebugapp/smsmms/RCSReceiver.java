package com.risewide.bdebugapp.smsmms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.risewide.bdebugapp.util.BLog;

/**
 * @author seungtae.hwang (birdea@sk.com)
 * @since 19/02/2019
 */
public class RCSReceiver extends BroadcastReceiver {
    private static final String TAG = "RCSReceiver";

    private static final String RECEIVE_CHAT_INVITATION = "com.samsung.rcs.framework.instantmessaging.action.RECEIVE_CHAT_INVITATION";
    private static final String RECEIVE_PARTICIPANT_UPDATED = "com.samsung.rcs.framework.instantmessaging.action.RECEIVE_PARTICIPANT_UPDATED";
    private static final String RECEIVE_PARTICIPANT_INSERTED = "com.samsung.rcs.framework.instantmessaging.action.RECEIVE_PARTICIPANT_INSERTED";
    //private Logger log = LoggerFactory.getLogger(MainActivity.class);

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        BLog.d(TAG, "RCS Receiver action:"+action);

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            if (RECEIVE_PARTICIPANT_UPDATED.equals(action) || RECEIVE_PARTICIPANT_INSERTED.equals(action)) {
                String participant = bundle.getString("participant");
                if (participant != null) {
                    String number = participant.substring(4); // get the string after "tel:"
                    BLog.d(TAG, "Chat number is: " + number);
                }
            } else if (RECEIVE_CHAT_INVITATION.equals(action)) {
                String subject = bundle.getString("subject");
                if (subject != null) {
                    BLog.d(TAG, "Chat subject: " + subject);
                }
            }
        }
    }
}