package com.risewide.bdebugapp.util.stringconverter;

/**
 * Created by birdea on 2016-11-24.
 */

public interface IJosaStringConverter {

	String getSentenceWithMultiJosa(String formatString, Object... word);
	String getSentenceWithSingleJosa(String formatString, Object word, boolean applyWordOnFormatSentence);
	String getWordWithJosa(Object word, String josaWithJongsung, String josaWithoutJongsung);
}
