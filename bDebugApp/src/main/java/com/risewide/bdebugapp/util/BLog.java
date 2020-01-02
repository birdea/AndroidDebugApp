package com.risewide.bdebugapp.util;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author seungtae.hwang (birdea@sk.com)
 * @since 2018. 11. 27.
 */
public class BLog {

    private static final String DEFAULT_TAG = "birdea";

    public final static int LEVEL_VERBOSE = 0;
    public final static int LEVEL_DEBUG = 1;
    public final static int LEVEL_INFO = 2;
    public final static int LEVEL_WARNING = 3;
    public final static int LEVEL_ERROR = 4;

    /**
     * Interface of integers representing log levels.
     * @see #LEVEL_VERBOSE
     * @see #LEVEL_INFO
     * @see #LEVEL_WARNING
     * @see #LEVEL_ERROR
     */
    @IntDef({LEVEL_VERBOSE, LEVEL_DEBUG, LEVEL_INFO, LEVEL_WARNING, LEVEL_ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LogLevel {}

    /**
     * A Logger can listen to internal log events
     * and log them to different providers.
     * The default logger will simply post to logcat.
     */
    public interface Logger {

        /**
         * Notifies that an internal log event was just triggered.
         *
         * @param level the log level
         * @param tag the log tag
         * @param message the log message
         * @param throwable an optional throwable
         */
        void log(@LogLevel int level, @NonNull String tag, @NonNull String message, @Nullable Throwable throwable);
    }

    private static int sLevel;

    static Logger sAndroidLogger = new Logger() {
        @Override
        public void log(int level, @NonNull String tag, @NonNull String message, @Nullable Throwable throwable) {
            switch (level) {
                case LEVEL_VERBOSE: Log.v(tag, message, throwable); break;
                case LEVEL_DEBUG: Log.d(tag, message, throwable); break;
                case LEVEL_INFO: Log.i(tag, message, throwable); break;
                case LEVEL_WARNING: Log.w(tag, message, throwable); break;
                case LEVEL_ERROR: Log.e(tag, message, throwable); break;
            }
        }
    };

    static {
        setLogLevel(LEVEL_VERBOSE);
    }

    /**
     * Sets the log sLevel for logcat events.
     *
     * @see #LEVEL_VERBOSE
     * @see #LEVEL_INFO
     * @see #LEVEL_WARNING
     * @see #LEVEL_ERROR
     * @param logLevel the desired log sLevel
     */
    public static void setLogLevel(@LogLevel int logLevel) {
        sLevel = logLevel;
    }

    private static boolean printable(int messageLevel) {
        return sLevel <= messageLevel;
    }

    public static void v(Object... data) {
        v(DEFAULT_TAG, data);
    }

    public static void v(String tag, Object... data) {
        log(tag, LEVEL_VERBOSE, data);
    }

    public static void d(Object... data) {
        d(DEFAULT_TAG, data);
    }

    public static void d(String tag, Object... data) {
        log(tag, LEVEL_DEBUG, data);
    }

    public static void i(Object... data) {
        i(DEFAULT_TAG, data);
    }

    public static void i(String tag, Object... data) {
        log(tag, LEVEL_INFO, data);
    }

    public static void w(Object... data) {
        w(DEFAULT_TAG, data);
    }

    public static void w(String tag, Object... data) {
        log(tag, LEVEL_WARNING, data);
    }

    public static void e(Object... data) {
        e(DEFAULT_TAG, data);
    }

    public static void e(String tag, Object... data) {
        log(tag, LEVEL_ERROR, data);
    }

    private static void log(@NonNull String tag, @LogLevel int level, @NonNull Object... data) {
        if (!printable(level)) {
            return;
        }

        StringBuilder message = new StringBuilder();
        Throwable throwable = null;
        for (Object object : data) {
            if (object instanceof Throwable) {
                throwable = (Throwable) object;
            }
            message.append(object);
            message.append(" ");
        }
        String string = message.toString().trim();
        sAndroidLogger.log(level, tag, string, throwable);
    }
}
