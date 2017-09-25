package com.risewide.bdebugapp.util.stringcomposer;

import com.risewide.bdebugapp.util.SLog;
import com.risewide.bdebugapp.util.TimeLap;

/**
 * Created by birdea on 2017-09-25.
 */

public class KoreanStringJosaComposerTest {

	private KoreanStringJosaComposer ksc;

	public KoreanStringJosaComposerTest() {
		ksc = new KoreanStringJosaComposer();
	}

	public void test() {
		SLog.i("[testSentenceComposer] start");
		TimeLap time = new TimeLap();
		time.start();
		///////////////////////////////////////////////////////////////////////////////
		//testDone();
		testSuccessCase();
		//testFailCase();
		///////////////////////////////////////////////////////////////////////////////
		time.end();
		SLog.i("[testSentenceComposer] end");
	}

	private void testSuccessCase() {
		///////////////////////////////////////////////////////////////////////////////
		Object[] words;
		String formatSentence;
		String result;

		// test case /////////////////////////////////////////////////////////////////////////
		formatSentence = "볼륨을 %s로 설정합니다. 화면밝기를 %s으로 조정합니다.";
		words = addWords(
				"19",
				"2"
		);
		result = ksc.getSentenceWithMultiJosa(words, formatSentence);
		SLog.i("getSentenceWithMultiJosa format:"+formatSentence);
		SLog.i("getSentenceWithMultiJosa result:"+result);
		SLog.i("-------------------------------------------------------------------");

		// test case /////////////////////////////////////////////////////////////////////////
		formatSentence = "%s은 %s년 %s월 %d일 %s요일입니다.";
		words = addWords(
				"영국",
				2016,
				11,
				24,
				"수",
				25
		);
		result = ksc.getSentenceWithMultiJosa(words, formatSentence);
		SLog.i("getSentenceWithMultiJosa format:"+formatSentence);
		SLog.i("getSentenceWithMultiJosa result:"+result);
		SLog.i("-------------------------------------------------------------------");
	}

	private void testFailCase() {}

	private void testDone() {
		///////////////////////////////////////////////////////////////////////////////
		Object word;
		Object[] words;
		String formatSentence;
		String result;

		// test case /////////////////////////////////////////////////////////////////////////
		formatSentence = "%s을 실행합니다.";
		word = "카카오";
		result = ksc.getSentenceWithSingleJosa(word, formatSentence);
		SLog.i("getSentenceWithSingleJosa format:"+formatSentence);
		SLog.i("getSentenceWithSingleJosa result:"+result);
		SLog.i("-------------------------------------------------------------------");

		// test case /////////////////////////////////////////////////////////////////////////
		formatSentence = "%s와 자료를 공유합니다. %s로서 활성화되었고 %s를 실행할게요, %s은 좋아요!";
		words = addWords(
				"카카오톡",
				"카카오",
				"라인",
				"플래이"
		);
		result = ksc.getSentenceWithMultiJosa(words, formatSentence);
		SLog.i("getSentenceWithMultiJosa format:"+formatSentence);
		SLog.i("getSentenceWithMultiJosa result:"+result);
		SLog.i("-------------------------------------------------------------------");

		// test case /////////////////////////////////////////////////////////////////////////
		formatSentence = "%s와 자료를 공유합니다. %s로서 활성화되었고 %s를 실행할게요, %s은 좋아요!";
		words = addWords(
				"런처플레닛",
				"SK텔레콤",
				"A-TF",
				"나3나"
		);
		result = ksc.getSentenceWithMultiJosa(words, formatSentence);
		SLog.i("getSentenceWithMultiJosa format:"+formatSentence);
		SLog.i("getSentenceWithMultiJosa result:"+result);
		SLog.i("-------------------------------------------------------------------");
	}


	private Object[] addWords(Object... args) {
		int size = args.length;
		Object[] wordArray = new Object[size];
		int idx = 0;
		for(Object word : args) {
			wordArray[idx++] = word;
		}
		return wordArray;
	}
}

