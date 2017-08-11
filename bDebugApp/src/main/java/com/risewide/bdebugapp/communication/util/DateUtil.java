package com.risewide.bdebugapp.communication.util;

import java.text.SimpleDateFormat;

/**
 * Created by birdea on 2016-11-01.
 */

public class DateUtil {

	public static final SimpleDateFormat sdf_ymd = new SimpleDateFormat("yyMMdd_hhmmss");
	public static final String NA = "N/A";

	public static String getSimpleDate(long dateTime) {
		return getSimpleDate(sdf_ymd, dateTime);
	}

	public static String getSimpleDate(SimpleDateFormat simpleDateFormat, long dateTime) {
		if (dateTime < 1) {
			return NA;
		}
		return simpleDateFormat.format(dateTime);
	}
}
