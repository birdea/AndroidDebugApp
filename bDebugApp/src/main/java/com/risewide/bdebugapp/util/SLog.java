package com.risewide.bdebugapp.util;

import android.os.Process;
import android.util.Log;

import com.risewide.bdebugapp.BuildConfig;

/**
 * Log 관련 클래스.
 *
 * @author nam, birdea
 */
public class SLog {

	private final static String TAG = "NuguSdk";

	private static boolean isDebug() {
		return BuildConfig.DEBUG;
	}

	public static void d(String message) {
		d(TAG, message);
	}

	public static void d(String tag, String message) {
		if (isDebug()) {
			Log.d(tag, getMessage(message));
		}
	}

	public static void w(String message) {
		w(TAG, message);
	}

	public static void w(String tag, Object message) {
		if (isDebug()) {
		    if (message instanceof String) {
                Log.w(tag, getMessage((String)message));
            }
            else if (message instanceof Throwable) {
                StringBuilder sb = new StringBuilder();

                Throwable throwable = (Throwable) message;
                try {
                    sb.append(throwable.toString());
                    sb.append("\n");
                    StackTraceElement[] element = throwable.getStackTrace();

                    for(int idx = 0; idx < element.length; ++idx) {
                        sb.append("\tat ");
                        sb.append(element[idx].toString());
                        sb.append("\n");
                    }
                } catch (Exception var5) {
                    d(TAG, "[Exception] " + throwable.getMessage());
                    return;
                }

                d(TAG, "[Exception] " + sb.toString());
            }
		}
	}

	public static void e(String tag, String message) {
		if (isDebug()) {
			Log.e(tag, getMessage(message));
		}
	}

	public static void i(String tag, String message) {
		if (isDebug()) {
			Log.i(tag, getMessage(message));
		}
	}

	private static String getMessage(String message) {
		StackTraceElement stack = getCurrentInvokedElement();
		if (stack != null) {
			String className = shortenClassName(stack.getClassName());
			String methodName = stack.getMethodName();
			int line = stack.getLineNumber();
			return className + "." + methodName + "(" + line + ") " + message;
		} else {
			return "?.?(?) " + message;
		}
	}

	private static StackTraceElement getCurrentInvokedElement() {
		StackTraceElement stacks[] = Thread.currentThread().getStackTrace();
		if (stacks != null && stacks.length > 3) {
			int idx = 4;
			while (stacks[idx].getClassName().equals(SLog.class.getName()) && idx < stacks.length) {
				idx++;
			}
			return stacks[idx];

		}
		return null;
	}

	private static String shortenClassName(String fullClassName) {
		if (fullClassName != null) {
			int lastIdx = fullClassName.lastIndexOf('.');
			if (lastIdx != -1) {
				return fullClassName.substring(lastIdx + 1);
			}
		}
		return "";
	}
}
