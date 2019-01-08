package com.risewide.bdebugapp.receiver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import com.android.internal.telephony.ITelephony;
import com.risewide.bdebugapp.util.AISNotificationListenerService;
import com.risewide.bdebugapp.util.AppIntentCode;
import com.risewide.bdebugapp.util.ClientApplicationStatus;
import com.risewide.bdebugapp.util.PermissionHelper;
import com.risewide.bdebugapp.util.SLog;
import com.risewide.bdebugapp.util.SystemServiceHelper;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

/**
 * @author sehwan.kwak, hyunho.mo
 *
 * @since 2017.06.22 created by bridea.
 * @since 2018.08.17 refactoring by hyunho.mo
 */
public class CallHelper {
    private static final String TAG = CallHelper.class.getSimpleName();

    public static final String ACTION_INNER_OUTGOING_CALL = "android.skt.aicloud.mobile.service.action.call";
    static final String BUNDLE_KEY_INNER_OUTGOING_CALL_PHONE_NUMBER = "bundle_key_inner_outgoing_call_phone_number";


    /**
     * @param context
     * @param phoneNumber
     */
    public static void connectCallWithPhoneNumber(Context context, String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            SLog.w(TAG, "connectCallWithPhoneNumber() : phoneNumber is empty.");
            return;
        }
        if (!PermissionHelper.hasPermission(context, Manifest.permission.CALL_PHONE)) {
            SLog.w(TAG, "connectCallWithPhoneNumber() : CALL_PHONE permission is not granted.");
            return;
        }
		SLog.i(TAG, String.format("connectCallWithPhoneNumaber() : phoneNumber(%s)", phoneNumber));

        Intent innerOutgoingIntent = new Intent(ACTION_INNER_OUTGOING_CALL);
        innerOutgoingIntent.putExtra(BUNDLE_KEY_INNER_OUTGOING_CALL_PHONE_NUMBER, phoneNumber);
        context.sendBroadcast(innerOutgoingIntent);

        String action;
        if (PhoneNumberUtils.isEmergencyNumber(phoneNumber)) {
            action = Intent.ACTION_DIAL;
        } else {
            action = Intent.ACTION_CALL;
        }
        Intent intent = new Intent(action, Uri.parse("tel:" + phoneNumber));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (PermissionHelper.hasPermission(context, Manifest.permission.CALL_PHONE)) {
            context.startActivity(intent);
        }
    }

    /**
     * Method to disconnect phone automatically and programmatically.
     * Keep this method as it is
     *
     * @param context
     */
    public static void disconnectCall(Context context) {
        SLog.i(TAG, "disconnectCall()");
        try {
            ITelephony telephonyService = getTelephony(context);
            telephonyService.endCall();
        } catch (Exception e) {
            SLog.e(TAG, String.format("disconnectCall() : Exception(%s)", e.getMessage()));
        }
    }

    /**
     * @param context
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static ITelephony getTelephony(Context context) throws ClassNotFoundException,
            NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        TelephonyManager telephony = SystemServiceHelper.getTelephonyManager(context);
        ITelephony telephonyService;
        Class c = Class.forName(telephony.getClass().getName());
        Method m = c.getDeclaredMethod("getITelephony");
        m.setAccessible(true);
        telephonyService = (ITelephony) m.invoke(telephony);
        return telephonyService;
    }

    /**
     * @param context
     */
    public static void acceptCall(Context context) {
        SLog.i(TAG, "acceptCall()");

        try {
            KeyEvent keyDownHeadsetHook = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK);
            KeyEvent keyUpHeasethook = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                intent.putExtra(Intent.EXTRA_KEY_EVENT, keyDownHeadsetHook);
                context.sendOrderedBroadcast(intent, Manifest.permission.CALL_PRIVILEGED);

                intent.putExtra(Intent.EXTRA_KEY_EVENT, keyUpHeasethook);
                context.sendOrderedBroadcast(intent, Manifest.permission.CALL_PRIVILEGED);
            } else {
                // 삼성 보안패치로 인해 아래 MediaSessionLegacyHelper는 동작되지 않음(System만 사용 가능)
                // MediaSessionManager는 LOLLIPOP 이상에서부터 지원하며 NotificationListenerService는
                // JELLY_BEAN_MR2 MR2에서 부터 지원하고 있기 때문에 롤리팝이상일 경우에만 적용되도록 함.
                if (PermissionHelper.isNotificationListenersEnabled(context)) {
                    MediaSessionManager mediaSessionManager = SystemServiceHelper.getMediaSessionManager(context);
                    ComponentName componentName = new ComponentName(context, AISNotificationListenerService.class);
                    List<MediaController> mediaControllerList = mediaSessionManager.getActiveSessions(componentName);

                    for (MediaController mediaController : mediaControllerList) {
                        if (mediaController == null) {
                            continue;
                        }

                        String packageName = mediaController.getPackageName();
                        if ("com.android.server.telecom".equals(packageName) || "com.android.phone".equals(packageName)) {
                            mediaController.dispatchMediaButtonEvent(keyDownHeadsetHook);
                            mediaController.dispatchMediaButtonEvent(keyUpHeasethook);
                            return;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "answerCall sdk : " + Build.VERSION.SDK_INT + " Exception : " + e.getMessage(), e);
        } catch (Error e) {
            Log.e(TAG, "answerCall() Error : " + e.getMessage(), e);
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
            SLog.e(TAG, "isForeground() : className is empty.");
            return false;
        }

        boolean isForeground = false;
        ClientApplicationStatus.ActivityStatusData activityStatusData = ClientApplicationStatus.getInstance().getActivityStatus(className);
        if (activityStatusData == null) {
            isForeground = true;
        } else if (AppIntentCode.ActivityLifeCycle.LifeCycleValue.CREATE.equals(activityStatusData.lifeCycle)
                || AppIntentCode.ActivityLifeCycle.LifeCycleValue.RESUME.equals(activityStatusData.lifeCycle)) {
            isForeground = true;
        } else if ((System.currentTimeMillis() - activityStatusData.changedTime) <= 1500) {
            isForeground = true;
        }

        SLog.i(TAG, String.format("isForegrounded() : className(%s), isForeground(%s)", className, isForeground));
        return isForeground;
    }
}
