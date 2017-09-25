package com.risewide.bdebugapp.util.stringcomposer.data;

/**
 * Created by birdea on 2016-11-22.
 */

public class JosaSet {
	private String properJosa;
	private String unproperJosa;

	public JosaSet(String proper, String unproper) {
		properJosa = proper;
		unproperJosa = unproper;
	}

	public String getProperJosa() {
		return properJosa;
	}

	public String getUnproperJosa() {
		return unproperJosa;
	}
}
