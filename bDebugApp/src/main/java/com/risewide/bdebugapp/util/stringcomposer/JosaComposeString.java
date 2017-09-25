package com.risewide.bdebugapp.util.stringcomposer;

import java.util.regex.Pattern;

import com.risewide.bdebugapp.util.SLog;

import android.text.TextUtils;

/**
 * Created by birdea on 2016-11-22.
 */

public class JosaComposeString extends JosaComposer<String>{

	private static final String PATTERN_UNICODE_SCOPE_KOREAN = "[가-힝]";

	@Override
	public JosaSet select(String word, String josaWithJongsung, String josaWithoutJongsung) {
		// step.1 - check if String is empty
		if (TextUtils.isEmpty(word) || TextUtils.isEmpty(josaWithJongsung)
				|| TextUtils.isEmpty(josaWithoutJongsung)) {
			SLog.i("[except] getMultiSentenceWithJosa. StringUtils.isEmpty word:" + word + ", arg1:"
					+ josaWithJongsung + ", arg2:" + josaWithoutJongsung);
			throw new IllegalArgumentException("The params should not be empty!");
		}
		// step.2 - check if korean word or not
		char lastChar = word.charAt(word.length() - 1);
		if (false == isKoreanChar(lastChar)) {
			return new JosaSet("","");
		}
		// step.3 - select the josa
		if ((lastChar - 0xAC00) % 28 > 0) {
			return new JosaSet(josaWithJongsung, josaWithoutJongsung);
		} else {
			return new JosaSet(josaWithoutJongsung, josaWithJongsung);
		}
	}

	/**
	 * <p>한글 문자를 인식하는 케이스 모음</p>
	 * <li>method 1. 한글의 제일 처음과 끝의 범위밖일 경우는 오류<br>
	 * if (c < 0xAC00 || c > 0xD7A3) { return false; }</li>
	 * <li>method 2. Character Type이 5인 경우<br>
	 * if(Character.getType(c) == 5) { hasKoreanCharacter = true; break; }</li>
	 */
	public static boolean isKoreanChar(char c) {
		return (Pattern.matches(PATTERN_UNICODE_SCOPE_KOREAN, String.valueOf(c)));
	}

	private boolean hasKoreanChar(String word) {
		for (int i = 0; i < word.length(); i++) {
			char c = word.charAt(i);
			if (isKoreanChar(c)) {
				return true;
			}
		}
		return false;
	}
}
