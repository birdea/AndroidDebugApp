package com.risewide.bdebugapp.util.stringconverter;

/**
 * Created by birdea on 2016-11-24.
 */

public interface IJosaStringConverter {

	String getSentenceWithMultiJosa(Object[] word, String formatString);
	String getSentenceWithSingleJosa(Object word, String formatString);
	String getWordWithJosa(Object word, String josaWithJongsung, String josaWithoutJongsung);
}
