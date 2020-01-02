package com.risewide.bdebugapp.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import com.risewide.bdebugapp.util.BLog;

import java.util.List;

public class WifiBasicScanner implements IWifiScan{

    private static final String TAG = "WifiBasicScanner";

    WifiManager wifiManager;

    @Override
    public void run(Context context, final onScanResultListener listener) {
        boolean locEnable = isLocationEnable(context);
        BLog.i(TAG, "run() isLocationEnable?"+locEnable);

        if (!locEnable) {
            context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            return;
        }

        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                BLog.i(TAG, "onReceive success:"+success);
                if (success) {
                    scanSuccess(listener);
                } else {
                    // scan failure handling
                    scanFailure(listener);
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        context.registerReceiver(wifiScanReceiver, intentFilter);

        boolean success = wifiManager.startScan();
        BLog.i(TAG, "run() startScan success:"+success);
        if (!success) {
            // scan failure handling
            scanFailure(listener);
        }
    }

    @Override
    public void stop(Context context) {

    }

    private boolean isLocationEnable(Context context) {
        BLog.i(TAG, "isLocationEnable()");
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}
        BLog.i(TAG, "isLocationEnable() gps_enabled:"+gps_enabled);
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}
        BLog.i(TAG, "isLocationEnable() network_enabled:"+gps_enabled);

        boolean enable = gps_enabled || network_enabled;
        BLog.i(TAG, "isLocationEnable() enable?" +enable);

        return enable;
    }

    private void scanSuccess(onScanResultListener listener) {
        BLog.i(TAG, "scanSuccess");
        List<ScanResult> results = wifiManager.getScanResults();
        listener.onResult(true, results);
        if (results == null) {
            BLog.i(TAG, "scanSuccess > result == null");
            return;
        }
        for(ScanResult sr : results) {
            BLog.i(TAG, sr);
        }
    }

    private void scanFailure(onScanResultListener listener) {
        BLog.i(TAG, "scanFailure");
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        List<ScanResult> results = wifiManager.getScanResults();
        listener.onResult(false, results);
        if (results == null) {
            BLog.i(TAG, "scanFailure > result == null");
            return;
        }
        for(ScanResult sr : results) {
            BLog.i(TAG, sr);
        }
    }
}
