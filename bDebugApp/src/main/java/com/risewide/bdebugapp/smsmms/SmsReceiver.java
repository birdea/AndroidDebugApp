package com.risewide.bdebugapp.smsmms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import com.risewide.bdebugapp.communication.util.TToast;
import com.risewide.bdebugapp.util.BLog;

import java.util.ArrayList;

/**
 * @author seungtae.hwang (birdea@sk.com)
 * @since 2019. 2. 15.
 */
public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";

    @Override
    public void onReceive(Context $context, Intent $intent) {
        Bundle bundle = $intent.getExtras();

        TToast.show($context, "[SmsReceiver] onReceive()");

        BLog.i(TAG, "onReceive() bundle:"+bundle);
        if (bundle == null)
            return;

        Object[] pdus = (Object[]) bundle.get("pdus");
        if (pdus == null)
            return;

        ArrayList<String> msgs = new ArrayList<String>();
        String number = "";
        for (int i = 0; i < pdus.length; i++) {
            SmsMessage smsMsg = SmsMessage.createFromPdu((byte[]) pdus[i]);
            number = smsMsg.getDisplayOriginatingAddress();

            msgs.add(smsMsg.getDisplayMessageBody());
        }

        if (!TextUtils.isEmpty(number) && number.contains(";"))
            number = number.split(";")[0];

        if (!TextUtils.isEmpty(number))
            number = number.trim().replaceAll("[^0-9]", "");

        String msg = TextUtils.join(" ", msgs);
        BLog.i(TAG, "|" + number + "|" + msg);
    }
}