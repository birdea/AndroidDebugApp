package com.risewide.bdebugapp.smsmms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.risewide.bdebugapp.communication.util.TToast;
import com.risewide.bdebugapp.smsmms.pdu.mms.pdu.GenericPdu;
import com.risewide.bdebugapp.smsmms.pdu.mms.pdu.NotificationInd;
import com.risewide.bdebugapp.smsmms.pdu.mms.pdu.PduParser;
import com.risewide.bdebugapp.util.BLog;

/**
 * @author seungtae.hwang (birdea@sk.com)
 * @since 2019. 2. 15.
 */
public class MmsReceiver extends BroadcastReceiver {

    private final String TAG = getClass().getSimpleName();
    private static final String ACTION_MMS_RECEIVED = "android.provider.Telephony.WAP_PUSH_RECEIVED";
    private static final String MMS_DATA_TYPE = "application/vnd.wap.mms-message";

    // Retrieve MMS
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        String type = intent.getType();


        String log = String.format("onReceive action:%s, type:%s", action, type);

        TToast.show(context, "[MmsReceiver] "+log);

        BLog.i(TAG, log);

        if (action.equals(ACTION_MMS_RECEIVED) && type.equals(MMS_DATA_TYPE)) {

            Bundle bundle = intent.getExtras();

            Log.d(TAG, "bundle " + bundle);
            SmsMessage[] msgs = null;
            String str = "";
            int contactId = -1;
            String address;

            if (bundle != null) {

                byte[] buffer = bundle.getByteArray("data");

                GenericPdu pdu = new PduParser(buffer).parse();

                NotificationInd nInd = (NotificationInd) pdu;

                byte [] contentLocation = nInd.getContentLocation();
                if ('=' == contentLocation[contentLocation.length - 1]) {
                    byte [] transactionId = nInd.getTransactionId();
                    byte [] contentLocationWithId = new byte [contentLocation.length
                            + transactionId.length];
                    System.arraycopy(contentLocation, 0, contentLocationWithId,
                            0, contentLocation.length);
                    System.arraycopy(transactionId, 0, contentLocationWithId,
                            contentLocation.length, transactionId.length);
                    nInd.setContentLocation(contentLocationWithId);
                }

                String loc = new String(nInd.getContentLocation());

                String incomingNumber = new String(buffer);
                Log.d(TAG, "buffer " + buffer +", incoming:"+incomingNumber);
                int indx = incomingNumber.indexOf("/TYPE");
                if (indx > 0 && (indx - 15) > 0) {
                    int newIndx = indx - 15;
                    incomingNumber = incomingNumber.substring(newIndx, indx);
                    indx = incomingNumber.indexOf("+");
                    if (indx > 0) {
                        incomingNumber = incomingNumber.substring(indx);
                        Log.d(TAG, "Mobile Number: " + incomingNumber);
                    }
                }

                int transactionId = bundle.getInt("transactionId");
                Log.d(TAG, "transactionId " + transactionId);

                int pduType = bundle.getInt("pduType");
                Log.d(TAG, "pduType " + pduType);

                byte[] buffer2 = bundle.getByteArray("header");
                String header = new String(buffer2);
                Log.d(TAG, "header " + header);

                if (contactId != -1) {
                    showNotification(contactId, str);
                }

                // ---send a broadcast intent to update the MMS received in the
                // activity---
                //Intent broadcastIntent = new Intent();
                //broadcastIntent.setAction("MMS_RECEIVED_ACTION");
                //broadcastIntent.putExtra("mms", str);
                //context.sendBroadcast(broadcastIntent);
            } else {
                BLog.d(TAG,"bundle is null");
            }
        }

    }

    /**
     * The notification is the icon and associated expanded entry in the status
     * bar.
     */
    protected void showNotification(int contactId, String message) {
        //Display notification...
    }
}