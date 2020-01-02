package com.risewide.bdebugapp.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.net.wifi.rtt.RangingRequest;
import android.net.wifi.rtt.RangingResult;
import android.net.wifi.rtt.WifiRttManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.risewide.bdebugapp.util.BLog;

public class WifiRttScanner implements IWifiScan{

    private static final String TAG = "WifiRttScanner";

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void run(Context context, onScanResultListener listener) {
        boolean hasSystemFeature = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_RTT);
        if (!hasSystemFeature) {
            BLog.d(TAG, "run() hasSystemFeature? false");
            return;
        }
        final WifiRttManager wifiRttManager = (WifiRttManager) context.getSystemService(Context.WIFI_RTT_RANGING_SERVICE);

        BLog.d(TAG, "run() hasSystemFeature?" +hasSystemFeature +", isAvailable?"+wifiRttManager.isAvailable());

        IntentFilter filter = new IntentFilter(WifiRttManager.ACTION_WIFI_RTT_STATE_CHANGED);
        BroadcastReceiver myReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (wifiRttManager.isAvailable()) {
                    BLog.d(TAG, "onReceive() isAvailable?"+wifiRttManager.isAvailable());
                } else {
                    BLog.d(TAG, "onReceive isAvailable?"+wifiRttManager.isAvailable());
                }
            }
        };
        context.registerReceiver(myReceiver, filter);

        RangingRequest.Builder builder = new RangingRequest.Builder();
        //builder.addAccessPoint(ap1ScanResult);
        //builder.addAccessPoint(ap2ScanResult);
        //wifiRttManager.startRanging(RangingRequest.);
        //RangingResult
    }

    @Override
    public void stop(Context context) {

    }
}
