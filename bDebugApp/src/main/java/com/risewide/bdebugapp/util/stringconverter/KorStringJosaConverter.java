package com.risewide.bdebugapp.util.stringconverter;

import java.util.List;

import com.risewide.bdebugapp.util.SLog;
import com.risewide.bdebugapp.util.stringconverter.converter.JosaConverter;
import com.risewide.bdebugapp.util.stringconverter.converter.JosaConverterObject;
import com.risewide.bdebugapp.util.stringconverter.data.JosaSet;
import com.risewide.bdebugapp.util.stringconverter.data.KoreanJosa;

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
 * ex.1: %s을 실행합니다. -> 보이스를 실행합니다. <br>
 * ex.2: 볼륨을 %s로 설정합니다. 화면 밝기를 %s으로 조정합니다. -> 볼륨을 3으로 설정합니다. 화면 밝기를 7로 조정합니다.<br>
 * ex.3: %1$s%2$s가 맞으면 전화연결이라고 말씀하세요. -> 조금 전 통화한 박지성이 맞으면 전화연결이라고 말씀하세요.<br>
 *
 * </P>
 *
 * <p>Created by birdea on 2016-11-16.</p>
 *
 * @see <a href="http://d2.naver.com/helloworld/76650">Refer#1</a>
 * @see <a href="http://SLog.jongminkim.co.kr/?p=252">Refer#2</a>
 * @see <a href="http://okky.kr/article/33317">Refer#3</a>
 * @see <a href="http://gun0912.tistory.com/65">Refer#4</a>
 * @see <a href="http://SLog.naver.com/PostView.nhn?blogId=kkson50&logNo=120200156752&parentCategoryNo=&categoryNo=9&viewDate=&isShowPopularPosts=false&from=postView">Refer#5</a>
 */

public class KorStringJosaConverter implements IStringJosaConverter {

	private static final String TAG = "KorStringJosaConverter";

	public KorStringJosaConverter() {
	}

	/**
	 * @param words formatString's %s에 채워질 단어들, String[] words = { 와이파이, 블루투스 }<br>
	 * @param formatString %s 인자를 지니고 있는 포맷 문장 "%s는 활성화됐지만 %s가 종료되었네요." 형태의 문장<br>
	 */
	@Override
	public String getSentence(String formatString, Object... words) {
		FormatSpecifier formatSpecifier = new FormatSpecifier(formatString);
		// - get count of params
		int countWord = words.length;
		int countFormatSpecifier = formatSpecifier.getCountOfFormatSpecifier();
		log("countWord:" + countWord + ", countFormatSpecifier :" + countFormatSpecifier);
		// - check if params is invalid
		if (countFormatSpecifier < 1) {
			log("The formatSentence should has only one letter of %s or %d...");
		}
		if (countWord < countFormatSpecifier) {
			log("You have set wrong params, [format count > param count is FAIL] ");
		}
		// - param is valid, next step should be split sentence with each word by prefix_format like %s
		List<String> truncated = formatSpecifier.getTruncatedSentence();
		// - for loop to task > compose each word to one of full sentence
		StringBuilder sb = new StringBuilder();
		int length = truncated.size();
		for (int i=0; i<length; i++) {
			String aSentence = getSentence(truncated.get(i), words[i], false);
			sb.append(aSentence);
		}
		//- applyWord
		String completeSentence = sb.toString();
		log("final getSentence() --- end :" + completeSentence + ", countOfFormatSpecifier :" + countFormatSpecifier);
		return getSafeFormatString(completeSentence, words);
	}

	@Override
	public String getSentence(String formatString, Object word, boolean applyWord) {
		log("--- getSentence() --- start word:" + word + ", formatSentence:" + formatString);
		if (word == null) {
			return formatString;
		}
		FormatSpecifier formatSpecifier = new FormatSpecifier(formatString);
		int countFormatSpecifier = formatSpecifier.getCountOfFormatSpecifier();
		log("[valid] countFormatSpecifier :" + countFormatSpecifier);
		// - check if params is invalid then return formatString
		if (countFormatSpecifier != 1) {
			log("The formatSentence should has only one letter of %s or %d...");
			return formatString;
		}

		// - formatSpecifier에 <TAG>가 붙어있는 형태를 대응하기 위함 (ex. <sk_name>%s</sk_name>)
		String withEndTag = formatSpecifier.getEndTag();
		log("[tag] withEndTag:" + withEndTag);

		// - formatSpecifier와 한국어 조사가 정규적으로 구성되어 있지 않다면, 조사 처리 필요 없음
		KoreanJosa josaSet = KoreanJosa.getJosaSet(formatString, formatSpecifier.getFormatSpecifiers(), withEndTag);
		if (KoreanJosa.UNKNOWN.equals(josaSet)) {
			log("[unknown] KoreanJosa.UNKNOWN word:" + word + "/ formatSentence:" + formatString);
			if (applyWord) {
				return getSafeFormatString(formatString, word);
			} else {
				return formatString;
			}
		}
		// - formatSpecifier와 한국어 조사가 정규적으로 구성되어 있으므로, 조사 처리 진행 후 결과값 반환
		JosaConverter josaConverter = new JosaConverterObject();
		JosaSet setOfJosa = josaConverter.select(word, josaSet);
		//
		String oldWord = formatSpecifier.getFormatSpecifier() + withEndTag + setOfJosa.getUnproperJosa();
		String newWord = formatSpecifier.getFormatSpecifier() + withEndTag + setOfJosa.getProperJosa();
		log("[josa] properJosa:" + setOfJosa.getProperJosa() + ", unproperJosa:" + setOfJosa.getUnproperJosa());
		String replacedSentence = formatString.replace(oldWord, newWord);
		String completeSentence = replacedSentence;
		if (applyWord) {
			completeSentence = getSafeFormatString(replacedSentence, word);
			log("[swap] oldSentence:" + formatString + ", newSentence:" + replacedSentence);
		}
		log("final getSentence() --- end :"+completeSentence);
		return completeSentence;
	}

	@Override
	public String getWord(Object word, String josaWithJongsung, String josaWithoutJongsung) {
		JosaConverter josaConverter = new JosaConverterObject();
		KoreanJosa koreanJosa = KoreanJosa.getKoreanJosa(josaWithJongsung, josaWithoutJongsung);
		if (KoreanJosa.UNKNOWN.equals(koreanJosa)) {
			log("[unknown] KoreanJosa.UNKNOWN word:" + word);
			return String.valueOf(word);
		}
		JosaSet setOfJosa = josaConverter.select(word, koreanJosa);
		String result = word + setOfJosa.getProperJosa();
		log(getSafeFormatString("getSentence result=%s on word=%s + josa=%s", result, ""+word, setOfJosa.getProperJosa()));
		return result;
	}

	private String getSafeFormatString(String formatString, Object... values) {
		try {
			return String.format(formatString, values);
		} catch (Exception ignore) {
			ignore.printStackTrace();
			return formatString;
		}
	}

	private void log(String msg) {
		//SLog.d(TAG, msg);
		System.out.println(msg);
	}
}
