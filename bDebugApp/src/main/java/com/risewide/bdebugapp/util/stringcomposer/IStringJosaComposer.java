package com.risewide.bdebugapp.util.stringcomposer;

/**
 * Created by birdea on 2016-11-24.
 */

public interface IStringJosaComposer {

	String getSentenceWithMultiJosa(Object[] word, String formatString);
	String getSentenceWithSingleJosa(Object word, String formatString);
	String getWordWithJosa(Object word, String josaWithJongsung, String josaWithoutJongsung);
}
