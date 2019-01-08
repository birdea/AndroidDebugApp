package com.risewide.bdebugapp.util;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author sehwan.kwak, hyunho.mo
 *
 * @since 2017.06.22 created by bridea.
 * @since 2018.08.17 refactoring by hyunho.mo
 */
public class TelephonyHelper {
    private static final String TAG = TelephonyHelper.class.getSimpleName();

    public static final String ACTION_INNER_OUTGOING_CALL = "android.skt.aicloud.mobile.service.action.call";
    public static final String BUNDLE_KEY_INNER_OUTGOING_CALL_PHONE_NUMBER = "bundle_key_inner_outgoing_call_phone_number";


    /**
     * @param context
     * @param phoneNumber
     */
    public static void connectCallWithPhoneNumber(Context context, String phoneNumber) {
        if (!PermissionHelper.hasPermission(context, Manifest.permission.CALL_PHONE)) {
            BLog.w(TAG, "connectCallWithPhoneNumber() : CALL_PHONE permission is not granted.");
            return;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            BLog.w(TAG, String.format("connectCallWithPhoneNumber() : %s phoneNumber is empty.", phoneNumber));
            return;
        }

        BLog.i(TAG, String.format("connectCallWithPhoneNumberconnectCallWithPhoneNumber() : phoneNumber(%s)", phoneNumber));

        Intent innerOutgoingIntent = new Intent(ACTION_INNER_OUTGOING_CALL);
        innerOutgoingIntent.putExtra(BUNDLE_KEY_INNER_OUTGOING_CALL_PHONE_NUMBER, phoneNumber);
        context.sendBroadcast(innerOutgoingIntent);

        String action = Intent.ACTION_CALL;
        if (PhoneNumberUtils.isEmergencyNumber(phoneNumber)) {
            action = Intent.ACTION_DIAL;
        }

        Intent intent = new Intent(action, Uri.parse("tel:" + Uri.encode(phoneNumber)));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * @param context
     */
    public static void acceptCall(Context context) {
        BLog.i(TAG, String.format("acceptCall() : Build.VERSION.SDK_INT(%d)", Build.VERSION.SDK_INT));

        // acceptCall이 연속해서 발생하면 전화가 끊어지기 때문에
        // RINGING 상태에서만 전화 수신 동작을 수행함
        if (getCallState(context) != TelephonyManager.CALL_STATE_RINGING) {
            return;
        }

        try {
            KeyEvent keyDownHeadsetHook = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK);
            KeyEvent keyUpHeadsetHook = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // LOLLIPOP(API 21) 이상
                // 삼성 보안패치로 인해 아래 MediaSessionLegacyHelper는 동작되지 않음(System만 사용 가능)
                // MediaSessionManager는 LOLLIPOP 이상에서부터 지원하며 NotificationListenerService는
                // JELLY_BEAN_MR2 MR2에서 부터 지원하고 있기 때문에 롤리팝이상일 경우에만 적용되도록 함.
                dispatchKeyEvent(context, keyDownHeadsetHook, keyUpHeadsetHook);
            } else {
                Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                intent.putExtra(Intent.EXTRA_KEY_EVENT, keyDownHeadsetHook);
                context.sendOrderedBroadcast(intent, Manifest.permission.CALL_PRIVILEGED);

                intent.putExtra(Intent.EXTRA_KEY_EVENT, keyUpHeadsetHook);
                context.sendOrderedBroadcast(intent, Manifest.permission.CALL_PRIVILEGED);
            }
        } catch (Exception e) {
            BLog.e(TAG, String.format("acceptCall() : Exception(%s)", e.getMessage()));
        } catch (Error e) {
            BLog.e(TAG, String.format("acceptCall() : Error(%s)", e.getMessage()));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void disconnectCallByKeyEvent(Context context) {
        BLog.i(TAG, String.format("disconnectCall() : Build.VERSION.SDK_INT(%d)", Build.VERSION.SDK_INT));

        int callState = getCallState(context);
        switch (callState) {
            case TelephonyManager.CALL_STATE_RINGING:
                int flag = KeyEvent.FLAG_LONG_PRESS;
                KeyEvent keyLongPressHeadsetHook = new KeyEvent(0, 0,
                        KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK, 0, 0, 0, 0, flag, 0);

                dispatchKeyEvent(context, keyLongPressHeadsetHook);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                KeyEvent keyDownHeadsetHook = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK);
                KeyEvent keyUpHeadsetHook = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
                dispatchKeyEvent(context, keyDownHeadsetHook, keyUpHeadsetHook);
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static void dispatchKeyEvent(Context context, KeyEvent... keyEvents) {
        if (PermissionHelper.isNotificationListenersEnabled(context)) {
            MediaSessionManager mediaSessionManager = SystemServiceHelper.getMediaSessionManager(context);
            ComponentName componentName = new ComponentName(context, AISNotificationListenerService.class);

            List<MediaController> mediaControllerList = null;
            try {
                 mediaControllerList = mediaSessionManager.getActiveSessions(componentName);
            } catch (SecurityException e) {
                BLog.e(TAG, String.format("dispatchKeyEvent() : SecurityException(%s)", e.getMessage()));
                return;
            }

            for (MediaController mediaController : mediaControllerList) {
                if (mediaController == null) {
                    continue;
                }
                String packageName = mediaController.getPackageName();
                if ("com.android.server.telecom".equals(packageName) || "com.android.phone".equals(packageName)) {
                    for (int i = 0; i < keyEvents.length; i++) {
                        mediaController.dispatchMediaButtonEvent(keyEvents[i]);
                    }
                    break;
                }
            }
            BLog.d(TAG, "dispatchKeyEvent() : NotificationListener is enabled, but do not matched package name.");
        } else {
            BLog.d(TAG, "dispatchKeyEvent() : NotificationListener is disabled.");
        }
    }

    /**
     * Foregroundable로 설정된 Activity 클래스가 foreground로 실행되고 있는지 여부를 리턴함
     *
     * @param className
     * @return
     */
    public static boolean isForeground(String className) {
        if (TextUtils.isEmpty(className)) {
            BLog.e(TAG, "isForeground() : className is empty.");
            return false;
        }

        boolean isForeground = false;
        /*ClientApplicationStatus.ActivityStatusData activityStatusData = ClientApplicationStatus.getInstance().getActivityStatus(className);
        if (activityStatusData == null) {
            isForeground = true;
        } else if (AppIntentCode.ActivityLifeCycle.LifeCycleValue.CREATE.equals(activityStatusData.lifeCycle)
                || AppIntentCode.ActivityLifeCycle.LifeCycleValue.RESUME.equals(activityStatusData.lifeCycle)) {
            isForeground = true;
        } else if ((System.currentTimeMillis() - activityStatusData.changedTime) <= 1500) {
            isForeground = true;
        }*/

        BLog.i(TAG, String.format("isForegrounded() : className(%s), isForeground(%s)", className, isForeground));
        return isForeground;
    }

    /**
     * @param context
     * @return
     */
    public static int getCallState(Context context) {
        // SDKTMAP-20
        // LG G4단말에서 화상전화 수신 시 제조사에서 추가한 전화 상태 코드(101, 102)가 내려옴.
        // 나머지 연산자 이용하여 101->1, 102->2로 안드로이드 정상범위의 상태코드로 변환시킴.
        return SystemServiceHelper.getTelephonyManager(context).getCallState() % 100;
    }

    public static ITelephony getTelephony(Context context) throws ClassNotFoundException,
            NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        Class c = Class.forName(telephonyManager.getClass().getName());
        Method m = c.getDeclaredMethod("getITelephony");
        m.setAccessible(true);
        ITelephony telephonyService = (ITelephony)m.invoke(telephonyManager);
        return telephonyService;
    }

    /**
     * @param context
     * @return
     */
    public static boolean isSimStateReady(Context context) {
        TelephonyManager telephonyManager = SystemServiceHelper.getTelephonyManager(context);
        int simState = telephonyManager.getSimState();
        BLog.i(TAG, String.format("isSimStateReady() : simState(%s)", toSimStateString(simState)));

        switch (simState) {
            case TelephonyManager.SIM_STATE_UNKNOWN:
                break;
            case TelephonyManager.SIM_STATE_ABSENT:
                break;
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                break;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                break;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                break;
            case TelephonyManager.SIM_STATE_READY:
                return true;
            default:
                break;
        }
        return false;
    }

    /**
     * @param simState
     * @return
     */
    public static String toSimStateString(int simState) {
        switch (simState) {
            case TelephonyManager.SIM_STATE_UNKNOWN:
                return "SIM_STATE_UNKNOWN";
            case TelephonyManager.SIM_STATE_ABSENT:
                return "SIM_STATE_ABSENT";
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                return "SIM_STATE_PIN_REQUIRED";
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                return "SIM_STATE_PUK_REQUIRED";
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                return "SIM_STATE_NETWORK_LOCKED";
            case TelephonyManager.SIM_STATE_READY:
                return "SIM_STATE_READY";
            default:
                break;
        }
        return Integer.toString(simState);
    }
}
