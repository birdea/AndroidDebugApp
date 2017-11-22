package com.risewide.bdebugapp.util;

import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;

public class EventManagerHelper {

    private final static String TAG = "EventManagerHelper";

    public static final String NUGU_SERVICE_EVENT = "NuguService";

    public final static String CONNECTION_TYPE = "Connection_Type";
    public final static String CONNECTION_TYPE_UNKNOWN = "Unknown";
    public final static String CONNECTION_TYPE_WIFI = "WiFi";
    public final static String CONNECTION_TYPE_2G = "2G";
    public final static String CONNECTION_TYPE_3G = "3G";
    public final static String CONNECTION_TYPE_4G = "4G";

    public final static String SIGNAL_LEVEL = "Signal_Level";
    public final static String SIGNAL_DBM = "Signal_DBM";

    public static final String P1_CATEGROY_ENTRYPOINT = "진입경로";
    public final static String P2_ACTION_WAKEUP = "Wakeup";
    public final static String P2_ACTION_BUTTON = "Button";

    public static final String P1_CATEGROY_MEASURE_TIME = "시간측정";
    public final static String P2_ACTION_BLUETOOTH_NOT_PLAYING_TO_PLAYING= "BLUETOOTH_NOT_PLAYING_TO_PLAYING";

    public static final String P1_CATEGROY_MELON = "Melon";
    public final static String P2_ACTION_PLAY_DOWNLOAD = "PlayDownload";
    public final static String P2_ACTION_PLAY_CACHE = "PlayCache";
    public final static String P2_ACTION_PLAY_BUFFERING = "PlayBuffering";

    public static final String P1_CATEGORY_CONTACT_UPLOAD = "ContactUpload";
    public final static String P2_ACTION_CONTACT_COUNT = "ContactCount";
    public final static String P2_ACTION_CONTACT_LOAD_TIME = "ContactLoadTime";
    public final static String P2_ACTION_CONTACT_UPLOAD_TIME = "ContactUploadTime";

    private static TelephonyManager sTelephonyManager;
    private static WifiManager sWifiManager;
    private static ConnectivityManager sConnectivityManager;

   // private static GoogleAnalytics sGoogleAnalytics;
    //private static Tracker sTracker;
    private static HandlerThread sHandlerThread;
    private static Handler sHandler;

    public synchronized static void init(final Context context) {
        /*if ( sHandler == null ) {
            final Context applicationContext = context.getApplicationContext();
            sTelephonyManager = (TelephonyManager) applicationContext.getSystemService(Context.TELEPHONY_SERVICE);
            sWifiManager = (WifiManager) applicationContext.getSystemService(Context.WIFI_SERVICE);
            sConnectivityManager = (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            sHandlerThread = new HandlerThread("TagManager-HandlerThread");
            sHandlerThread.start();
            sHandler = new Handler(sHandlerThread.getLooper());
            sHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (sGoogleAnalytics == null) {
                        sGoogleAnalytics = GoogleAnalytics.getInstance(applicationContext);
                        if ( sGoogleAnalytics != null ) {
                            sTracker = sGoogleAnalytics.newTracker(R.xml.ga_tracker);
                        }
                    }
                }
            },0);
        }*/
    }

    public synchronized static void makeNetworkStatus(Bundle bundle) {
        /*bundle.putString(EventManagerHelper.CONNECTION_TYPE,EventManagerHelper.CONNECTION_TYPE_UNKNOWN);
        bundle.putInt(EventManagerHelper.SIGNAL_LEVEL,-1);

        NetworkInfo mNetInfo = sConnectivityManager.getActiveNetworkInfo();
        if (mNetInfo != null && mNetInfo.isAvailable()) {
            NetworkInfo.State tmpState;
            tmpState = mNetInfo.getState();

            if (tmpState == NetworkInfo.State.CONNECTED
                    || tmpState == NetworkInfo.State.CONNECTING) {
                int netType;
                netType = mNetInfo.getType();

                switch (netType) {
                    case ConnectivityManager.TYPE_WIFI:
                        WifiInfo wifiInfo = sWifiManager.getConnectionInfo();
                        if ( wifiInfo != null ) {
                            int dbm = wifiInfo.getRssi();
                            int level = WifiManager.calculateSignalLevel(dbm,5);
                            bundle.putString(EventManagerHelper.CONNECTION_TYPE,EventManagerHelper.CONNECTION_TYPE_WIFI);
                            bundle.putInt(EventManagerHelper.SIGNAL_LEVEL,level);
                            bundle.putInt(EventManagerHelper.SIGNAL_DBM,dbm);
                        } else {
                            SLog.d(TAG,"wifiInfo is null");
                        }
                        break;
                    case ConnectivityManager.TYPE_MOBILE:
                        int telephonyType = sTelephonyManager.getNetworkType();
                        String telephonyTypeStr;
                        int telephonySignalStrength = -1;
                        int telephonySignalDBM = -1;

                        switch (telephonyType) {
                            case TelephonyManager.NETWORK_TYPE_GPRS:
                            case TelephonyManager.NETWORK_TYPE_EDGE:
                            case TelephonyManager.NETWORK_TYPE_CDMA:
                            case TelephonyManager.NETWORK_TYPE_1xRTT:
                            case TelephonyManager.NETWORK_TYPE_IDEN:
                                telephonyTypeStr = EventManagerHelper.CONNECTION_TYPE_2G;
                                break;
                            case TelephonyManager.NETWORK_TYPE_UMTS:
                            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                            case TelephonyManager.NETWORK_TYPE_HSDPA:
                            case TelephonyManager.NETWORK_TYPE_HSUPA:
                            case TelephonyManager.NETWORK_TYPE_HSPA:
                            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                            case TelephonyManager.NETWORK_TYPE_EHRPD:
                            case TelephonyManager.NETWORK_TYPE_HSPAP:
                                telephonyTypeStr = EventManagerHelper.CONNECTION_TYPE_3G;
                                break;
                            case TelephonyManager.NETWORK_TYPE_LTE:
                                telephonyTypeStr = EventManagerHelper.CONNECTION_TYPE_4G;
                                break;
                            default:
                                telephonyTypeStr = EventManagerHelper.CONNECTION_TYPE_UNKNOWN;
                                break;
                        }

                        List<CellInfo> cellInfos = sTelephonyManager.getAllCellInfo();
                        if ( cellInfos != null && cellInfos.size() > 0 ) {
                            CellInfo cellInfo = cellInfos.get(0);
                            if ( cellInfo instanceof CellInfoGsm ) {
                                CellSignalStrengthGsm gsm = ((CellInfoGsm) cellInfo).getCellSignalStrength();
                                telephonySignalDBM = gsm.getDbm();
                                telephonySignalStrength = gsm.getLevel();
                            } else if ( cellInfo instanceof CellInfoCdma) {
                                CellSignalStrengthCdma cdma = ((CellInfoCdma) cellInfo).getCellSignalStrength();
                                telephonySignalDBM = cdma.getDbm();
                                telephonySignalStrength = cdma.getLevel();
                            } else if ( cellInfo instanceof CellInfoLte) {
                                CellSignalStrengthLte lte = ((CellInfoLte) cellInfo).getCellSignalStrength();
                                telephonySignalDBM = lte.getDbm();
                                telephonySignalStrength = lte.getLevel();
                            } else if  ( cellInfo instanceof CellInfoWcdma) {
                                CellSignalStrengthWcdma wcdma = ((CellInfoWcdma) cellInfo).getCellSignalStrength();
                                telephonySignalDBM = wcdma.getDbm();
                                telephonySignalStrength = wcdma.getLevel();
                            }
                        }
                        bundle.putString(EventManagerHelper.CONNECTION_TYPE,telephonyTypeStr);
                        bundle.putInt(EventManagerHelper.SIGNAL_LEVEL,telephonySignalStrength);
                        bundle.putInt(EventManagerHelper.SIGNAL_DBM,telephonySignalDBM);
                        break;
                    default:
                        SLog.d(TAG, "Network not available");
                        break;
                }
            }
        } else {
            SLog.d(TAG, "Network not available");
        }
    }

    public synchronized static void sendEvent(final String event,final String... params) {
        if ( sHandler != null ) {
            sHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (sGoogleAnalytics != null) {
                        try {
                            HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder();
                            if (params != null) {
                                String msg = "";
                                for (int i = 0; i < params.length; i++) {
                                    if (params[i] != null) {
                                        switch (i) {
                                            case 0:
                                                eventBuilder = eventBuilder.setCategory(params[i]);
                                                break;
                                            case 1:
                                                eventBuilder = eventBuilder.setAction(params[i]);
                                                break;
                                            case 2:
                                                eventBuilder = eventBuilder.setLabel(params[i]);
                                                break;
                                            case 3:
                                                eventBuilder = eventBuilder.setValue(Integer.parseInt(params[i]));
                                                break;
                                            default:
                                                break;
                                        }
                                        msg += " " + params[i];
                                    }
                                }
                                if (params.length > 0 && P1_CATEGROY_MEASURE_TIME.equals(params[0])) {
                                    Bundle bundle = new Bundle();
                                    makeNetworkStatus(bundle);
                                    String connectType = bundle.getString(EventManagerHelper.CONNECTION_TYPE);
                                    String signalLabel = Integer.toString(bundle.getInt(EventManagerHelper.SIGNAL_LEVEL,-1));
                                    eventBuilder.setCustomDimension(1,connectType);
                                    eventBuilder.setCustomDimension(2,signalLabel);
                                    msg += " " + bundle.getString(EventManagerHelper.CONNECTION_TYPE) +
                                            " " + bundle.getInt(EventManagerHelper.SIGNAL_LEVEL,-1);
                                }
                                SLog.d(TAG, "send ... " + event + msg);

                                if ( sTracker != null ) {
                                    sTracker.send(eventBuilder.build());
                                }
                            }
                        } catch (Exception e) {
                            SLog.e(TAG,e.toString());
                        }
                    }
                }
            },100);
        }*/
    }
}