package com.risewide.bdebugapp.util.stringcomposer.spec;

import android.support.annotation.NonNull;

/**
 * Created by birdea on 2017-09-25.
 */

public enum MatcherAlphabetToKorean {
	a('a', "에이"),
	b('b', "비"),
	c('c', "씨"),
	d('d', "디"),
	e('e', "이"),
	f('f', "에프"),
	g('g', "지"),
	h('h', "에이치"),
	i('i', "아이"),
	j('j', "제이"),
	k('k', "케이"),
	l('l', "엘"),
	m('m', "엠"),
	n('n', "엔"),
	o('o', "오"),
	p('p', "피"),
	q('q', "큐"),
	r('r', "알"),
	s('s', "에스"),
	t('t', "티"),
	u('u', "유"),
	v('v', "브이"),
	w('w', "더블유"),
	x('x', "엑스"),
	y('y', "와이"),
	z('z', "제트"),
	UNKNOWN('0', ""),
	;

	char word;
	String korean;

	MatcherAlphabetToKorean(char w, String k) {
		word = w;
		korean = k;
	}

	public char getKoreanLastChar() {
		return korean.charAt(korean.length()-1);
	}

	@NonNull
	public static MatcherAlphabetToKorean getKoreanWord(char engWord) {
		char lowerCaseChar = Character.toLowerCase(engWord);
		MatcherAlphabetToKorean selected = null;
		for(MatcherAlphabetToKorean candi : values()) {
			if (MatcherAlphabetToKorean.UNKNOWN.equals(candi)) {
				continue;
			}
			if (candi.word == lowerCaseChar) {
				selected = candi;
				break;
			}
		}
		if (selected == null) {
			selected = MatcherAlphabetToKorean.UNKNOWN;
			selected.word = engWord;
			selected.korean = String.valueOf(engWord);
		}
		return selected;
	}
}
