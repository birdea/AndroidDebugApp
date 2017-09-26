package com.risewide.bdebugapp.util.stringconverter.converter;

import java.util.regex.Pattern;

import com.risewide.bdebugapp.util.SLog;
import com.risewide.bdebugapp.util.stringconverter.data.JosaSet;
import com.risewide.bdebugapp.util.stringconverter.spec.MatcherAlphabetToKorean;
import com.risewide.bdebugapp.util.stringconverter.spec.MatcherArabicToKorean;

import android.text.TextUtils;

/**
 * Created by birdea on 2016-11-22.
 */

public class JosaConverterObject extends JosaConverter<Object> {

	private static final String PATTERN_UNICODE_KOREAN = "^[가-힝]*$";
	private static final String PATTERN_UNICODE_ALPHABET = "^[A-Za-z]*$";

	@Override
	public JosaSet select(Object obj, String josaWithJongsung, String josaWithoutJongsung) {
		// step.1 - check if param is empty
		if (obj == null || TextUtils.isEmpty(josaWithJongsung)
				|| TextUtils.isEmpty(josaWithoutJongsung)) {
			SLog.w("[except] getMultiSentenceWithJosa. StringUtils.isEmpty obj:" + obj + ", arg1:"
					+ josaWithJongsung + ", arg2:" + josaWithoutJongsung);
			return new JosaSet("","");
		}
		// step.2 - get last character and convert char (from alphabet, digit)
		char lastChar = getConvertChar(obj);

		// step.3 - check if korean then process to get the set of josa
		if (isKoreanChar(lastChar)) {
			SLog.d("isKoreanChar[true] :"+lastChar);
			if ((lastChar - 0xAC00) % 28 > 0) {
				return new JosaSet(josaWithJongsung, josaWithoutJongsung);
			} else {
				return new JosaSet(josaWithoutJongsung, josaWithJongsung);
			}
		}
		SLog.d("isUnknownLetter[true] :"+lastChar);
		return new JosaSet("","");
	}

	private char getConvertChar(Object obj) {
		char lastChar = 1;
		// case of Long
		if (obj instanceof Long) {
			SLog.d("obj instanceof Long[true] :"+obj);
			Long val = (Long)obj;
			return MatcherArabicToKorean.get(val).getKoreanChar();
		}
		// case of Integer
		if (obj instanceof Integer) {
			SLog.d("obj instanceof Integer[true] :"+obj);
			Integer val = (Integer)obj;
			return MatcherArabicToKorean.get(val).getKoreanChar();
		}
		// case of Short
		if (obj instanceof Short) {
			SLog.d("obj instanceof Short[true] :"+obj);
			Short val = (Short)obj;
			return MatcherArabicToKorean.get(val).getKoreanChar();
		}

		// case of String
		if (obj instanceof String) {
			SLog.d("obj instanceof String[true] :"+obj);
			String word = (String)obj;
			try {
				Long val = Long.parseLong(word);
				return MatcherArabicToKorean.get(val).getKoreanChar();
			} catch(NumberFormatException ignore) {
			}
			lastChar = word.charAt(word.length() - 1);
		}
		if (isAlphabetLetter(lastChar)) {
			SLog.d("isAlphabetLetter[true] :"+lastChar);
			lastChar = MatcherAlphabetToKorean.getKoreanWord(lastChar).getKoreanLastChar();
		}
		if (isDigit(lastChar)) {
			SLog.d("isDigit[true] :"+lastChar);
			lastChar = MatcherArabicToKorean.get(Character.getNumericValue(lastChar)).getKoreanChar();
		}
		return lastChar;
	}

	public static boolean isKoreanChar(String str) {
		return (Pattern.matches(PATTERN_UNICODE_KOREAN, str));
	}

	public static boolean isKoreanChar(char c) {
		return isKoreanChar(String.valueOf(c));
	}

	public static boolean isAlphabetLetter(char c) {
		return Character.isAlphabetic(c);
	}

	public static boolean isDigit(char c) {
		return Character.isDigit(c);
	}
}
