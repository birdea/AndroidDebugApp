package com.risewide.bdebugapp.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.List;

public interface IWifiScan {

    interface onScanResultListener {
        void onResult(boolean success, List<ScanResult> results);
    }

    void run(Context context, final onScanResultListener listener);

    void stop(Context context);
}
