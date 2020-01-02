package com.risewide.bdebugapp.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.risewide.bdebugapp.BaseActivity;
import com.risewide.bdebugapp.R;
import com.risewide.bdebugapp.adapter.HandyListAdapter;
import com.risewide.bdebugapp.util.SLog;

import java.util.List;

public class WifiScanTestActivity extends BaseActivity {

    private static final String TAG = "WifiScanTestActivity";

    private IWifiScan wifiScanner = new WifiAwareScanner();
    private HandyListAdapter mHandyListAdapter;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_scan_test);

        mHandyListAdapter = new HandyListAdapter(this, HandyListAdapter.Mode.BODY_ONLY);
        ListView lvEvents = (ListView) findViewById(R.id.lvEvents);
        tvResult = (TextView) findViewById(R.id.tvResult);
        lvEvents.setAdapter(mHandyListAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        wifiScanner.stop(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnWifiScan: {
                mHandyListAdapter.clear();
                mHandyListAdapter.notifyDataSetChanged();
                scan();
                break;
            }
            case R.id.btnWifiOn: {
                getWifiManager(this).setWifiEnabled(true);
                break;
            }
            case R.id.btnWifiOff: {
                getWifiManager(this).setWifiEnabled(false);
                break;
            }
        }
    }

    public void scan() {
        wifiScanner.run(this, new IWifiScan.onScanResultListener() {
            @Override
            public void onResult(boolean success, final List<ScanResult> results) {
                final String result;
                if (results != null) {
                    result = "size: " +results.size();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int index = 1;
                            for (ScanResult sr : results) {
                                addEventMessage((index++)+"] "+sr.toString());
                            }
                        }
                    });
                } else {
                    result = "result is null";
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvResult.setText(result);
                    }
                });
            }
        });
    }

    private void addEventMessage(String event) {
        SLog.i(TAG, event);
        if(mHandyListAdapter == null) {
            return;
        }
        mHandyListAdapter.addAndnotifyDataSetChanged(null, event);
    }

    WifiManager wifiManager;

    public WifiManager getWifiManager(Context context) {
        if (wifiManager == null) {
            wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        }
        return wifiManager;
    }
}
