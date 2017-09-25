package com.risewide.bdebugapp.util.stringcomposer;


import com.risewide.bdebugapp.util.SLog;

/**
 * Created by birdea on 2016-11-22.
 */

public class JosaComposeInteger extends JosaComposer<Integer> {

	@Override
	public JosaSet select(Integer word, String josaWithJongsung, String josaWithoutJongsung) {
		PatternArabicKorean pak = PatternArabicKorean.get(word);
		//
		String lastWord = pak.pattern;
		SLog.d("JosaComposeInteger.selected:"+lastWord + ", word:"+word);
		// step.2 - check if korean word or not
		char lastChar = lastWord.charAt(lastWord.length() - 1);
		if (false == JosaComposeString.isKoreanChar(lastChar)) {
			return new JosaSet("","");
		}
		// step.3 - select the josa
		if ((lastChar - 0xAC00) % 28 > 0) {
			return new JosaSet(josaWithJongsung, josaWithoutJongsung);
		} else {
			return new JosaSet(josaWithoutJongsung, josaWithJongsung);
		}
	}
}
