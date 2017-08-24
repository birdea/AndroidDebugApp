package com.risewide.bdebugapp.communication.util;

import java.util.regex.Pattern;

import android.text.TextUtils;

/**
 * Created by sktechx on 2017-05-16.
 */

public class StringMaskHelper {

	public enum StringType {
		ENG, KOR, MIX
	}

	public enum CharRegExType {
		SPACE("\\p{Space}"), // White Space : [\t\n\x0B\f\r]
		PUNCT("\\p{Punct}"), // Punctuation : [!"#$%&'()*+,-./:;<=>?@[\]^_`{\}]
		DIGIT("\\p{Digit}"); // Decimal Digits : [0-9]

		public final String regularExpress;
		CharRegExType(String regex) {
			regularExpress = regex;
		}
	}

	/**
	 * 숫자, 특수문자, 스페이스 제거후 한글인지 영문인지 섞여있는지 판단 글자수랑 바이트랑 같으면 영문, 글자수*3과 바이트랑 같으면 한글
	 *
	 * @param src
	 * @return
	 */
	public static StringType getStringType(String src) {
		if (TextUtils.isEmpty(src)) {
			return null;
		}

		// 숫자, 특수문자, 스페이스를 제거하고 체크
		String temp = src.replaceAll("\\p{Digit}|\\p{Space}|\\p{Punct}", "");
		if (temp.length() == temp.getBytes().length) {
			return StringType.ENG;
		} else if (3 * temp.length() == temp.getBytes().length) {
			return StringType.KOR;
		} else {
			return StringType.MIX;
		}
	}

	private static Pattern pattern;
	/**
	 * @param src
	 * @return
	 */
	public static String remove(String src) {
		//
		if (src == null) {
			return src;
		}

		if (pattern == null) {
			final CharRegExType[] CHAR_REG_EX_TYPE = {CharRegExType.DIGIT};
			StringBuilder builder = new StringBuilder();
			for (int i = 0 ; i < CHAR_REG_EX_TYPE.length ; i++) {
				if (i != 0) {
					builder.append("|");
				}
				builder.append(CHAR_REG_EX_TYPE[i].regularExpress);
			}
			pattern = Pattern.compile(builder.toString());
		}
		String dst = pattern.matcher(src).replaceAll("0");
		return dst;
	}

	/**
	 * @param s1
	 * @param s2
	 *
	 * @return true if s1 equals s2, otherwise false.
	 */
	public static boolean equals(String s1, String s2) {
		return (s1 != null) && s1.equals(s2);
	}
}
