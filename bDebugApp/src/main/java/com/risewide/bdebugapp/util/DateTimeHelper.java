package com.risewide.bdebugapp.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author hyunho.mo
 *
 * @since 2017.06.13
 */
public class DateTimeHelper {
    private static final String TAG = DateTimeHelper.class.getSimpleName();

    public static final String DATE_TIME_FORMAT_PATTERN_1 = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_FORMAT_PATTERN_2 = "yy년 MM월 dd일 HH:mm:ss";
	public static final String NA = "N/A";

    /**
     * @param pattern
     * @param milliseconds
     * @return
     */
    public static String format(String pattern, long milliseconds) {
        if (milliseconds < 0) {
            return null;
        }
        return new SimpleDateFormat(pattern).format(new Date(milliseconds));
    }

    /**
     * @param dateAmount
     *
     * @return 현재 날짜(00시 00분 00초 00ms는 고정) 기준으로 dateAmount 만큼 증감된 ms time.
     */
    public static long getTimeInMillisWithDateAmount(int dateAmount) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, dateAmount);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * @param hourAmount
     *
     * @return 현재 날짜와 시간(00ms는 고정) 기준으로 hourAmount(24시간 단위) 만큼 증감된 ms time.
     */
    public static long getTimeInMillisWithHourAmount(int hourAmount) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, hourAmount);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * @param year
     * @param month
     * @param date
     * @param hourOfDay
     * @param minute
     * @param second
     *
     * @return 특정 year년 month월 date일 hourOfDay시 minute분 second초(00ms는 고정)의 ms time.
     */
    public static long getTimeInMillis(int year, int month, int date, int hourOfDay, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, date);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

	/**
	 * @param simpleDateFormat
	 * @param dateTime
	 * @return
	 */
	public static String getSimpleDate(SimpleDateFormat simpleDateFormat, long dateTime) {
		if (dateTime < 1) {
			return NA;
		}
		return simpleDateFormat.format(dateTime);
	}

    /**
     * Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT
     * represented by this <tt>Date</tt> object.
     *
     * @return  the number of milliseconds since January 1, 1970, 00:00:00 GMT
     *          represented by this date.
     */
	public static long getCurrentTime() {
        return new Date().getTime();
    }
}
