package com.risewide.bdebugapp;

import com.risewide.bdebugapp.util.SLog;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by birdea on 2017-09-26.
 */

public class FindStringFormatter {

	private static final String TAG = "FindStringFormatter";

	@Test
	public void test() throws Exception {
		String sentence;
		String[] result;

		// test case
		sentence = "<![CDATA[<skml domain=\\\"phone\">%s가 맞으면 전화연결이라고 말씀하세요.</skml>]]>";
		result = find(sentence);
		printList(result);

		// test case
		sentence = "<![CDATA[<skml domain=\"phone\">연락처 %s와 %s가 있어요. 몇 번째 분에게 전화를 걸까요?</skml>]]>";
		result = find(sentence);
		printList(result);

		// test case
		sentence = "<![CDATA[<skml domain=\"phone\">연락처 %1$s와 %2$s가 있어요. 몇 번째 분에게 전화를 걸까요?</skml>]]>";
		result = find(sentence);
		printList(result);

		// test case
		sentence = "<![CDATA[<skml domain=\"phone\">연락처 %132$s와 %2$s가 있어요. %.22f 분에게 %s를 걸까요?</skml>]]>";
		result = find(sentence);
		printList(result);
	}

	// "\%(?:\\d+\\$)?[dfsu]\"
	// "\%[sdf]"
	private static final String REG_EXP = "\\%(\\d+\\$)?(.\\d+)?[dfsu]";

	public String[] find(String sentence) {
		log("printPatternMatch-start:"+sentence+", reg:"+REG_EXP);
		List<String> list = new ArrayList<>();
		Pattern p = Pattern.compile(REG_EXP);
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
				log("idxStart:"+idxStart + ", idxEnd:"+ idxEnd +", group:"+group + ", groupCount:"+ m.groupCount() + ", subSentence:"+subSentence);
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
		log("[last] idxStart:"+idxStart + ", idxEnd:"+ idxEnd + ", subSentence:"+subSentence);
		// print out for debug
		///for (String text : list) {
		//	log("[result-getTruncatedSentence] text:" + text);
		//}
		log("printPatternMatch-end");
		return list.toArray(new String[0]);
	}

	private void log(String msg) {
		SLog.d(TAG, msg);
	}

	private void printList(String[] array) {
		if (array == null) {
			log("array is null");
		}
		log("-- array print(start) ------------------");
		for (String item : array) {
			log("- item:"+item);
		}
		log("-- array print(end) ------------------");
	}
}
