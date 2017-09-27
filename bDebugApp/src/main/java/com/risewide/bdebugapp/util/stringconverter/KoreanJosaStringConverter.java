package com.risewide.bdebugapp.util.stringconverter;

import com.risewide.bdebugapp.util.SLog;
import com.risewide.bdebugapp.util.stringconverter.converter.JosaConverter;
import com.risewide.bdebugapp.util.stringconverter.converter.JosaConverterObject;
import com.risewide.bdebugapp.util.stringconverter.data.JosaSet;
import com.risewide.bdebugapp.util.stringconverter.format.FormatSpecifier;
import com.risewide.bdebugapp.util.stringconverter.josa.KoreanJosa;

import java.util.List;

/**
 * <p>한글 종성의 받침 여부를 파악해서 조사 선택 및 문장 구성</p>
 *
 * <p>
 * [기능]<br>
 * 주어진 단어와 해당 조사에 알맞는 조사를 올바로 선택하여 반환<br>
 * </p>
 *
 * <p>
 * [예제]<br>
 * ex: 보이스을 실행합니다.(x) -> 보이스를 실행합니다.(o) <br>
 * </P>
 *
 * <p>Created by birdea on 2016-11-16.</p>
 *
 * @see <a href="http://d2.naver.com/helloworld/76650">Refer#1</a>
 * @see <a href="http://blog.jongminkim.co.kr/?p=252">Refer#2</a>
 * @see <a href="http://okky.kr/article/33317">Refer#3</a>
 * @see <a href="http://gun0912.tistory.com/65">Refer#4</a>
 * @see <a href="http://blog.naver.com/PostView.nhn?blogId=kkson50&logNo=120200156752&parentCategoryNo=&categoryNo=9&viewDate=&isShowPopularPosts=false&from=postView">Refer#5</a>
 */

public class KoreanJosaStringConverter implements IJosaStringConverter {

	private static final String TAG = "KoreanJosaStringConverter";

	public KoreanJosaStringConverter() {
	}

	/**
	 * @param words formatString's %s에 채워질 단어들, String[] words = { 와이파이, 블루투스 }<br>
	 * @param formatString %s 인자를 지니고 있는 포맷 문장 "%s는 활성화됐지만 %s가 종료되었네요." 형태의 문장<br>
	 */
	@Override
	public String getSentenceWithMultiJosa(String formatString, Object... words) {
		FormatSpecifier formatSpecifier = new FormatSpecifier();
		// - init
		if (!formatSpecifier.parse(formatString)) {
			return formatString;
		}
		// - get count of params
		int countOfWord = words.length;
		int countOfFormatSpecifier = formatSpecifier.getCountOfFormatSpecifier();
		Log("countOfWord:" + countOfWord + ", countOfFormatSpecifier :" + countOfFormatSpecifier);
		// - check if params is invalid
		if (countOfFormatSpecifier < 1) {
			Log("The formatSentence should has only one letter of %s or %d...");
		}
		if (countOfWord < countOfFormatSpecifier) {
			Log("You have set wrong params, [format count > param count is FAIL] ");
		}
		// - param is valid, next step should be split sentence with each word by prefix_format like %s
		List<String> truncated = formatSpecifier.getTruncatedSentence();
		// - for loop to task > compose each word to one of full sentence
		StringBuilder sb = new StringBuilder();
		int length = truncated.size();
		for (int i=0; i<length; i++) {
			String aWord = getSentenceWithSingleJosa(truncated.get(i), words[i], false);
			sb.append(aWord);
		}
		//- applyWord
		String completeSentence = sb.toString();
		Log("final getSentenceWithMultiJosa() --- end :" + completeSentence + ", countOfFormatSpecifier :" + countOfFormatSpecifier);
		return getSafeFormatString(completeSentence, words);
	}

	@Override
	public String getSentenceWithSingleJosa(String formatString, Object word, boolean applyWordOnFormatSentence) {
		Log("--- getSentenceWithSingleJosa() --- start word:" + word + ", formatSentence:" + formatString);
		if (word == null) {
			return formatString;
		}
		FormatSpecifier formatSpecifier = new FormatSpecifier();
		// - init
		if (!formatSpecifier.parse(formatString)) {
			return formatString;
		}
		int countOfFormatSpecifier = formatSpecifier.getCountOfFormatSpecifier();
		Log("[valid] countOfFormatSpecifier :" + countOfFormatSpecifier);
		// - check if params is invalid then return formatString
		if (countOfFormatSpecifier != 1) {
			Log("The formatSentence should has only one letter of %s or %d...");
			return formatString;
		}

		// - formatSpecifier와 한국어 조사가 정규적으로 구성되어 있지 않다면, 조사 처리 필요 없음
		KoreanJosa josaSet = KoreanJosa.getJosaSet(formatString, formatSpecifier.getFormatSpecifiers());
		if (KoreanJosa.UNKNOWN.equals(josaSet)) {
			Log("[unknown] KoreanJosa.UNKNOWN word:" + word + "/ formatSentence:" + formatString);
			if (applyWordOnFormatSentence) {
				return getSafeFormatString(formatString, word);
			} else {
				return formatString;
			}
		}
		// - formatSpecifier와 한국어 조사가 정규적으로 구성되어 있으므로, 조사 처리 진행 후 결과값 반환
		JosaConverter josaConverter = new JosaConverterObject();
		JosaSet setOfJosa = josaConverter.select(word, josaSet);
		//
		String oldWord = formatSpecifier.getFormatSpecifier() + setOfJosa.getUnproperJosa();
		String newWord = formatSpecifier.getFormatSpecifier() + setOfJosa.getProperJosa();
		Log("[josa] properJosa:" + setOfJosa.getProperJosa() + ", unproperJosa:" + setOfJosa.getUnproperJosa());
		String replacedSentence = formatString.replace(oldWord, newWord);
		String completeSentence = replacedSentence;
		if (applyWordOnFormatSentence) {
			completeSentence = getSafeFormatString(replacedSentence, word);
			Log("[swap] oldSentence:" + formatString + ", newSentence:" + replacedSentence);
		}
		Log("final getSentenceWithSingleJosa() --- end :"+completeSentence);
		return completeSentence;
	}

	@Override
	public String getWordWithJosa(Object word, String josaWithJongsung, String josaWithoutJongsung) {
		JosaConverter josaConverter = new JosaConverterObject();
		KoreanJosa koreanJosa = KoreanJosa.getKoreanJosa(josaWithJongsung, josaWithoutJongsung);
		if (KoreanJosa.UNKNOWN.equals(koreanJosa)) {
			Log("[unknown] KoreanJosa.UNKNOWN word:" + word);
			return String.valueOf(word);
		}
		JosaSet setOfJosa = josaConverter.select(word, koreanJosa);
		String result = word + setOfJosa.getProperJosa();
		Log(getSafeFormatString("getSentenceWithSingleJosa result=%s on word=%s + josa=%s", new String[] { result, ""+word, setOfJosa.getProperJosa()}));
		return result;
	}

	private String getSafeFormatString(String formatString, Object... values) {
		//try {
			return String.format(formatString, values);
		//} catch (Exception ignore) {
		//	ignore.printStackTrace();
		//	return formatString;
		//}
	}

	private void Log(String msg) {
		SLog.d(TAG, msg);
	}
}
