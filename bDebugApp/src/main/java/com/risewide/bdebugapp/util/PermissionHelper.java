package com.risewide.bdebugapp.util;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

/**
 * @author hyunho.mo
 * @since 2017.06.21
 */
public class PermissionHelper {
    private static final String TAG = PermissionHelper.class.getSimpleName();
    private static final String SETTINGS_ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";

    public static final String[] PERMISSION_REQUEST_LIST = new String[]{
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.RECEIVE_MMS,
            Manifest.permission.RECEIVE_WAP_PUSH,
    };

    /**
     * @param context
     * @return
     */
    public static boolean hasPermission(Context context, String permission) {
        return (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * 권한들이 있는지 승인되어 있는지 체크
     * true : 모든 권한들이 승인됨
     * false : 권한들 중 하나라도 승인되지 않음
     *
     * @param context
     * @param permission
     * @return
     */
    public static boolean hasPermission(Context context, String[] permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean needPermission = false;
            for (String requestPermission : permission) {
                if (!hasPermission(context, requestPermission)) {
                    needPermission = true;
                    break;
                }
            }
            if (!needPermission) {
                result = true;
            }
        } else {
            result = true;
        }
        return result;
    }

    /**
     * @param context
     * @return
     */
    public static boolean isNotificationListenersEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            String enableNotificationListeners = Settings.Secure.getString(context.getContentResolver(),
                    SETTINGS_ENABLED_NOTIFICATION_LISTENERS);
            if ((enableNotificationListeners == null) || TextUtils.isEmpty(enableNotificationListeners.trim())) {
                return false;
            }

            String[] components = enableNotificationListeners.split(":");
            for (String component : components) {
                ComponentName componentName = ComponentName.unflattenFromString(component);
                if (component == null) {
                    continue;
                }

                if (componentName.getPackageName().equals(context.getPackageName())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @param packageName
     * @return
     */
    public static boolean hasUsageStatsPermission(Context context, String packageName) {
        if ((context == null) || (TextUtils.isEmpty(packageName))) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);
        }
        return false;
    }

    /**
     * @param context
     * @return
     */
    public static boolean hasOverlayPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        }
        return true;
    }
}
