package com.risewide.bdebugapp.util.stringcomposer;

import com.risewide.bdebugapp.util.SLog;

public enum PatternArabicKorean {
	// scope x={1~9}
	_1(1, "일"),
	_2(2, "이"),
	_3(3, "삼"),
	_4(4, "사"),
	_5(5, "오"),
	_6(6, "육"),
	_7(7, "칠"),
	_8(8, "팔"),
	_9(9, "구"),
	// scope x>=10 (x*10)
	_10p1(10, "십"),
	_10p2(100, "백"),
	_10p3(1000, "천"),
	_10p4(10000, "만"),
	// scope x>=10000 (x*10000)
	_10p8(100000000, "억"),
	_10p12(1000000000000L, "조"),
	_10p16(10000000000000000L, "경"),
	//_해(100000000000000000000L, "해"),
	_0(-1, "영"),
	;
	
	long value;
	public String pattern;
	
	PatternArabicKorean(long v, String p) {
		value = v;
		pattern = p;
	}
	
	public long getDivider() {
		return value * 10;
	}

	public static PatternArabicKorean get(long value) {
		// int > String > get last char > compare with ..
		String number = String.valueOf(value);
		int length = number.length();
		int idx = length;
		long divider = 1;
		char c = '0';
		//
		while(idx > 0) {
			c = number.charAt(idx-1);
			if(c>'0' && c<='9') {
				break;
			}
			divider = divider * 10L;
			idx--;
		}
		SLog.d("PatternArabicKorean.get-value:"+value + ", divider:"+divider +", c:"+c);
		//
		if(divider > 0 && divider > 1) {
			return find(divider);
		} else {
			return find(Long.parseLong(String.valueOf(c)));
		}
	}
	
	private static PatternArabicKorean find(long value){
		PatternArabicKorean prePattern = _0;
		for(PatternArabicKorean pattern : values()) {
			if(value < _10p4.value) {
				if(pattern.value == value) {
					return pattern;
				}
			} else {
				long divider = pattern.value;
				int divide = (int) (value / divider);
				int nextDivide = (int) (value / divider * 10000);
				SLog.d("PatternArabicKorean.find-pattern.value, value:"+value + ", divider:"+divider +", divide:"+divide+", nextDivide:"+nextDivide);
				if(divide == 0) {
					return prePattern;
				}
			}
			prePattern = pattern;
		}
		return _0;
	}
}