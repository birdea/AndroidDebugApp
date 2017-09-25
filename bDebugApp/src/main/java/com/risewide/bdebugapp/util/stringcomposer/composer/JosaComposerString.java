package com.risewide.bdebugapp.util.stringcomposer.composer;

import java.util.regex.Pattern;

import com.risewide.bdebugapp.util.SLog;
import com.risewide.bdebugapp.util.stringcomposer.data.JosaSet;
import com.risewide.bdebugapp.util.stringcomposer.spec.MatcherAlphabetToKorean;
import com.risewide.bdebugapp.util.stringcomposer.spec.MatcherArabicToKorean;

import android.text.TextUtils;

/**
 * Created by birdea on 2016-11-22.
 */

public class JosaComposerString extends JosaComposer<String> {

	private static final String PATTERN_UNICODE_KOREAN = "^[가-힝]*$";
	private static final String PATTERN_UNICODE_ALPHABET = "^[A-Za-z]*$";

	@Override
	public JosaSet select(String word, String josaWithJongsung, String josaWithoutJongsung) {
		// step.1 - check if param is empty
		if (TextUtils.isEmpty(word) || TextUtils.isEmpty(josaWithJongsung)
				|| TextUtils.isEmpty(josaWithoutJongsung)) {
			SLog.w("[except] getMultiSentenceWithJosa. StringUtils.isEmpty word:" + word + ", arg1:"
					+ josaWithJongsung + ", arg2:" + josaWithoutJongsung);
			return new JosaSet("","");
		}
		// step.2 - get last character and convert char (from alphabet, digit)
		char lastChar = getConvertChar(word.charAt(word.length() - 1));

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

	private char getConvertChar(char c) {
		if (isAlphabetLetter(c)) {
			SLog.d("isAlphabetLetter[true] :"+c);
			c = MatcherAlphabetToKorean.getKoreanWord(c).getKoreanLastChar();
		}
		if (isDigit(c)) {
			SLog.d("isAlphabetLetter[true] :"+c);
			c = MatcherArabicToKorean.get(Long.valueOf(c)).getKoreanChar();

		}
		return c;
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
