package com.risewide.bdebugapp.util.stringconverter;

/**
 * Created by birdea on 2016-11-24.
 */

public interface IStringJosaConverter {

	String getSentence(String formatString, Object... words);
	String getSentence(String formatString, Object word, boolean applyWord);
	String getWord(Object word, String josaWithJongsung, String josaWithoutJongsung);
}
