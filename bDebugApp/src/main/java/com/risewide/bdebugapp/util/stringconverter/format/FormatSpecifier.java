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
		truncatedList = new ArrayList<>();
		formatList = new ArrayList<>();
	}

	public boolean parse(String text) {
		Log("printPatternMatch-start:"+text+", REG_EXP:"+REG_EXP);
		//
		formatList.clear();
		truncatedList.clear();
		//
		List<String> list = new ArrayList<>();
		Pattern p = Pattern.compile(REG_EXP);
		Matcher m = p.matcher(text);
		int idxStart = 0, idxEnd = 0, idxBase = 0;
		boolean firstMatch = false;
		String subSentence;
		while (m.find()) {
			idxStart = m.start();
			idxEnd = m.end();
			String group = m.group();
			formatList.add(group);
			if (firstMatch) {
				subSentence = text.substring(idxBase, idxStart);
				Log("idxStart:"+idxStart + ", idxEnd:"+ idxEnd +", group:"+group + ", groupCount:"+ m.groupCount() + ", subSentence:"+subSentence);
				idxBase = idxStart;
				list.add(subSentence);
			}
			else {
				firstMatch = true;
			}
		}
		//
		subSentence = text.substring(idxBase);
		list.add(subSentence);
		Log("[last] idxStart:"+idxStart + ", idxEnd:"+ idxEnd + ", subSentence:"+subSentence);
		// print out for debug
		for (String item : list) {
			Log("[result-getTruncatedSentence] item:" + item);
			truncatedList.add(item);
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
