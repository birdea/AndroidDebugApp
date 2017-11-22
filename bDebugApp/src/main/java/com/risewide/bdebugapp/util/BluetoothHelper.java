package com.risewide.bdebugapp.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;

public class BluetoothHelper {

    private final static String TAG = "BluetoothHelper";

    private static BluetoothHelper sInstance;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothA2dp mBluetoothA2dp;
    private BluetoothHeadset mBluetoothHeadset;
    private Context mContext;
    private int mCurrentState;

    private String mBluetoothName;
    private String mBluetoothType;
    private ArrayList<BluetoothDevice> mBluetoothHeadsetList;
    private BluetoothHeadsetState mBluetoothHeadsetState;

    private BluetoothHelper(Context context) {
        mContext = context.getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            BluetoothManager bluetoothManager = (BluetoothManager)mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
        } else {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if ( mBluetoothAdapter != null ) {
            try {
                boolean isProfileProxy = mBluetoothAdapter.getProfileProxy(mContext, new BluetoothProfile.ServiceListener() {
                    @Override
                    public void onServiceConnected(int profile, BluetoothProfile bluetoothProfile) {
                        SLog.d(TAG, "onServiceConnected");
                        mBluetoothA2dp = (BluetoothA2dp) bluetoothProfile;
                    }

                    @Override
                    public void onServiceDisconnected(int profile) {
                        SLog.d(TAG, "onServiceDisconnected");
                        mBluetoothA2dp = null;
                    }
                }, BluetoothProfile.A2DP);

                IntentFilter ifilter = new IntentFilter();
                ifilter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
                ifilter.addAction(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED);
                mContext.registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        int state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, -1);
                        int prevState = intent.getIntExtra(BluetoothProfile.EXTRA_PREVIOUS_STATE, -1);
                        SLog.d(TAG, "state = " + state + " prev = " + prevState);
                        mCurrentState = state;
                        if (prevState == BluetoothA2dp.STATE_NOT_PLAYING && state == BluetoothA2dp.STATE_PLAYING) {
                            double elapsedTime = SystemTimeHelper.endElapsedTime(EventManagerHelper.P2_ACTION_BLUETOOTH_NOT_PLAYING_TO_PLAYING);
                            if (elapsedTime > 0) {
                                updateBluetoothInfo();
                                String seconds = String.format("%.1f", elapsedTime / (double) 1000);
                                /*EventManagerHelper.sendEvent(EventManagerHelper.NUGU_SERVICE_EVENT,
                                        EventManagerHelper.P1_CATEGROY_MEASURE_TIME,
                                        EventManagerHelper.P2_ACTION_BLUETOOTH_NOT_PLAYING_TO_PLAYING + "/" + mBluetoothName + "/" + mBluetoothType,
                                        seconds, Long.toString((long) elapsedTime));*/
                            }
                        }
                    }
                }, ifilter);
            } catch (SecurityException e) {
                SLog.e(TAG,""+e.getMessage());
            }

            initBluetoothHeadset();
        }
    }

    private void initBluetoothHeadset() {
        mBluetoothHeadsetList = new ArrayList<BluetoothDevice>();
        mBluetoothHeadsetState = BluetoothHeadsetState.INITIALIZE;
        try {
            mBluetoothAdapter.getProfileProxy(mContext, new BluetoothProfile.ServiceListener() {
                @Override
                public void onServiceConnected(int profile, BluetoothProfile proxy) {
                    SLog.d(TAG, "onServiceConnected profile = "+profile);
                    if(profile == BluetoothProfile.HEADSET) {
                        mBluetoothHeadset = (BluetoothHeadset)proxy;
                    }
                }

                @Override
                public void onServiceDisconnected(int profile) {
                    SLog.d(TAG, "onServiceConnected profile = "+profile);
                    if(profile == BluetoothProfile.HEADSET) {
                        mBluetoothHeadset = null;
                    }
                }
            }, BluetoothProfile.HEADSET);
        } catch (SecurityException e) {
            SLog.e(TAG, "initBluetoothHeadset "+ e.getMessage());
        }
    }

    public synchronized static BluetoothHelper getInstance(Context context) {
        if ( sInstance == null ) {
            sInstance = new BluetoothHelper(context);
        }
        return sInstance;
    }

    public boolean preparePlaying() {
        boolean isPrepared = false;
        int retry = 10;
        if (mBluetoothA2dp != null ) {
            try {
                List<BluetoothDevice> sinks = mBluetoothA2dp.getConnectedDevices();
                if (!sinks.isEmpty()) {

                    if (mCurrentState == BluetoothA2dp.STATE_NOT_PLAYING) {
                        SystemTimeHelper.startElapsedTimeForcely(EventManagerHelper.P2_ACTION_BLUETOOTH_NOT_PLAYING_TO_PLAYING);
                    }

                    if (!mBluetoothA2dp.isA2dpPlaying(sinks.get(0))) {
                        SLog.d(TAG, "not isA2dpPlaying... playing beep");
                        //MediaPlayer player = MediaPlayer.create(mContext, R.raw.asr_ready_56);
                        //player.start();
                    }

                    for (int i = 0; i < retry; i++) {
                        if (mCurrentState != BluetoothA2dp.STATE_PLAYING) {
                            SLog.d(TAG, "not STATE_PLAYING... sleep...");
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            SLog.d(TAG, "current state is playing...");
                            isPrepared = true;
                            break;
                        }
                    }
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        return isPrepared;
    }

    public boolean isConnected() {
        if (mBluetoothA2dp != null ) {
            try {
                List<BluetoothDevice> sinks = mBluetoothA2dp.getConnectedDevices();
                if (!sinks.isEmpty()) {
                    return true;
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean updateBluetoothInfo() {
        if (mBluetoothA2dp != null ) {
            try {
                List<BluetoothDevice> sinks = mBluetoothA2dp.getConnectedDevices();
                if (!sinks.isEmpty()) {
                    BluetoothDevice bd = sinks.get(0);
                    if (bd != null) {
                        mBluetoothName = bd.getName();
                        int type = bd.getType();
                        switch (type) {
                            case BluetoothDevice.DEVICE_TYPE_CLASSIC:
                                mBluetoothType = "DEVICE_TYPE_CLASSIC";
                                break;
                            case BluetoothDevice.DEVICE_TYPE_DUAL:
                                mBluetoothType = "DEVICE_TYPE_DUAL";
                                break;
                            case BluetoothDevice.DEVICE_TYPE_LE:
                                mBluetoothType = "DEVICE_TYPE_LE";
                                break;
                            default:
                                mBluetoothType = "DEVICE_TYPE_UNKNOWN";
                                break;
                        }
                        return true;
                    }
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * BT 연결 확인 - 통화 기능
     * @return boolean
     */
    public boolean isConnectedBluetoothHeadsetProfile() {
        boolean result = false;
        if (mBluetoothAdapter != null
                && mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET) != BluetoothProfile.STATE_DISCONNECTED) {
            result = true;
        }
        return result;
    }

    /**
     * 전화 수신 중 BT 연결 되었을 경우 통화기능 disabled 하는 기능 수행 중인지 여부
     * @return boolean
     */
    public boolean isProgressingConnectedBluetoothHeadset() {
        boolean result = false;
        if (mBluetoothHeadsetList != null
                && mBluetoothHeadsetList.size() > 0
                && !isConnectedBluetoothHeadsetProfile()) {
            result = true;
        }
        return result;
    }

    /**
     * BT 연결 확인 - 오디오 기능
     * @return boolean
     */
    public boolean isConnectedBluetoothA2dpProfile() {
        boolean result = false;
        if (mBluetoothAdapter != null
                && mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP) != BluetoothProfile.STATE_DISCONNECTED) {
            result = true;
        }
        return result;
    }

    /**
     * disconnectHeadsetProfile() - Reflection BluetoothHeadset disconnect()
     * @param bluetoothDevice
     * @return
     */
    private boolean disconnectHeadsetProfile(BluetoothDevice bluetoothDevice) {
        boolean result = false;
        try {
            Class BluetoothHeadsetClass = mContext.getApplicationContext()
                    .getClassLoader().loadClass("android.bluetooth.BluetoothHeadset"); // 패키지명으로 원하는 클래스 로드

            Class[] types = new Class[1]; // 파라미터 타입 지정
            types[0] = BluetoothDevice.class;

            Object[] params = new Object[1]; // 파라미터 값 넣기
            params[0] = bluetoothDevice;

            Method set = BluetoothHeadsetClass.getMethod("disconnect", types); // 메소드 이름으로 불러서 실행
            result = (boolean) set.invoke(mBluetoothHeadset, params); // 파라미터 넘겨서 실행, return 값
            SLog.d(TAG,"disconnectCallProfile result = " + result +" , " + bluetoothDevice.getName()+ " , " +bluetoothDevice.getAddress());
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException |
                IllegalArgumentException | InvocationTargetException | ClassNotFoundException e ) {
            SLog.e(TAG,"disconnectCallProfile Exception = " + e.getMessage());
        }
        return result;
    }

    /**
     * connectHeadsetProfile() - Reflection BluetoothHeadset connect()
     * @param bluetoothDevice
     * @return
     */
    private boolean connectHeadsetProfile(BluetoothDevice bluetoothDevice) {
        boolean result = false;
        try {
            Class BluetoothHeadsetClass = mContext.getApplicationContext()
                    .getClassLoader().loadClass("android.bluetooth.BluetoothHeadset"); // 패키지명으로 원하는 클래스 로드

            Class[] types = new Class[1];
            types[0] = BluetoothDevice.class;

            Object[] params = new Object[1]; // 파라미터 값 넣기
            params[0] = bluetoothDevice;

            Method set = BluetoothHeadsetClass.getMethod("connect", types); // 메소드 이름으로 불러서 실행
            result = (boolean) set.invoke(mBluetoothHeadset, params); // 파라미터 넘겨서 실행, return 값
            SLog.d(TAG,"connectCallProfile result = " + result +" , " + bluetoothDevice.getName()+ " , " +bluetoothDevice.getAddress());
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException |
                IllegalArgumentException | InvocationTargetException | ClassNotFoundException e ) {
            SLog.e(TAG,"connectCallProfile Exception = " + e.getMessage());
        }
        return result;
    }

    /**
     * disconnect 된 BluetoothHeadsetList
     * @return
     */
    public List<BluetoothDevice> getBluetoothHeadsetList() {
        return mBluetoothHeadsetList;
    }

    public void clearBluetoothHeadsetList() {
        mBluetoothHeadsetList.clear();
    }

    private BroadcastReceiver broadcastHeadsetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SLog.d(TAG,"broadcastHeadsetReceiver onReceive intent = "+intent);
            if (intent == null) {
                return;
            }
            String action  = intent.getAction();
            if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, BluetoothHeadset.STATE_DISCONNECTED);
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                SLog.d(TAG,"broadcastHeadsetReceiver ACTION_CONNECTION_STATE_CHANGED "+state +
                        " , "+bluetoothDevice.getName()+" , "+bluetoothDevice.getAddress());
                if (BluetoothHeadset.STATE_CONNECTED == state
                        && mBluetoothHeadsetState.equals(BluetoothHeadsetState.CONNECT)) {
                    connectHeadset(true);
                } else if (BluetoothHeadset.STATE_DISCONNECTED == state
                        && mBluetoothHeadsetState.equals(BluetoothHeadsetState.DISCONNECT)) {
                    disConnectHeadset(true);
                } else if (BluetoothHeadset.STATE_DISCONNECTED == state
                        && mBluetoothHeadsetState.equals(BluetoothHeadsetState.CONNECT)) {
                    connectHeadset(true);
                }
            }
        }
    };

    /**
     * Headset broadcast receiver 등록
     */
    public void registerHeadsetReceiver() {
        SLog.d(TAG,"registerHeadsetReceiver");
        IntentFilter ifilter = new IntentFilter();
        ifilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        mContext.registerReceiver(broadcastHeadsetReceiver, ifilter);
    }

    /**
     * Headset broadcast receiver 해제
     */
    public void unRegisterHeadsetReceiver() {
        mContext.unregisterReceiver(broadcastHeadsetReceiver);
    }

    /**
     * disConnectHeadset()
     * true - all devices
     * false - last devices
     * @param allBluetoothDevice
     */
    public void disConnectHeadset(boolean allBluetoothDevice) {
        SLog.d(TAG,"disconnectHeadset mBluetoothAdapter : " + (mBluetoothAdapter == null ? "null" : "isConnectedBluetoothHeadsetProfile() = "
                + isConnectedBluetoothHeadsetProfile())
                + "allBluetoothDevice = "+allBluetoothDevice);
        if (mBluetoothAdapter != null
                && mBluetoothHeadset != null
                && isConnectedBluetoothHeadsetProfile()) {
            mBluetoothHeadsetState = BluetoothHeadsetState.DISCONNECT;
            if (allBluetoothDevice) {
                List<BluetoothDevice> bluetoothDeviceList = mBluetoothHeadset.getConnectedDevices();
                if (bluetoothDeviceList != null && bluetoothDeviceList.size() > 0) {
                    BluetoothDevice tempBluetoothDevice = bluetoothDeviceList.get(0);
                    disconnectHeadsetProfile(tempBluetoothDevice);
                    mBluetoothHeadsetList.add(tempBluetoothDevice);
                } else {
                    mBluetoothHeadsetState = BluetoothHeadsetState.INITIALIZE;
                }
            } else {
                if(mBluetoothHeadsetList != null && mBluetoothHeadsetList.size() > 0){
                    mBluetoothHeadsetState = BluetoothHeadsetState.CONNECT;
                    BluetoothDevice tempBluetoothDevice = mBluetoothHeadsetList.get(mBluetoothHeadsetList.size()-1);
                    disconnectHeadsetProfile(tempBluetoothDevice);
                } else {
                    mBluetoothHeadsetState = BluetoothHeadsetState.INITIALIZE;
                }
            }
        } else {
            mBluetoothHeadsetState = BluetoothHeadsetState.INITIALIZE;
        }
        SLog.d(TAG,"disconnectHeadset mBluetoothHeadsetState = "+mBluetoothHeadsetState+" , mBluetoothHeadsetList = "+mBluetoothHeadsetList);
    }

    /**
     * connectHeadset
     * true - all devices
     * false - last devices
     * @param allBluetoothDevice
     */
    public void connectHeadset(boolean allBluetoothDevice) {
        SLog.d(TAG, "connectHeadset mBluetoothAdapter : " + (mBluetoothAdapter == null ? "null" : "not null")
                + ", mBluetoothHeadsetList = "+mBluetoothHeadsetList
                + "allBluetoothDevice = " + allBluetoothDevice);
        if (mBluetoothAdapter != null
                && mBluetoothHeadset != null
                && !ArrayHelper.isEmpty(mBluetoothHeadsetList)) {
            mBluetoothHeadsetState = BluetoothHeadsetState.CONNECT;
            if (allBluetoothDevice) {
                if (mBluetoothHeadsetList != null && mBluetoothHeadsetList.size() > 0) {
                    BluetoothDevice tempBluetoothDevice = mBluetoothHeadsetList.get(0);
                    connectHeadsetProfile(tempBluetoothDevice);
                    mBluetoothHeadsetList.remove(0);
                } else {
                    mBluetoothHeadsetState = BluetoothHeadsetState.INITIALIZE;
                }
            } else {
                if (mBluetoothHeadsetList != null && mBluetoothHeadsetList.size() > 0) {
                    mBluetoothHeadsetState = BluetoothHeadsetState.INITIALIZE;
                    BluetoothDevice tempBluetoothDevice = mBluetoothHeadsetList.get(mBluetoothHeadsetList.size()-1);
                    connectHeadsetProfile(tempBluetoothDevice);
                } else {
                    mBluetoothHeadsetState = BluetoothHeadsetState.INITIALIZE;
                }
            }
        } else {
            mBluetoothHeadsetState = BluetoothHeadsetState.INITIALIZE;
            mBluetoothHeadsetList.clear();
        }
        SLog.d(TAG,"connectHeadset mBluetoothHeadsetState = "+mBluetoothHeadsetState+" , mBluetoothHeadsetList = "+mBluetoothHeadsetList);
    }

    /**
     * Bluetooth headset state
     */
    public enum BluetoothHeadsetState {
        INITIALIZE,
        DISCONNECT,
        CONNECT
    }
}
