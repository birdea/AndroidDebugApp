package com.risewide.bdebugapp.util;

import java.util.HashMap;
import java.util.Locale;

/**
 * @author hyunho.mo
 *
 * @since 2017.06.13
 */
public class SystemTimeHelper {
    private static final String TAG = SystemTimeHelper.class.getSimpleName();

    private static HashMap<String, Long> sStartNanosMap = new HashMap<>();

    private static final int NANO_TO_MILLIS = 1000000;


    /**
     * @param key
     * @return
     */
    public static final void startElapsedTime(String key) {
        startElapsedTime(key, true);
    }

    /**
     * @param key
     * @return
     */
    public static final double endElapsedTime(String key) {
        return endElapsedTime(key, false);
    }

    /**
     * @param key
     * @return
     */
    public static final double endElapsedTimeNanos(String key) {
        return endElapsedTime(key, true);
    }

    /**
     * @param key
     * @return
     */
    public static final boolean startElapsedTime(String key, boolean nanos) {
        if (sStartNanosMap.containsKey(key)) {
            return false;
        }

        sStartNanosMap.put(key, System.nanoTime());
        return true;
    }

    /**
     * @param key
     * @return
     */
    public static final boolean startElapsedTimeForcely(String key) {
        sStartNanosMap.put(key, System.nanoTime());
        return true;
    }

    /**
     * @param key
     * @return
     */
    public static final double endElapsedTime(String key, boolean nanos) {
        Long startTime = sStartNanosMap.remove(key);
        if (startTime == null) {
            return -1;
        }

        long elapsed = System.nanoTime() - startTime;
        return (nanos) ? elapsed : toMilliseconds(elapsed);
    }

    /**
     * @param nanos
     * @return
     */
    public static double toMilliseconds(long nanos) {
        double millis = (double)nanos / (double)NANO_TO_MILLIS;
        return Double.parseDouble(String.format(Locale.ENGLISH, "%.2f", millis));
    }
}
