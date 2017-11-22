package com.risewide.bdebugapp.util;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.media.AudioManager;
import android.media.session.MediaSessionManager;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.WindowManager;

public class SystemServiceHelper {

    /**
     * @param context
     * @return
     */
    public static WindowManager getWindowManager(Context context) {
        return (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
    }

    /**
     * @param context
     * @return
     */
    public static LayoutInflater getLayoutInflater(Context context) {
        return (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * @param context
     * @return
     */
    public static ActivityManager getActivityManager(Context context) {
        return (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    /**
     * @param context
     * @return
     */
    public static UsageStatsManager getUsageStatsManager(Context context) {
        return (UsageStatsManager)context.getSystemService(Context.USAGE_STATS_SERVICE);
    }

    /**
     * @param context
     * @return
     */
    public static AlarmManager getAlarmManager(Context context) {
        return (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    }

    /**
     * @param context
     * @return
     */
    public static AppOpsManager getAppOpsManager(Context context) {
        return (AppOpsManager)context.getSystemService(Context.APP_OPS_SERVICE);
    }

    /**
     * @param context
     * @return
     */
    public static Vibrator getVibrator(Context context) {
        return (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    /**
     * @param context
     * @return
     */
    public static AudioManager getAudioManager(Context context) {
        return (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * @param context
     * @return
     */
    public static MediaSessionManager getMediaSessionManager(Context context) {
        return (MediaSessionManager)context.getSystemService(Context.MEDIA_SESSION_SERVICE);
    }

    /**
     * @param context
     * @return
     */
    public static TelephonyManager getTelephonyManager(Context context) {
        return (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
    }
}
