package com.risewide.bdebugapp.util;

import android.util.Log;

import com.risewide.bdebugapp.BuildConfig;

/**
 * Log 관련 클래스.
 *
 * @author nam, birdea
 */
public class SLog {

	public static String makeTag(Class c) {
		return "[S]" + c.getSimpleName();
	}

	public enum Mode {
		OFF, ON,;

		public boolean isActive() {
			return ON.equals(this);
		}
	}

	public enum Level {
		DEFAULT("#000000"), VERBOSE("#FFFF00"), DEBUG("#00FF00"), INFO("#0000FF"), WARN("#00FFFF"), ERROR("#FF0000"),;

		String color;

		Level(String _color) {
			color = _color;
		}
	}

	private static Mode modePrintOut = Mode.ON;

	/**
	 * check if log is active or not
	 *
	 * @return true/false
	 */
	protected static boolean loggable() {
		return (isActive() || BuildConfig.DEBUG);
	}

	/**
	 * set active or deactive logcat + debugview
	 */
	public static void activeLog() {
		modePrintOut = Mode.ON;
	}

	/**
	 * set active or deactive logcat + debugview
	 */
	public static void deactiveLog() {
		modePrintOut = Mode.OFF;
	}

	private static boolean isActive() {
		return modePrintOut.isActive();
	}

	/**
	 * Handy-Logger of [verbose] priority
	 *
	 * @param obj
	 */
	public static void v(Object obj) {
		v(makeTag(SLog.class), obj);
	}

	public static void v(String tag, Object msg) {
		if (false == loggable()) {
			if (BuildConfig.DEBUG) {
				printLogcat(tag, msg, Level.VERBOSE);
			}
			return;
		}

		printLogcat(tag, msg, Level.VERBOSE);
	}

	/**
	 * Handy-Logger of [debug] priority
	 *
	 * @param obj
	 */
	public static void d(Object obj) {
		d(makeTag(SLog.class), obj);
	}

	public static void d(String tag, Object msg) {
		if (false == loggable()) {
			if (BuildConfig.DEBUG) {
				printLogcat(tag, msg, Level.DEBUG);
			}
			return;
		}

		printLogcat(tag, msg, Level.DEBUG);
	}

	/**
	 * Handy-Logger of [info] priority
	 *
	 * @param obj
	 */
	public static void i(Object obj) {
		i(makeTag(SLog.class), obj);
	}

	public static void i(String tag, Object msg) {
		if (false == loggable()) {
			if (BuildConfig.DEBUG) {
				printLogcat(tag, msg, Level.INFO);
			}
			return;
		}

		printLogcat(tag, msg, Level.INFO);
	}

	/**
	 * Handy-Logger of [warn] priority
	 *
	 * @param obj
	 */
	public static void w(Object obj) {
		w(makeTag(SLog.class), obj);
	}

	public static void w(String tag, Object msg) {
		if (false == loggable()) {
			if (BuildConfig.DEBUG) {
				printLogcat(tag, msg, Level.ERROR);
			}
			return;
		}

		printLogcat(tag, msg, Level.WARN);
	}

	/**
	 * Handy-Logger of [error] priority
	 *
	 * @param obj
	 */
	public static void e(Object obj) {
		e(makeTag(SLog.class), obj);
	}

	public static void e(String tag, Object msg) {
		if (false == loggable()) {
			if (BuildConfig.DEBUG) {
				printLogcat(tag, msg, Level.ERROR);
			}
			return;
		}

		printLogcat(tag, msg, Level.ERROR);
	}

	private static void printLogcat(String tag, Object msg, Level level) {
		try {
			if (msg instanceof String) {
				// print out general string message
				switch (level) {
				case VERBOSE:
					Log.v(tag, buildMsg(msg));
					break;
				case DEBUG:
					Log.d(tag, buildMsg(msg));
					break;
				case INFO:
					Log.i(tag, buildMsg(msg));
					break;
				case WARN:
					Log.w(tag, buildMsg(msg));
					break;
				case ERROR:
					Log.e(tag, buildMsg(msg));
					break;
				}
			} else if (msg instanceof Throwable) {
				// print out throwable message
				switch (level) {
				case VERBOSE:
					Log.v(tag, "Throwable", (Throwable) msg);
					break;
				case DEBUG:
					Log.d(tag, "Throwable", (Throwable) msg);
					break;
				case INFO:
					Log.i(tag, "Throwable", (Throwable) msg);
					break;
				case WARN:
					Log.w(tag, "Throwable", (Throwable) msg);
					break;
				case ERROR:
					Log.e(tag, "Throwable", (Throwable) msg);
					break;
				}
			} else {
				// do nothing is ok.
			}
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println(msg);
		}
	}

	private static String buildMsg(Object msg) {
		StackTraceElement ste = Thread.currentThread().getStackTrace()[4];

		StringBuilder sb = new StringBuilder()
//		sb.append("[").append(ste.getFileName().replace(".java", "")).append("::");;
//		sb.append(ste.getMethodName()).append("]").append(msg);
		.append(msg);

		return sb.toString();
	}
}
