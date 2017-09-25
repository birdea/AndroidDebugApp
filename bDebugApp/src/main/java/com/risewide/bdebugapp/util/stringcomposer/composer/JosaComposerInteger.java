package com.risewide.bdebugapp.util.stringcomposer.composer;


import android.text.TextUtils;

import com.risewide.bdebugapp.util.SLog;
import com.risewide.bdebugapp.util.stringcomposer.data.JosaSet;
import com.risewide.bdebugapp.util.stringcomposer.spec.MatcherArabicToKorean;

/**
 * Created by birdea on 2016-11-22.
 */

public class JosaComposerInteger extends JosaComposer<Integer> {

	@Override
	public JosaSet select(Integer integer, String josaWithJongsung, String josaWithoutJongsung) {
		// step.1 - check if param is empty
		if (integer == null || TextUtils.isEmpty(josaWithJongsung) || TextUtils.isEmpty(josaWithoutJongsung)) {
			SLog.w("[except] getMultiSentenceWithJosa. StringUtils.isEmpty integer:" + integer + ", arg1:"
					+ josaWithJongsung + ", arg2:" + josaWithoutJongsung);
			return new JosaSet("","");
		}
		MatcherArabicToKorean pak = MatcherArabicToKorean.get(integer);
		// step.2 - get last character
		char lastChar = pak.getKoreanChar();
		SLog.d("JosaComposerInteger.selected-lastChar:"+lastChar + ", word:"+integer);
		// step.3 - check if korean, since it should be
		if (JosaComposerObject.isKoreanChar(lastChar)) {
			// step.3 - select the josa
			if ((lastChar - 0xAC00) % 28 > 0) {
				return new JosaSet(josaWithJongsung, josaWithoutJongsung);
			} else {
				return new JosaSet(josaWithoutJongsung, josaWithJongsung);
			}
		}
		return new JosaSet("","");
	}
}
