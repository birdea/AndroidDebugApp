package com.risewide.bdebugapp;

import com.risewide.bdebugapp.util.SLog;
import com.risewide.bdebugapp.util.TimeLap;
import com.risewide.bdebugapp.util.stringconverter.KorStringJosaConverter;
import com.risewide.bdebugapp.util.stringconverter.data.MatcherArabicToKorean;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by birdea on 2017-09-25.
 */

public class KoreanJosaStringTest {

	private KorStringJosaConverter ksc = new KorStringJosaConverter();

	//@Test
	public void test_josa_arabic() throws Exception {
		log("[test_josa_arabic] start");
		for (int i=0;i<101;i++) {
			char c = MatcherArabicToKorean.get(i).getKoreanChar();
			log("result c:"+c);
		}
		char c = MatcherArabicToKorean.get(10001).getKoreanChar();
		log("result c:"+c);
		log("[test_josa_arabic] end");
	}

	@Test
	public void test() {
		log("[testSentenceConverter] start");
		TimeLap time = new TimeLap();
		time.start();
		///////////////////////////////////////////////////////////////////////////////
		testWordCase();
		testSingleSentenceCase();
		testMultiSentenceCase();

		testDone();
		testScenarioCase();
		///////////////////////////////////////////////////////////////////////////////
		time.end();
		log("[testSentenceConverter] end");
	}

	private void log(String msg) {
		SLog.d(msg);
	}

	private String processExecuteWordJosa(Object word, String josaWith, String josaWithout) {
		String result = ksc.getWord(word, josaWith, josaWithout);
		log("-------------------------------------------------------------------");
		log("getSentenceWithMultiJosa word:" + word + ", josaWith:"+josaWith + ", josaWithout:"+josaWithout);
		log("getSentenceWithMultiJosa result:" + result);
		log("-------------------------------------------------------------------");
		return result;
	}

	private synchronized String processExecuteMultiJosa(String formatSentence, Object... words) {
		String result = ksc.getSentence(formatSentence, words);
		log("-------------------------------------------------------------------");
		log("getSentenceWithMultiJosa format:" + formatSentence + ", word: "+getReadable(words));
		log("getSentenceWithMultiJosa result:" + result);
		log("-------------------------------------------------------------------");
		return result;
	}

	private String processExecuteSingleJosa(String formatSentence, Object word) {
		String result = ksc.getSentence(formatSentence, word, true);
		log("-------------------------------------------------------------------");
		log("getSentenceWithSingleJosa format:" + formatSentence + ", word:"+word);
		log("getSentenceWithSingleJosa result:" + result);
		log("-------------------------------------------------------------------");
		return result;
	}

	private void testWordCase() {

		Object word;
		String josaWith, josaWithout;
		String result;

		// test case
		word = "박용태";
		josaWith = "이";
		josaWithout = "가";
		result = processExecuteWordJosa(word, josaWith, josaWithout);
		assertEquals("박용태가", result);

		// test case
		word = "박용택";
		josaWith = "이";
		josaWithout = "가";
		result = processExecuteWordJosa(word, josaWith, josaWithout);
		assertEquals("박용택이", result);

		// test case
		word = "박용택62";
		josaWith = "이";
		josaWithout = "가";
		result = processExecuteWordJosa(word, josaWith, josaWithout);
		assertEquals("박용택62가", result);

		// test case
		word = "박용택a";
		josaWith = "이";
		josaWithout = "가";
		result = processExecuteWordJosa(word, josaWith, josaWithout);
		assertEquals("박용택a가", result);

		// test case
		word = "박용택M";
		josaWith = "이";
		josaWithout = "가";
		result = processExecuteWordJosa(word, josaWith, josaWithout);
		assertEquals("박용택M이", result);
	}

	private void testSingleSentenceCase() {

		Object word;
		String formatSentence;
		String result;

		// test case
		formatSentence = "%s가 맞나요?";
		word = "박용태";
		result = processExecuteSingleJosa(formatSentence, word);
		assertEquals("박용태가 맞나요?", result);

		// test case
		formatSentence = "%s이 맞나요?";
		word = "박용택";
		result = processExecuteSingleJosa(formatSentence, word);
		assertEquals("박용택이 맞나요?", result);

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

		formatSentence = "<![CDATA[<skml domain=\\\"phone\">%s가 맞으면 전화연결이라고 말씀하세요.</skml>]]>";
		word = "Mr.모현호80";
		result = processExecuteSingleJosa(formatSentence, word);
		assertEquals("<![CDATA[<skml domain=\\\"phone\">Mr.모현호80이 맞으면 전화연결이라고 말씀하세요.</skml>]]>", result);

		formatSentence = "<![CDATA[<skml domain=\\\"phone\">%s가 맞으면 전화연결이라고 말씀하세요.</skml>]]>";
		word = "이대범.M";
		result = processExecuteSingleJosa(formatSentence, word);
		assertEquals("<![CDATA[<skml domain=\\\"phone\">이대범.M이 맞으면 전화연결이라고 말씀하세요.</skml>]]>", result);
	}

	private void testMultiSentenceCase() {
		///////////////////////////////////////////////////////////////////////////////
		Object[] words;
		String formatSentence;
		String result;

		// test case //
		formatSentence = "%s 볼륨을 %s로 설정합니다. 화면밝기를 %s으로 조정합니다. %s";
		//words = addWords("스피커", "3", "7", "감사합니다.");
		result = processExecuteMultiJosa(formatSentence, "스피커", "3", "7", "감사합니다.");
		assertEquals("스피커 볼륨을 3으로 설정합니다. 화면밝기를 7로 조정합니다. 감사합니다.", result);

		// test case //
		formatSentence = "볼륨을 %s로 설정합니다. 화면밝기를 %s으로 조정합니다.";
		//words = addWords("3", "7");
		result = processExecuteMultiJosa(formatSentence, "3", "7");
		assertEquals("볼륨을 3으로 설정합니다. 화면밝기를 7로 조정합니다.", result);

		formatSentence = "<![CDATA[<skml domain=\"phone\">연락처 %1$s와 %2$s가 있어요. 몇 번째 분에게 전화를 걸까요?</skml>]]>";
		//words = addWords("황승택", "김용택");
		result = processExecuteMultiJosa(formatSentence, "황승택", "김용택");
		assertEquals("<![CDATA[<skml domain=\"phone\">연락처 황승택과 김용택이 있어요. 몇 번째 분에게 전화를 걸까요?</skml>]]>", result);
	}

	private void testScenarioCase() {
		Object[] words;
		String formatSentence;
		String result;

		formatSentence = "<![CDATA[<skml domain=\\\"phone\\\">%1$s%2$s가 맞으면 전화연결이라고 말씀하세요.</skml>]]>";
		//words = addWords("황승택", "김용택");
		result = processExecuteMultiJosa(formatSentence, "조금 전 통화한 ", "김용택");
		assertEquals("<![CDATA[<skml domain=\\\"phone\\\">조금 전 통화한 김용택이 맞으면 전화연결이라고 말씀하세요.</skml>]]>", result);

		formatSentence = "<![CDATA[<skml domain=\\\"phone\\\">%s가 맞으면 전화연결이라고 말씀하세요.</skml>]]>";
		//words = addWords("황승택", "김용택");
		result = processExecuteMultiJosa(formatSentence, "김용택");
		assertEquals("<![CDATA[<skml domain=\\\"phone\\\">김용택이 맞으면 전화연결이라고 말씀하세요.</skml>]]>", result);
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
		//words = addWords("카카오톡", "카카오", "라인", "플래이");
		result = processExecuteMultiJosa(formatSentence, "카카오톡", "카카오", "라인", "플래이");
		assertEquals("카카오톡과 자료를 공유합니다. 카카오로서 활성화되었고 라인을 실행할게요, 플래이는 좋아요!", result);

		// test case
		formatSentence = "%s와 자료를 공유합니다. %s로서 활성화되었고 %s를 실행할게요, %s은 좋아요!";
		//words = addWords("런처플레닛", "SK텔레콤", "A-TF", "나3나");
		result = processExecuteMultiJosa(formatSentence, "런처플레닛", "SK텔레콤", "A-TF", "나3나");
		assertEquals("런처플레닛과 자료를 공유합니다. SK텔레콤으로서 활성화되었고 A-TF를 실행할게요, 나3나는 좋아요!", result);

		// test case
		formatSentence = "%s은 %d년 %d월 %s일 %s요일입니다. %.2f 수량";
		//words = addWords("영국", 2016, 11, 24, "수", 25, 0.343232f);
		result = processExecuteMultiJosa(formatSentence, "영국", 2016, 11, 24, "수", 0.3343f);
		assertEquals("영국은 2016년 11월 24일 수요일입니다. 0.33 수량", result);

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
	}

	/*private Object[] addWords(Object... args) {
		int size = args.length;
		Object[] wordArray = new Object[size];
		int idx = 0;
		for (Object word : args) {
			wordArray[idx++] = word;
		}
		return wordArray;
	}*/

	private String getReadable(Object[] objArray) {
		StringBuilder sb = new StringBuilder();
		for (Object item : objArray) {
			sb.append(item).append(",");
		}
		return sb.toString();
	}
}
