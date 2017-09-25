package com.risewide.bdebugapp.util.stringcomposer.josa;

import com.risewide.bdebugapp.util.stringcomposer.format.FormatSpecifier;

/**
 * 각 korean enum 에 대한 로마자 변환은 아래 사이트를 참고.
 *
 *
 * @see <p><a href="http://roman.cs.pusan.ac.kr/">로마자 변환 참고 사이트</a></p>
 * Created by birdea on 2016-11-22.
 */

public enum CandidateJosa {

	UNKNOWN(null, null), // @NonNull; empty set
	eun_neun("은", "는"), // %s은, %s는
	i_ga("이", "가"), // %s이, %s가
	eul_reul("을", "를"), // %s을, %s를
	gwa_wa("과", "와"), // %s과, %s와
	euro_ro("으로", "로"), // %s으로, %s로 (으로서, 으로서의, 으로써를 is covered-on)
	// 으로서_로서("으로서","로서"),
	// 으로서의_로서의("으로서의","로서의"),
	// 으로써_로써("으로써","로써"),
	;
	public String josaWithJongsung = "";
	public String josaWithoutJongsung = "";

	//
	CandidateJosa(String josaWith, String josaWithout) {
		josaWithJongsung = josaWith;
		josaWithoutJongsung = josaWithout;
	}

	public static CandidateJosa getJosaSet(String formatSentence) {
		FormatSpecifier[] formats = FormatSpecifier.values();
		for (CandidateJosa josa : values()) {
			if (josa.isContained(formatSentence, formats)) {
				return josa;
			}
		}
		return CandidateJosa.UNKNOWN;
	}

	/**
	 * <p>특정 문장에 알려진 포맷터와 알려진 조사가 (내부) 규격대로 올바로 구성되어 있는지 체크</p>
	 * ex: %s는 오케이입니다. (acceptable)<br>
	 * ex: %s 는 낫오케이입니다. (not-acceptable)<br>
	 * @param sentence
	 * @param formatArray
	 * @return
	 */
	public boolean isContained(String sentence, FormatSpecifier[] formatArray) {
		if (josaWithJongsung == null || josaWithoutJongsung == null) {
			return false;
		}
		for (FormatSpecifier formatSpecifier : formatArray) {
			String prefix = formatSpecifier.getFormat();
			if ((sentence.contains(prefix + josaWithJongsung) || sentence.contains(prefix + josaWithoutJongsung))) {
				return true;
			}
		}
		return false;
	}

	public boolean isMatched(String letter) {
		if (josaWithJongsung == null || josaWithoutJongsung == null) {
			return false;
		}
		return (josaWithJongsung.equals(letter) || josaWithoutJongsung.equals(letter));
	}
}
