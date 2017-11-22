package com.risewide.bdebugapp.util;

import java.util.Formatter;
import java.util.Locale;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import com.risewide.bdebugapp.util.stringconverter.KorStringJosaConverter;

/**
 * @author hyunho.mo, birdea
 *
 * @since 2017.06.30
 */
public class StringHelper {

	private static final String TAG = StringHelper.class.getSimpleName();

    /**
     * @param src
     * @param ch
     * @return
     */
    public static String removeCharAll(String src, char ch) {
        if (TextUtils.isEmpty(src)) {
            return src;
        }
        return src.replace(String.valueOf(ch), "");
    }

    /**
     * @param src
     * @param chArray
     * @return
     */
    public static String removeCharArrayAll(String src, char[] chArray) {
        if (TextUtils.isEmpty(src)) {
            return src;
        }

        int srcCount = src.length();
        int lastMatch = 0;
        StringBuilder sb = new StringBuilder(srcCount);
        for (;;) {
            int currentMatch = indexOf(src, chArray, lastMatch);
            if (currentMatch == -1) {
                break;
            }

            sb.append(src, lastMatch, currentMatch);
            lastMatch = currentMatch + 1;
        }

        if (sb.length() > 0) {
            sb.append(src, lastMatch, srcCount);
            return sb.toString();
        } else {
            return src;
        }
    }

    /**
     * @param src
     * @param chArray
     * @param fromIndex
     * @return
     */
    public static int indexOf(String src, char[] chArray, int fromIndex) {
        final int max = src.length();
        if (fromIndex < 0) {
            fromIndex = 0;
        } else if (fromIndex >= max) {
            return -1;
        }

        for (int i = fromIndex ; i < max ; i++) {
            for (char ch : chArray) {
                if (src.charAt(i) == ch) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * @param s1
     * @param s2
     * @return
     */
    public static boolean containsMutually(String s1, String s2) {
        if (TextUtils.isEmpty(s1) || TextUtils.isEmpty(s2)) {
            return false;
        }

        String longer = s1;
        String shorter = s2;
        if (s1.length() < s2.length()) {
            longer = s2;
            shorter = s1;
        }
        return longer.contains(shorter);
    }

    /**
     * @param src
     * @param ch
     *
     * @return true if src contains ch character, otherwise false.
     */
    public static boolean contains(String src, char ch) {
        return !TextUtils.isEmpty(src) && (src.indexOf(ch) != -1);
    }

    /**
     * @param src
     * @param array
     * @return
     */
    public static boolean contains(String src, String[] array) {
        if ((src == null) || ArrayHelper.isEmpty(array)) {
            return false;
        }

        for (String item : array) {
            if (src.contains(item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param s1
     * @param s2
     * @return
     */
    public static boolean equals(String s1, String s2) {
        return (s1 != null) && s1.equals(s2);
    }

    /**
     * @param format
     * @param args
     * @return
     */
    public static String format(String format, Object... args) {
        try {
            if (args != null) {
                return new Formatter().format(format, args).toString();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return format;
    }

    /**
     * @param l
     * @param format
     * @param args
     * @return
     */
    public static String format(Locale l, String format, Object... args) {
        try {
            if (args != null) {
                return new Formatter(l).format(format, args).toString();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return format;
    }

	/**
	 *
	 * @param format
	 * @param args
	 * @return
	 */
	public static String formatJosa(String format, Object... args) {
		KorStringJosaConverter converter = new KorStringJosaConverter();
		String result = converter.getSentence(format, args);
		//- print in/out
		StringBuilder sb = new StringBuilder();
		for (Object item : args) {
			sb.append(item).append(",");
		}
		SLog.d(TAG, "formatJosa() format:"+format+", args:"+sb.toString());
		SLog.d(TAG, "formatJosa() result:"+result);
		return result;
	}

    /**
     * @param context
     * @param resId
     * @return
     */
	public static String getString(Context context, int resId) {
		return context.getResources().getString(resId);
	}

    /**
     * string
     *
     * @param context
     * @param resId
     * @return
     */
	public static String getStringFromStringArrayRandomly(Context context, int resId) {
        String[] stringArray;
        try {
            stringArray = context.getResources().getStringArray(resId);
        } catch (Resources.NotFoundException e) {
            return null;
        }
        return stringArray[RandomHelper.random(0, stringArray.length-1)];
    }
}
