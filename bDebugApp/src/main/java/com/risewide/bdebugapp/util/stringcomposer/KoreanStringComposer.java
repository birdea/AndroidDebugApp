package com.risewide.bdebugapp.util.stringcomposer;

import com.risewide.bdebugapp.util.SLog;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 한글 마지막글자의 종성을 파악해서 뒤에 붙일 글자를 처리한다.
 *
 * Created by birdea on 2016-11-16.
 *
 * @link http://d2.naver.com/helloworld/76650
 * @link http://blog.jongminkim.co.kr/?p=252
 * http://okky.kr/article/33317
 * http://gun0912.tistory.com/65
 * http://blog.naver.com/PostView.nhn?blogId=kkson50&logNo=120200156752&parentCategoryNo=&categoryNo=9&viewDate=&isShowPopularPosts=false&from=postView
 */

public class KoreanStringComposer implements IStringComposer{

	private static final String TAG = "KoreanStringComposer";
	private static final String EMPTY = "";

	public KoreanStringComposer() {
	}

	/**
	 * @param words "%s을 실행합니다" 형태의 문장에 들어갈 String[] value<br> ex: 보이스<br>
	 * @param formedSentence strings_tts.xml 파일상 value 값으로 정의된 문장<br> "%s은 xx입니다" 또는 "%s는 활성화됐지만 %s가 종료되었네요." 형태로 정의되어 있어야 한다.<br>
	 * @return %s에 알맞는 조사로 변경된 문장을 반환<br> ex: 보이스을 실행합니다.(x) -> 보이스를 실행합니다.(o) <br>
	 */
	@Override
	public String getMultiSentenceWithJosa(Object[] words, String formedSentence) {
		// - get count of params
		int countOfWord = words.length;
		int countOfRequiredWord = 0;
		for(FormatSpecifier formatSpecifier : FormatSpecifier.values()) {
			countOfRequiredWord += getSumOfAppearance(formatSpecifier.format, formedSentence);
		}
		SLog.d(TAG, "countOfWord:" + countOfWord + ", countOfRequiredWord :" + countOfRequiredWord);
		// - check if params is invalid
		if (countOfRequiredWord < 1) {
			SLog.w(TAG, "The formedSentence should has only one letter of %s or %d...");
		}
		if (countOfWord < countOfRequiredWord) {
			//throw new IllegalArgumentException("You have set wrong params, each count of param should be equal");
			SLog.w(TAG, "You have set wrong params, [format count > param count is FAIL] ");
		}
		// - param is valid, next step should be split sentence with each word by prefix_format like %s
		String[] truncatedWords = getTruncatedSentence(formedSentence, FormatSpecifier.getRegularExpression());
		// - for loop to task > compose each word to one of full sentence
		int length = truncatedWords.length;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			String aWord = getSingleSentenceWithJosa(getSafeArrayString(words, i), String.valueOf(getSafeArrayString(truncatedWords, i)));
			sb.append(aWord);
		}
		return sb.toString();
	}

	@Override
	public String getSingleSentenceWithJosa(Object word, String formedSentence) {
		FormatSpecifier formatSpecifier = FormatSpecifier.getProperType(word);
		int countOfPrefixFormat = getSumOfAppearance(formatSpecifier.format, formedSentence);
		SLog.d(TAG, "countOfPrefixFormat :" + countOfPrefixFormat);
		// - check if params is invalid
		if (countOfPrefixFormat != 1) {
			//throw new IllegalArgumentException("The formedSentence should has only one letter of %s or %d...");
			SLog.w(TAG, "The formedSentence should has only one letter of %s or %d...");
		}
		CandidateJosa josaSet = CandidateJosa.getJosaSet(formedSentence);
		if (CandidateJosa.UNKNOWN.equals(josaSet)) {
			SLog.w(TAG, "[unknown] CandidateJosa.UNKNOWN word:" + word + "/ formedSentence:" + formedSentence);
			return getSafeFormatString(formedSentence, word);
		}
		SLog.d(TAG, "[TASK] word:" + word + ", formedSentence:" + formedSentence);
		JosaComposer josaComposer = formatSpecifier.getComposer();
		JosaSet setOfJosa = josaComposer.select(word, josaSet.josaWithJongsung, josaSet.josaWithoutJongsung);//getSelectedJosa(word, josaSet.josaWithJongsung, josaSet.josaWithoutJongsung);
		//
		String oldWord = formatSpecifier.format + setOfJosa.unproperJosa;
		String newWord = formatSpecifier.format + setOfJosa.properJosa;
		SLog.d(TAG, "[JOSA] properJosa:" + setOfJosa.properJosa + ", unproperJosa:" + setOfJosa.unproperJosa);
		String replacedSentence = formedSentence.replaceFirst(oldWord, newWord);
		String completeSentence = getSafeFormatString(replacedSentence, word);
		SLog.d(TAG, "[Swap] oldSentence:" + formedSentence + ", newSentence:" + replacedSentence);
		SLog.d(TAG, "[Single] completeSentence:" + completeSentence);
		return completeSentence;
	}

	@Override
	public String getSentenceWithJosa(Object word, String josaWithJongsung, String josaWithoutJongsung) {
		FormatSpecifier formatSpecifier = FormatSpecifier.getProperType(word);
		if(formatSpecifier == null) {
			return EMPTY;
		}
		JosaComposer josaComposer = formatSpecifier.getComposer();
		JosaSet setOfJosa = josaComposer.select(word, josaWithJongsung, josaWithoutJongsung);
		String result = word + setOfJosa.properJosa;
		SLog.d(getSafeFormatString("getSentenceWithJosa result=%s on word=%s + josa=%s", new String[] { result, ""+word, setOfJosa.properJosa}));
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

	private String[] getTruncatedSentence(String sentence, String reg) {
		//
		List<String> list = new ArrayList<>();
		SLog.d(TAG, "printPatternMatch-start:"+sentence+", reg:"+reg);
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(sentence);
		int idxStart = 0, idxEnd = 0, idxBase = 0;
		boolean firstMatch = false;
		String subSentence;
		while(m.find()) {
			idxStart = m.start();
			idxEnd = m.end();
			String group = m.group();
			//if(idxBase < idxStart)
			if(firstMatch) {
				subSentence = sentence.substring(idxBase, idxStart);
				SLog.d(TAG, "idxStart:"+idxStart + ", idxEnd:"+ idxEnd +", group:"+group + ", groupCount:"+ m.groupCount() + ", subSentence:"+subSentence);
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
		SLog.d(TAG, "[last] idxStart:"+idxStart + ", idxEnd:"+ idxEnd + ", subSentence:"+subSentence);
		// print out for debug
		for (String text : list) {
			SLog.d(TAG, "[result-getTruncatedSentence] text:" + text);
		}
		SLog.d(TAG, "printPatternMatch-end");
		return list.toArray(new String[0]);
	}

	private String getSafeFormatString(String format, Object value) {
		return getSafeFormatString(format, new Object[]{ value });
	}

	private String getSafeFormatString(String formattedSentence, Object[] values) {
		String sentence;
		try {
			sentence = String.format(formattedSentence, values);
		} catch (Exception ignore) {
			ignore.printStackTrace();
			sentence = formattedSentence;
		}
		return sentence;
	}

	private Object getSafeArrayString(Object[] array, int index) {
		try {
			return array[index];
		} catch (ArrayIndexOutOfBoundsException ignore) {
			return EMPTY;
		}
	}
}
