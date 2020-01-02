package com.risewide.bdebugapp.util;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hyunho.mo
 * @since 2017.06.21
 */
public class PermissionHelper {
    private static final String TAG = PermissionHelper.class.getSimpleName();
    private static final String SETTINGS_ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    public static final int PERMISSION_REQUESTS = 1001;

    public static final String[] PERMISSION_REQUEST_LIST = new String[]{
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.RECEIVE_MMS,
            Manifest.permission.RECEIVE_WAP_PUSH,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE,
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

    public static boolean allPermissionsGranted(Activity activity) {
        for (String permission : getRequiredPermissions(activity)) {
            if (!isPermissionGranted(activity, permission)) {
                return false;
            }
        }
        return true;
    }

    public static void getRuntimePermissions(Activity activity) {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions(activity)) {
            if (!isPermissionGranted(activity, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(activity, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    private static String[] getRequiredPermissions(Activity activity) {
        try {
            PackageInfo info = activity.getPackageManager()
                    .getPackageInfo(activity.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            BLog.d(TAG, "Permission granted: " + permission);
            return true;
        }
        BLog.d(TAG, "Permission NOT granted: " + permission);
        return false;
    }
}
