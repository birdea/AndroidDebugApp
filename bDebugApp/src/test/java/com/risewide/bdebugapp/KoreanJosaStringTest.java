package com.risewide.bdebugapp;

import com.risewide.bdebugapp.util.SLog;
import com.risewide.bdebugapp.util.TimeLap;
import com.risewide.bdebugapp.util.stringconverter.KoreanJosaStringConverter;
import com.risewide.bdebugapp.util.stringconverter.spec.MatcherArabicToKorean;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by birdea on 2017-09-25.
 */

public class KoreanJosaStringTest {

	private KoreanJosaStringConverter ksc = new KoreanJosaStringConverter();

	//@Test
	public void test_josa_arabic() throws Exception {
		Log("[test_josa_arabic] start");
		for (int i=0;i<101;i++) {
			char c = MatcherArabicToKorean.get(i).getKoreanChar();
			Log("result c:"+c);
		}
		char c = MatcherArabicToKorean.get(10001).getKoreanChar();
		Log("result c:"+c);
		Log("[test_josa_arabic] end");
	}

	private void Log(String msg) {
		SLog.d(msg);
	}

	@Test
	public void test() {
		SLog.i("[testSentenceConverter] start");
		TimeLap time = new TimeLap();
		time.start();
		///////////////////////////////////////////////////////////////////////////////
		//testDone();
		testSuccessCase();
		///////////////////////////////////////////////////////////////////////////////
		time.end();
		SLog.i("[testSentenceConverter] end");
	}

	private String processExecuteMultiJosa(String formatSentence, Object[] words) {
		String result = ksc.getSentenceWithMultiJosa(words, formatSentence);
		SLog.i("-------------------------------------------------------------------");
		SLog.i("getSentenceWithMultiJosa format:" + formatSentence + ", word:"+words);
		SLog.i("getSentenceWithMultiJosa result:" + result);
		SLog.i("-------------------------------------------------------------------");
		return result;
	}

	private String processExecuteSingleJosa(String formatSentence, Object word) {
		String result = ksc.getSentenceWithSingleJosa(word, formatSentence);
		SLog.i("-------------------------------------------------------------------");
		SLog.i("getSentenceWithSingleJosa format:" + formatSentence + ", word:"+word);
		SLog.i("getSentenceWithSingleJosa result:" + result);
		SLog.i("-------------------------------------------------------------------");
		return result;
	}

	private void testSuccessCase() {
		///////////////////////////////////////////////////////////////////////////////
		Object word;
		Object[] words;
		String formatSentence;
		String result;

		// test case //
		formatSentence = "볼륨을 %s로 설정합니다. 화면밝기를 %s으로 조정합니다.";
		words = addWords("3", "7");
		result = processExecuteMultiJosa(formatSentence, words);
		assertEquals("볼륨을 3으로 설정합니다. 화면밝기를 7로 조정합니다.", result);

		// test case
		formatSentence = "%s가 맞나요?";
		word = "박용태";
		result = processExecuteSingleJosa(formatSentence, word);
		assertEquals("박용태가 맞나요?", result);

		// test case
		formatSentence = "%s이 맞나요?";
		word = "박용태";
		result = processExecuteSingleJosa(formatSentence, word);
		assertEquals("박용태가 맞나요?", result);

		// test case
		formatSentence = "%s가 맞나요?";
		word = "abcdefghl";
		result = processExecuteSingleJosa(formatSentence, word);
		assertEquals("abcdefghl이 맞나요?", result);

		formatSentence = "%s이 맞나요?";
		word = null; // null
		result = processExecuteSingleJosa(formatSentence, word);
		assertEquals("%s이 맞나요?", result);

		formatSentence = "%s이 맞나요?";
		word = 0; // 0
		result = processExecuteSingleJosa(formatSentence, word);
		assertEquals("0이 맞나요?", result);

		formatSentence = "%s이 맞나요?";
		word = 10000; // 만
		result = processExecuteSingleJosa(formatSentence, word);
		assertEquals("10000이 맞나요?", result);

		formatSentence = "%s이 맞나요?";
		word = 10001; // 만
		result = processExecuteSingleJosa(formatSentence, word);
		assertEquals("10001이 맞나요?", result);

		formatSentence = "%s이 맞나요?";
		word = 100000; // 십만
		result = processExecuteSingleJosa(formatSentence, word);
		assertEquals("100000이 맞나요?", result);

		formatSentence = "%s이 맞나요?";
		word = 10000000; // 천만
		result = processExecuteSingleJosa(formatSentence, word);
		assertEquals("10000000이 맞나요?", result);

		formatSentence = "%s이 맞나요?";
		word = 100000000; // 억
		result = processExecuteSingleJosa(formatSentence, word);
		assertEquals("100000000이 맞나요?", result);

		formatSentence = "%s이 맞나요?";
		word = 100000000000000L; // 조
		result = processExecuteSingleJosa(formatSentence, word);
		assertEquals("100000000000000가 맞나요?", result);

		formatSentence = "<![CDATA[<skml domain=\\\"phone\">%s가 맞으면 전화연결이라고 말씀하세요.</skml>]]>";
		word = "황승택o";
		result = processExecuteSingleJosa(formatSentence, word);
		assertEquals("<![CDATA[<skml domain=\\\"phone\">황승택o가 맞으면 전화연결이라고 말씀하세요.</skml>]]>", result);

		formatSentence = "<![CDATA[<skml domain=\"phone\">연락처 %1$s와 %2$s가 있어요. 몇 번째 분에게 전화를 걸까요?</skml>]]>";
		words = addWords("황승택", "김용택");
		result = processExecuteMultiJosa(formatSentence, words);
		assertEquals("<![CDATA[<skml domain=\"phone\">연락처 황승택과 김용택이 있어요. 몇 번째 분에게 전화를 걸까요?</skml>]]>", result);
	}

	long pow(long a, int b) {
		if (b == 0)
			return 1;
		if (b == 1)
			return a;
		if (b % 2 == 0)
			return pow(a * a, b / 2); // even a=(a^2)^b/2
		else
			return a * pow(a * a, b / 2); // odd a=a*(a^2)^b/2
	}

	private void testFailCase() {
	}

	private void testDone() {
		///////////////////////////////////////////////////////////////////////////////
		Object word;
		Object[] words;
		String formatSentence;
		String result;

		// test case
		formatSentence = "%s을 실행합니다.";
		word = "카카오";
		result = processExecuteSingleJosa(formatSentence, word);
		assertEquals("카카오를 실행합니다.", result);

		// test case
		formatSentence = "%s와 자료를 공유합니다. %s로서 활성화되었고 %s를 실행할게요, %s은 좋아요!";
		words = addWords("카카오톡", "카카오", "라인", "플래이");
		result = processExecuteMultiJosa(formatSentence, words);
		assertEquals("카카오톡과 자료를 공유합니다. 카카오로서 활성화되었고 라인을 실행할게요, 플래이는 좋아요!", result);

		// test case
		formatSentence = "%s와 자료를 공유합니다. %s로서 활성화되었고 %s를 실행할게요, %s은 좋아요!";
		words = addWords("런처플레닛", "SK텔레콤", "A-TF", "나3나");
		result = processExecuteMultiJosa(formatSentence, words);
		assertEquals("런처플레닛과 자료를 공유합니다. SK텔레콤으로서 활성화되었고 A-TF를 실행할게요, 나3나는 좋아요!", result);

		// test case
		formatSentence = "%s은 %s년 %s월 %s일 %s요일입니다.";
		words = addWords("영국", 2016, 11, 24, "수", 25);
		result = processExecuteMultiJosa(formatSentence, words);
		assertEquals("영국은 2016년 11월 24일 수요일입니다.", result);

		char idx_a = 'a';
		char idx_z = 'z';
		// test case - 소문자[a-z]
		for (char i=idx_a;i<=idx_z;i++) {
			formatSentence = "%s이 맞나요?";
			word = String.valueOf(i);
			result = processExecuteSingleJosa(formatSentence, word);
			//assertEquals("", result);
		}

		char idx_A = 'A';
		char idx_Z = 'Z';
		// test case - 대문자[A-Z]
		for (char i=idx_A;i<=idx_Z;i++) {
			formatSentence = "%s이 맞나요?";
			word = String.valueOf(i);
			result = processExecuteSingleJosa(formatSentence, word);
			//assertEquals("", result);
		}
		// test case - digit
		for(int i=0;i<20;i++) {
			formatSentence = "%s가 맞나요?";
			word = String.valueOf(i);
			result = processExecuteSingleJosa(formatSentence, word);
			//assertEquals("", result);
		}
		// test case - digit
		for (int i = 0; i < 120; i++) {
			formatSentence = "%s가 맞나요?";
			word = String.valueOf(i);
			result = processExecuteSingleJosa(formatSentence, word);
			//assertEquals("", result);
		}
	}

	private Object[] addWords(Object... args) {
		int size = args.length;
		Object[] wordArray = new Object[size];
		int idx = 0;
		for (Object word : args) {
			wordArray[idx++] = word;
		}
		return wordArray;
	}
}
