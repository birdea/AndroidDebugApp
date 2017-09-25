package com.risewide.bdebugapp.util.stringcomposer;

/**
 * Created by birdea on 2016-11-24.
 */

public interface IStringComposer {

	String getSingleSentenceWithJosa(Object word, String formedSentence);
	String getMultiSentenceWithJosa(Object[] words, String formedSentence);
	String getSentenceWithJosa(Object word, String josaWithJongsung, String josaWithoutJongsung);
}
