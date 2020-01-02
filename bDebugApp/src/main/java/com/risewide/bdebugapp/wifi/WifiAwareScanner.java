package com.risewide.bdebugapp.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.aware.AttachCallback;
import android.net.wifi.aware.WifiAwareManager;
import android.net.wifi.aware.WifiAwareSession;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;

import com.risewide.bdebugapp.util.BLog;

public class WifiAwareScanner implements IWifiScan{

    private static final String TAG = "WifiAwareScanner";

    BroadcastReceiver myReceiver;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run(Context context, onScanResultListener listener) {
        boolean hasSystemFeature = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_AWARE);

        BLog.d(TAG, "run() hasSystemFeature?" +hasSystemFeature);

        final WifiAwareManager wifiAwareManager = (WifiAwareManager)context.getSystemService(Context.WIFI_AWARE_SERVICE);
        IntentFilter filter = new IntentFilter(WifiAwareManager.ACTION_WIFI_AWARE_STATE_CHANGED);
        myReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // discard current sessions
                if (wifiAwareManager.isAvailable()) {
                    BLog.d(TAG, "onReceive() hasSystemFeature? true");
                } else {
                    BLog.d(TAG, "onReceive() hasSystemFeature? false");
                }
            }
        };
        try {
            context.registerReceiver(myReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }


        wifiAwareManager.attach(new MyAttachCallback(), new Handler());
    }

    @Override
    public void stop(Context context) {
        try {
            context.unregisterReceiver(myReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public class MyAttachCallback extends AttachCallback {

        @Override
        public void onAttached(WifiAwareSession session) {
            super.onAttached(session);
            BLog.d(TAG, "onAttached() WifiAwareSession? "+session);
        }

        @Override
        public void onAttachFailed() {
            super.onAttachFailed();
            BLog.d(TAG, "onAttachFailed()");
        }
    }
}
