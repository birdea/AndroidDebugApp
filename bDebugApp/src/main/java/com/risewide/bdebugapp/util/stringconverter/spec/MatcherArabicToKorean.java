package com.risewide.bdebugapp.util.stringconverter.spec;

public class MatcherArabicToKorean {

	public interface IArabicKorean {
		char getKoreanChar();
	}

	private enum ArabicKorean implements IArabicKorean{
		// scope x={1-9}
		_1(1, '일'),
		_2(2, '이'),
		_3(3, '삼'),
		_4(4, '사'),
		_5(5, '오'),
		_6(6, '육'),
		_7(7, '칠'),
		_8(8, '팔'),
		_9(9, '구'),
		// scope x={0}
		_0(0, '공'),;

		int value;
		char korean;

		ArabicKorean(int v, char k) {
			value = v;
			korean = k;
		}

		@Override
		public char getKoreanChar() {
			return korean;
		}
	}

	private enum BigArabicKorean implements IArabicKorean {
		// scope x>=10 (x*10)
		_10p1(1, '십'), // 10
		_10p2(2, '백'), // 100
		_10p3(3, '천'), // 1000
		_10p4(4, '만'), // 10000
		// scope x>=10000 (x*10000)
		_10p8(8, '억'), // 100000000
		_10p12(12, '조'), // 1000000000000
		_10p16(16, '경'), // 10000000000000000
		;

		int pow;
		char korean;

		BigArabicKorean(int p, char k) {
			pow = p;
			korean = k;
		}

		@Override
		public char getKoreanChar() {
			return korean;
		}
	}

	private static IArabicKorean find(int value) {
		for (ArabicKorean number : ArabicKorean.values()) {
			if (number.value == value) {
				return number;
			}
		}
		return ArabicKorean._0;
	}

	public static IArabicKorean get(long value) {
		log("MatcherArabicToKorean.get() value:"+value);
		// 0 > 리턴
		if (value == 0) {
			return ArabicKorean._0;
		}
		// 음수 > 양수
		if (value < 0) {
			value = Math.abs(value);
		}
		long cipher = (long) Math.log10(value); // 자릿수
		long divisor = (long) Math.pow(10, cipher); // 분모
		int quotient = (int) (value / divisor); // 몫
		int remainder = (int) (value % divisor); // 나머지
		log(String.format("cal(base) > %s/%s=%s+%s",value,divisor,quotient,remainder));

		BigArabicKorean preBigNumber = BigArabicKorean._10p1;
		for (BigArabicKorean bigNumber : BigArabicKorean.values()) {
			long d = (long) Math.pow(10, bigNumber.pow);
			int q = (int) (value / d);
			int r = (int) (value % d);
			log(String.format("cal(loop) > %s/%s=%s+%s",value,d,q,r));
			if (r > 0) {
				if (r > 9) {
					return preBigNumber;
				} else {
					return find(r);
				}
			}
			preBigNumber = bigNumber;
		}
		return ArabicKorean._0;
	}
	
	private static void log(String msg) {
		//SLog.d(msg);
	}
}
