package com.risewide.bdebugapp.util.stringconverter.converter;

import java.util.regex.Pattern;

import android.text.TextUtils;

import com.risewide.bdebugapp.util.stringconverter.data.JosaSet;
import com.risewide.bdebugapp.util.stringconverter.data.KoreanJosa;
import com.risewide.bdebugapp.util.stringconverter.data.MatcherAlphabetToKorean;
import com.risewide.bdebugapp.util.stringconverter.data.MatcherArabicToKorean;

/**
 * Created by birdea on 2016-11-22.
 */

public class JosaConverterObject extends JosaConverter<Object> {

	private static final String PATTERN_UNICODE_KOREAN = "^[가-힝]*$";
	private static final String PATTERN_UNICODE_ALPHABET = "^[A-Za-z]*$";

	@Override
	public JosaSet select(Object obj, KoreanJosa koreanJosa) {
		// step.1 - check if param is empty
		if (obj == null || koreanJosa == null || TextUtils.isEmpty(koreanJosa.getJosaWithJongsung())
				|| TextUtils.isEmpty(koreanJosa.getJosaWithoutJongsung())) {
			log("[except] StringUtils.isEmpty obj:" + obj + ", koreanJosa:"
					+ koreanJosa);
			return new JosaSet("","");
		}
		// step.2 - get last character and convert char (from alphabet, digit)
		char lastChar = getConvertChar(obj);

		// step.3 - check if korean then process to get the set of josa
		if (isKoreanChar(lastChar)) {
			log("isKoreanChar[true] :"+lastChar);
			return koreanJosa.process(lastChar);
		}
		log("isUnknownLetter[true] :"+lastChar);
		return new JosaSet("","");
	}

	private char getConvertChar(Object obj) {
		char lastChar = 1;
		// case of Long
		if (obj instanceof Long) {
			log("obj instanceof Long[true] :"+obj);
			Long val = (Long)obj;
			return MatcherArabicToKorean.get(val).getKoreanChar();
		}
		// case of Integer
		if (obj instanceof Integer) {
			log("obj instanceof Integer[true] :"+obj);
			Integer val = (Integer)obj;
			return MatcherArabicToKorean.get(val).getKoreanChar();
		}
		// case of Short
		if (obj instanceof Short) {
			log("obj instanceof Short[true] :"+obj);
			Short val = (Short)obj;
			return MatcherArabicToKorean.get(val).getKoreanChar();
		}

		// case of String
		if (obj instanceof String) {
			log("obj instanceof String[true] :"+obj);
			String word = (String)obj;
			// case of String > Integer, ex) "1209390123"
			try {
				Long val = Long.parseLong(word);
				return MatcherArabicToKorean.get(val).getKoreanChar();
			} catch(NumberFormatException ignore) {
			}
			lastChar = word.charAt(word.length() - 1);
		}
		if (isAlphabetLetter(lastChar)) {
			log("isAlphabetLetter[true] :"+lastChar);
			lastChar = MatcherAlphabetToKorean.getKoreanWord(lastChar).getKoreanLastChar();
		}
		if (isDigit(lastChar)) {
			log("isDigit[true] :"+lastChar);
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
		return Pattern.matches(PATTERN_UNICODE_ALPHABET, String.valueOf(c));
	}

	public static boolean isDigit(char c) {
		return Character.isDigit(c);
	}
	
	private void log(String msg) {
		//SLog.d(msg);
	}
}
