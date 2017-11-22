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

	public static void d() {
		d("");
	}

	public static void w() {
		w("");
	}

	public static void e() {
		e("");
	}

	public static void i() {
		i("");
	}

	public static void d(String format, Object... args) {
		d(String.format(format, args));
	}

	public static void w(String format, Object... args) {
		w(String.format(format, args));
	}

	public static void e(String format, Object... args) {
		e(String.format(format, args));
	}

	public static void i(String format, Object... args) {
		i(String.format(format, args));
	}

	public static void d(String message) {
		if (isDebug()) {
			Log.d(getTag(), getMessage(message));
		}
	}

	public static void w(String message) {
		if (isDebug()) {
			Log.w(getTag(), getMessage(message));
		}
	}

	public static void e(String message) {
		if (isDebug()) {
			Log.e(getTag(), getMessage(message));
		}
	}

	public static void i(String message) {
		if (isDebug()) {
			Log.i(getTag(), getMessage(message));
		}
	}

	private static String getTag() {
		int pid = Process.myPid();
		long tid = Thread.currentThread().getId();
		return TAG + "(" + pid + ":" + tid + ")";
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
