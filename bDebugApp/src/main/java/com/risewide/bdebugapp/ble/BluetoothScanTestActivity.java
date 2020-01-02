package com.risewide.bdebugapp.ble;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.risewide.bdebugapp.BaseActivity;
import com.risewide.bdebugapp.R;
import com.risewide.bdebugapp.adapter.HandyListAdapter;
import com.risewide.bdebugapp.util.BLog;

public class BluetoothScanTestActivity extends BaseActivity {

    private static final String TAG = "BluetoothScanTestActivity";

    private HandyListAdapter mHandyListAdapter;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_scan_test);

        mHandyListAdapter = new HandyListAdapter(this, HandyListAdapter.Mode.BODY_ONLY);
        ListView lvEvents = (ListView) findViewById(R.id.lvEvents);
        tvResult = (TextView) findViewById(R.id.tvResult);
        lvEvents.setAdapter(mHandyListAdapter);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBluetoothScan:
                break;
            case R.id.btnBluetoothOn: {
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (!mBluetoothAdapter.isEnabled()) {
                    mBluetoothAdapter.enable();
                }
                break;
            }
            case R.id.btnBluetoothOff:{
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter.isEnabled()) {
                    mBluetoothAdapter.disable();
                }
                break;
            }
        }
    }

    private void addEventMessage(String event) {
        BLog.i(TAG, event);
        if(mHandyListAdapter == null) {
            return;
        }
        mHandyListAdapter.addAndnotifyDataSetChanged(null, event);
    }

}
