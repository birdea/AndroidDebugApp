package com.risewide.bdebugapp.util.stringconverter.helper;

import android.support.annotation.Nullable;

/**
 * TextUtils 사용시 UnitTest Mock object 구현 작업을 해야해서 대용
 * @see android.text.TextUtils#isEmpty(CharSequence)
 * Created by birdea on 2017-09-26.
 */

public class StringUtils {
	/**
	 * Returns true if the string is null or 0-length.
	 * @param str the string to be examined
	 * @return true if str is null or zero length
	 */
	public static boolean isEmpty(@Nullable CharSequence str) {
		if (str == null || str.length() == 0)
			return true;
		else
			return false;
	}
}
