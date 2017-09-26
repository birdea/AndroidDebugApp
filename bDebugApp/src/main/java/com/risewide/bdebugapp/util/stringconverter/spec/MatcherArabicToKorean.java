package com.risewide.bdebugapp.util.stringconverter.spec;

import com.risewide.bdebugapp.util.SLog;

public enum MatcherArabicToKorean {
	// scope x={1~9}
	_1(1, '일'),
	_2(2, '이'),
	_3(3, '삼'),
	_4(4, '사'),
	_5(5, '오'),
	_6(6, '육'),
	_7(7, '칠'),
	_8(8, '팔'),
	_9(9, '구'),
	// scope x>=10 (x*10)
	_10p1(10, '십'),
	_10p2(100, '백'),
	_10p3(1000, '천'),
	_10p4(10000, '만'),
	// scope x>=10000 (x*10000)
	_10p8(100000000, '억'),
	_10p12(1000000000000L, '조'),
	_10p16(10000000000000000L, '경'),
	//
	_0(0, '공'),;

	long value;
	char korean;

	MatcherArabicToKorean(long v, char k) {
		value = v;
		korean = k;
	}

	public char getKoreanChar() {
		return korean;
	}

	public static MatcherArabicToKorean get(long value) {
		if (value < 0) {
			value = Math.abs(value);
		}
		String number = String.valueOf(value);
		int length = number.length();
		long divider = 1;
		char c = '0';
		//
		while (length-- > 0) {
			c = number.charAt(length);
			if (c > '0' && c <= '9') {
				break;
			}
			divider *= 10;
		}
		SLog.d("MatcherArabicToKorean.get() value:" + value + ", divider:" + divider + ", c:" + c);
		//
		if (divider > 1) {
			return find(divider);
		} else {
			return find(Long.parseLong(String.valueOf(c)));
		}
	}

	private static MatcherArabicToKorean find(long value) {
		SLog.d("MatcherArabicToKorean.find() value:" + value);
		MatcherArabicToKorean matcher = _0;
		if (value == 0) {
			return _0;
		}
		for (MatcherArabicToKorean spec : values()) {
			if (value < _10p4.value) {
				if (spec.value == value) {
					return spec;
				}
			} else {
				long divider = spec.value;
				if (divider == 0) {
					return matcher;
				}
				int divide = (int) (value / divider);
				int nextDivide = (int) (value / divider * 10000);
				SLog.d("MatcherArabicToKorean.find-korean.value, value:" + value + ", divider:" + divider + ", divide:"
						+ divide + ", nextDivide:" + nextDivide);
				if (divide == 0) {
					return matcher;
				}
			}
			matcher = spec;
		}
		return _0;
	}
}
