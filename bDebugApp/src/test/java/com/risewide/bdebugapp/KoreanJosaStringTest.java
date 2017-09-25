package com.risewide.bdebugapp;

import com.risewide.bdebugapp.util.SLog;
import com.risewide.bdebugapp.util.TimeLap;
import com.risewide.bdebugapp.util.stringcomposer.KoreanStringJosaComposer;

import org.junit.Test;

/**
 * Created by birdea on 2017-09-25.
 */

public class KoreanJosaStringTest {

	@Test
	public void test_josa() throws Exception{
		SLog.i("[testSentenceComposer] start");
		TimeLap time = new TimeLap();
		time.start();
		KoreanStringJosaComposer ksc = new KoreanStringJosaComposer();
		String result;
		// case.1 - getSentenceWithSingleJosa
		result = ksc.getSentenceWithSingleJosa("카카오", "%s를 실행합니다.");
		time.mid();
		SLog.i("getSentenceWithSingleJosa result:"+result+", 카카오"+", %s를 실행합니다.");
		// case.2 - getSentenceWithMultiJosa
		String[] words_1 = {
				"카카오톡",
				"카카오",
				"라인",
				"플래이"
		};
		String formatSentence = "%s와 자료를 공유합니다. %s로서 활성화되었고 %s를 실행할게요, %s은 좋아요!";
		result = ksc.getSentenceWithMultiJosa(words_1, formatSentence);
		SLog.i("getSentenceWithMultiJosa result:"+result+", words_1"+", "+formatSentence);
		time.mid();
		// case.3 - getSentenceWithMultiJosa
		String[] words_2 = {
				"런처플레닛",
				"SK텔레콤",
				"A-TF",
				"나3나"
		};
		formatSentence = "%s와 자료를 공유합니다. %s로서 활성화되었고 %s를 실행할게요, %s은 좋아요!";
		time.mid();
		result = ksc.getSentenceWithMultiJosa(words_2, formatSentence);
		SLog.i("getSentenceWithMultiJosa result:"+result+", words_2"+", "+formatSentence);
		time.mid();
		//- test case support decimal
		formatSentence = "볼륨을 %d로 설정합니다. 화면밝기를 %d으로 조정합니다.";
		Object[] words_3 = {
				19,
				2,
		};
		result = ksc.getSentenceWithMultiJosa(words_3, formatSentence);
		SLog.d("getSentenceWithMultiJosa result:"+result+", words_3, "+formatSentence);

		Object[] params = {
				"영국",
				2016,
				11,
				24,
				"수",
				25
		};
		String sentence = "%s은 %s년 %s월 %d일 %s요일입니다.";
		//StringComposerTest.taskMultiWordsWith(params, sentence, "영국은 2016년 11월 24일 수요일입니다.");
		result = ksc.getSentenceWithMultiJosa(params, sentence);
		SLog.d("getSentenceWithMultiJosa result:"+result+", words_3, "+formatSentence);
		//
		time.end();
		SLog.i("[testSentenceComposer] end");
	}

}
