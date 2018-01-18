package com.risewide.bdebugapp.util.stringconverter.data;

import java.util.List;

/**
 * 각 korean enum 에 대한 로마자 변환은 아래 사이트를 참고.
 *
 *
 * @see <p><a href="http://roman.cs.pusan.ac.kr/">로마자 변환 참고 사이트</a></p>
 * Created by birdea on 2016-11-22.
 */

public enum KoreanJosa {
	UNKNOWN(null, null) {
		@Override
		public JosaSet process(char lastChar) {
			return null;
		}
	},
	eun_neun("은", "는") {
		@Override
		public JosaSet process(char lastChar) {
			return processCommon(lastChar);
		}
	},
	i_ga("이", "가") {
		@Override
		public JosaSet process(char lastChar) {
			return processCommon(lastChar);
		}
	},
	eul_reul("을", "를") {
		@Override
		public JosaSet process(char lastChar) {
			return processCommon(lastChar);
		}
	},
	gwa_wa("과", "와") {
		@Override
		public JosaSet process(char lastChar) {
			return processCommon(lastChar);
		}
	},
	euro_ro("으로", "로") {
		@Override
		public JosaSet process(char lastChar) {
			int x = lastChar - 44032;
			int[] devide = new int[3];
			devide[0] = 0x1100 + ((x / 28) / 21);
			devide[1] = 0x1161 + ((x / 21) % 21);
			devide[2] = 0x11a8 + (x % 28);
			if(4520 == devide[2]           // 받침 없음
					|| 4528 == devide[2]){ // 받침 'ㄹ'
				return new JosaSet(getJosaWithoutJongsung(), getJosaWithJongsung()); //로
			} else {
				return new JosaSet(getJosaWithJongsung(), getJosaWithoutJongsung()); //으로
			}
		}
	},
	;
	private final String josaWithJongsung;
	private final String josaWithoutJongsung;

	KoreanJosa(String josaWith, String josaWithout) {
		josaWithJongsung = josaWith;
		josaWithoutJongsung = josaWithout;
	}

	public String getJosaWithJongsung()	{
		return josaWithJongsung;
	}

	public String getJosaWithoutJongsung() {
		return josaWithoutJongsung;
	}

	public abstract JosaSet process(char lastChar);

	protected JosaSet processCommon(char lastChar) {
		if ((lastChar - 0xAC00) % 28 > 0) {
			return new JosaSet(josaWithJongsung, josaWithoutJongsung);
		} else {
			return new JosaSet(josaWithoutJongsung, josaWithJongsung);
		}
	}

	public static KoreanJosa getKoreanJosa(String josawithJongsung, String josawithoutJongsung) {
		for (KoreanJosa item : values()) {
			if (item.josaWithJongsung == null || item.josaWithoutJongsung == null) {
				continue;
			}
			if (item.josaWithJongsung.equals(josawithJongsung) &&
					item.josaWithoutJongsung.equals(josawithoutJongsung))
				return item;
		}
		return UNKNOWN;
	}

	public static KoreanJosa getJosaSet(String formatSentence, List<String> formatArray, String withEndTag) {
		for (KoreanJosa josa : values()) {
			if (josa.isContained(formatSentence, formatArray, withEndTag)) {
				return josa;
			}
		}
		return KoreanJosa.UNKNOWN;
	}

	/**
	 * <p>특정 문장에 알려진 포맷터와 알려진 조사가 (내부) 규격대로 올바로 구성되어 있는지 체크</p>
	 * ex: %s는 오케이입니다. (acceptable)<br>
	 * ex: %s 는 낫오케이입니다. (not-acceptable)<br>
	 * @param sentence
	 * @param formatArray
	 * @return
	 */
	public boolean isContained(String sentence, List<String> formatArray, String withEndTag) {
		if (josaWithJongsung == null || josaWithoutJongsung == null) {
			return false;
		}
		final String BLANK = " ";
		for (String formatSpecifier : formatArray) {
			if ((sentence.contains(formatSpecifier + withEndTag + josaWithJongsung + BLANK) ||
				sentence.contains(formatSpecifier + withEndTag + josaWithoutJongsung + BLANK))) {
				return true;
			}
		}
		return false;
	}
}
