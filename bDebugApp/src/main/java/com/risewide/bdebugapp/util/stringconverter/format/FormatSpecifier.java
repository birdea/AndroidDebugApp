package com.risewide.bdebugapp.util.stringconverter.format;

import com.risewide.bdebugapp.util.SLog;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by birdea on 2016-11-22.
 */

public class FormatSpecifier {

	private static final String REG_EXP = "\\%(\\d+\\$)?(.\\d+)?[dfsu]";
	private List<String> truncatedList;
	private List<String> formatList;

	public FormatSpecifier() {
	}

	public boolean parse(String text) {
		Log("printPatternMatch-start:"+text+", REG_EXP:"+REG_EXP);
		truncatedList = new ArrayList<>();
		formatList = new ArrayList<>();
		Pattern p = Pattern.compile(REG_EXP);
		Matcher m = p.matcher(text);
		int idxStart = 0, idxBase = 0;
		boolean ignoreFirstFind = true;
		String subSentence = null;
		while (m.find()) {
			idxStart = m.start();
			String group = m.group();
			formatList.add(group);
			if (!ignoreFirstFind) {
				subSentence = text.substring(idxBase, idxStart);
				idxBase = idxStart;
				truncatedList.add(subSentence);
			} else {
				ignoreFirstFind = false;
			}
			Log("[find] idxStart:"+idxStart + ", group:"+group + ", subSentence:"+subSentence);
		}
		//
		subSentence = text.substring(idxBase);
		truncatedList.add(subSentence);
		Log("[remain] idxStart:"+idxStart + ", subSentence:"+subSentence);
		// print out for debug
		for (String item : truncatedList) {
			Log("[result-getTruncatedSentence] item:" + item);
		}
		Log("printPatternMatch-end");
		return true;
	}

	public List<String> getFormatSpecifiers() {
		return formatList;
	}

	public String getFormatSpecifier() {
		return formatList.get(0);
	}

	public int getCountOfFormatSpecifier() {
		return truncatedList.size();
	}

	public List<String> getTruncatedSentence() {
		return truncatedList;
	}

	private void Log(String msg) {
		SLog.d("FormatSpecifier", msg);
	}
}
