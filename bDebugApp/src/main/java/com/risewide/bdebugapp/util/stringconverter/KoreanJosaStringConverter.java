package com.risewide.bdebugapp.util.stringconverter;

import com.risewide.bdebugapp.util.SLog;
import com.risewide.bdebugapp.util.stringconverter.converter.JosaConverter;
import com.risewide.bdebugapp.util.stringconverter.data.JosaSet;
import com.risewide.bdebugapp.util.stringconverter.format.FormatSpecifier;
import com.risewide.bdebugapp.util.stringconverter.josa.KoreanJosa;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	private static final String EMPTY = "";

	public KoreanJosaStringConverter() {
	}

	/**
	 * @param words formatString's %s에 채워질 단어들, String[] words = { 와이파이, 블루투스 }<br>
	 * @param formatString %s 인자를 지니고 있는 포맷 문장 "%s는 활성화됐지만 %s가 종료되었네요." 형태의 문장<br>
	 */
	@Override
	public String getSentenceWithMultiJosa(Object[] words, String formatString) {
		// - get count of params
		int countOfWord = words.length;
		int countOfFormatSpecifier = 0;
		for(FormatSpecifier formatSpecifier : FormatSpecifier.values()) {
			countOfFormatSpecifier += getSumOfAppearance(formatSpecifier.getFormat(), formatString);
		}
		Log("countOfWord:" + countOfWord + ", countOfFormatSpecifier :" + countOfFormatSpecifier);
		// - check if params is invalid
		if (countOfFormatSpecifier < 1) {
			Log("The formatSentence should has only one letter of %s or %d...");
		}
		if (countOfWord < countOfFormatSpecifier) {
			Log("You have set wrong params, [format count > param count is FAIL] ");
		}
		// - param is valid, next step should be split sentence with each word by prefix_format like %s
		String[] truncatedWords = getTruncatedSentence(formatString, FormatSpecifier.getRegularExpression());
		// - for loop to task > compose each word to one of full sentence
		int length = truncatedWords.length;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			String aWord = getSentenceWithSingleJosa(getSafeArrayString(words, i), String.valueOf(getSafeArrayString(truncatedWords, i)));
			sb.append(aWord);
		}
		return sb.toString();
	}

	@Override
	public String getSentenceWithSingleJosa(Object word, String formatString) {
		Log("[start] word:" + word + ", formatSentence:" + formatString);
		FormatSpecifier formatSpecifier = FormatSpecifier.getProperType(word);
		if (formatSpecifier == null) {
			return formatString;
		}
		int countOfFormatSpecifier = getSumOfAppearance(formatSpecifier.getFormat(), formatString);
		Log("[valid] countOfFormatSpecifier :" + countOfFormatSpecifier);
		// - check if params is invalid then return formatString
		if (countOfFormatSpecifier != 1) {
			formatSpecifier = FormatSpecifier._s;
			countOfFormatSpecifier = getSumOfAppearance(formatSpecifier.getFormat(), formatString);
			if (countOfFormatSpecifier != 1) {
				SLog.w(TAG, "The formatSentence should has only one letter of %s or %d...");
				return formatString;
			}
		}
		Log("[valid] selected FormatSpecifier :" + formatSpecifier);

		KoreanJosa josaSet = KoreanJosa.getJosaSet(formatString);
		if (KoreanJosa.UNKNOWN.equals(josaSet)) {
			SLog.w(TAG, "[unknown] KoreanJosa.UNKNOWN word:" + word + "/ formatSentence:" + formatString);
			return getSafeFormatString(formatString, word);
		}
		JosaConverter josaConverter = formatSpecifier.getConverter();
		JosaSet setOfJosa = josaConverter.select(word, josaSet);
		//
		String oldWord = formatSpecifier.getFormat() + setOfJosa.getUnproperJosa();
		String newWord = formatSpecifier.getFormat() + setOfJosa.getProperJosa();
		Log("[josa] properJosa:" + setOfJosa.getProperJosa() + ", unproperJosa:" + setOfJosa.getUnproperJosa());
		String replacedSentence = formatString.replaceFirst(oldWord, newWord);
		String completeSentence = getSafeFormatString(replacedSentence, word);
		Log("[wwap] oldSentence:" + formatString + ", newSentence:" + replacedSentence);
		Log("[result] completeSentence:" + completeSentence);
		return completeSentence;
	}

	@Override
	public String getWordWithJosa(Object word, String josaWithJongsung, String josaWithoutJongsung) {
		FormatSpecifier formatSpecifier = FormatSpecifier.getProperType(word);
		if (formatSpecifier == null) {
			return String.valueOf(word);
		}
		JosaConverter josaConverter = formatSpecifier.getConverter();
		KoreanJosa koreanJosa = KoreanJosa.getKoreanJosa(josaWithJongsung, josaWithoutJongsung);
		if (KoreanJosa.UNKNOWN.equals(koreanJosa)) {
			SLog.w(TAG, "[unknown] KoreanJosa.UNKNOWN word:" + word);
			return String.valueOf(word);
		}
		JosaSet setOfJosa = josaConverter.select(word, koreanJosa);
		String result = word + setOfJosa.getProperJosa();
		Log(getSafeFormatString("getSentenceWithSingleJosa result=%s on word=%s + josa=%s", new String[] { result, ""+word, setOfJosa.getProperJosa()}));
		return result;
	}

	private int getSumOfAppearance(String word, String sentence) {
		int sum = 0;
		int lengthWord = word.length();
		int lengthSentence = sentence.length();
		String truncatedWord;
		for (int i = 0; i < sentence.length() - lengthWord + 1; i++) {
			if (i + lengthWord > lengthSentence) {
				truncatedWord = sentence.substring(i);
			} else {
				truncatedWord = sentence.substring(i, i + lengthWord);
			}
			if (word.equals(truncatedWord)) {
				sum++;
			}
		}
		return sum;
	}

	private String getSafeFormatString(String formatString, Object value) {
		return getSafeFormatString(formatString, new Object[]{ value });
	}

	private String getSafeFormatString(String formatString, Object[] values) {
		try {
			return String.format(formatString, values);
		} catch (Exception ignore) {
			ignore.printStackTrace();
			return formatString;
		}
	}

	private Object getSafeArrayString(Object[] array, int index) {
		try {
			return array[index];
		} catch (ArrayIndexOutOfBoundsException ignore) {
			return EMPTY;
		}
	}

	private String[] getTruncatedSentence(String sentence, String reg) {
		//
		List<String> list = new ArrayList<>();
		Log("printPatternMatch-start:"+sentence+", reg:"+reg);
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(sentence);
		int idxStart = 0, idxEnd = 0, idxBase = 0;
		boolean firstMatch = false;
		String subSentence;
		while(m.find()) {
			idxStart = m.start();
			idxEnd = m.end();
			String group = m.group();
			if (firstMatch) {
				subSentence = sentence.substring(idxBase, idxStart);
				Log("idxStart:"+idxStart + ", idxEnd:"+ idxEnd +", group:"+group + ", groupCount:"+ m.groupCount() + ", subSentence:"+subSentence);
				idxBase = idxStart;
				list.add(subSentence);
			}
			else {
				firstMatch = true;
			}
		}
		//
		subSentence = sentence.substring(idxBase);
		list.add(subSentence);
		Log("[last] idxStart:"+idxStart + ", idxEnd:"+ idxEnd + ", subSentence:"+subSentence);
		// print out for debug
		for (String text : list) {
			Log("[result-getTruncatedSentence] text:" + text);
		}
		Log("printPatternMatch-end");
		return list.toArray(new String[0]);
	}

	private void Log(String msg) {
		SLog.d(TAG, msg);
	}
}
