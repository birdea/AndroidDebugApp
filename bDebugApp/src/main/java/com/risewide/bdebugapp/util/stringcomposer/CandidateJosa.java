package com.risewide.bdebugapp.util.stringcomposer;

/**
 * 각 korean enum 에 대한 로마자 변환은 아래 사이트를 참고.
 * http://roman.cs.pusan.ac.kr/
 *
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

	public static CandidateJosa getJosaSet(String formedSentence) {
		FormatSpecifier[] formatArray = FormatSpecifier.values();
		for (CandidateJosa josa : values()) {
			if (josa.isContained(formedSentence, formatArray)) {
				return josa;
			}
		}
			/*
			 * CandidateJosa[] josas = values(); for (int i = 0; i < formedSentence.length(); i++) { char c =
			 * formedSentence.charAt(i); for(CandidateJosa josa : josas) { if(josa.isMatched(String.valueOf(c))){ return
			 * josa; } } }
			 */
		return CandidateJosa.UNKNOWN;
	}

	public boolean isContained(String sentence, FormatSpecifier[] formatArray) {
		if (josaWithJongsung == null || josaWithoutJongsung == null) {
			return false;
		}
		for (FormatSpecifier formatSpecifier : formatArray) {
			String prefix = formatSpecifier.format;
			if((sentence.contains(prefix + josaWithJongsung) || sentence.contains(prefix + josaWithoutJongsung))) {
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
