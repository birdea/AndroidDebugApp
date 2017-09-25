package com.risewide.bdebugapp.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 메소드 실행 시간 테스트를 위한 간편 모듈
 * Created by birdea on 2016-11-17.
 */
public class TimeLap {

	private long timeStart;
	private long timeMid;
	private long timeEnd;

	public TimeLap() {
		timeStart = System.currentTimeMillis();
	}
	public TimeLap(long start) {
		timeStart = start;
	}

	/**
	 * start time delay (init)
	 */
	public long start() {
		long now = System.currentTimeMillis();
		Mode.START.print(now);
		timeStart = now;
		return 0;
	}

	/**
	 * mid time delay (run)
	 */
	public long mid() {
		long now = System.currentTimeMillis();
		Mode.MID.print(timeStart, timeMid, now);
		long gap = (timeMid >0)?now - timeMid :now- timeStart;
		timeMid = now;
		return gap;
	}

	/**
	 * total time delay (end)
	 */
	public long end() {
		long now = System.currentTimeMillis();
		Mode.END.print(timeStart, timeEnd, now);
		long gap = now - timeStart;
		timeEnd = now;
		return gap;
	}

	public long getDelay() {
		return Mode.getDelay(timeStart, timeEnd);
	}

	//////////////////////////////////////////////////////

	private static final String LOG_FORM_START = "[timeLap:%s] start - start=0(ms), %s";
	private static final String LOG_FORM_MID = "[timeLap:%s] run - mid=%d(ms), end=%d(ms)";
	private static final String LOG_FORM_END = "[timeLap:%s] end - mid=%d(ms), end=%d(ms), %s";

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS", Locale.KOREA);

	private enum Mode{
		START {
			@Override
			public void print(long... args) {
				String dateTime = DATE_FORMAT.format(args[0]);
				String msg = String.format(LOG_FORM_START, name(), dateTime);
				SLog.d(msg);
			}
		},
		MID {
			@Override
			public void print(long... args) {
				long s = args[0];
				long m = args[1];
				long e = args[2];
				//
				long delayMid = getDelay(s, e);
				long delayEnd = getDelay(m, e);
				String msg = String.format(LOG_FORM_MID, name(), delayEnd, delayMid);
				SLog.d(msg);
			}
		},
		END {
			@Override
			public void print(long... args) {
				long s = args[0];
				long m = args[1];
				long e = args[2];
				long delayMid = getDelay(m, e);
				long delayEnd = getDelay(s, e);
				String dateTime = DATE_FORMAT.format(e);
				String msg = String.format(LOG_FORM_END, name(), delayMid, delayEnd, dateTime);
				SLog.d(msg);
			}
		},
		;

		abstract public void print(long... args);

		private static long getDelay(long past, long now) {
			if (past < 1) {
				return 0;
			}
			return (now - past);
		}
	}
}
