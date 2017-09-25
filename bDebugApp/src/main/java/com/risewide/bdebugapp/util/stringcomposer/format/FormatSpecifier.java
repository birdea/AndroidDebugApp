package com.risewide.bdebugapp.util.stringcomposer.format;

import com.risewide.bdebugapp.util.stringcomposer.composer.JosaComposerInteger;
import com.risewide.bdebugapp.util.stringcomposer.composer.JosaComposerString;
import com.risewide.bdebugapp.util.stringcomposer.composer.JosaComposer;

/**
 * Created by birdea on 2016-11-22.
 */

public enum FormatSpecifier {

	_s("%s", 's') {
		@Override
		public JosaComposer getComposer() {
			if (josaComposer == null) {
				return new JosaComposerString();
			}
			return josaComposer;
		}
	},
	_d("%d", 'd') {
		@Override
		public JosaComposer getComposer() {
			if (josaComposer == null) {
				return new JosaComposerInteger();
			}
			return josaComposer;
		}
	},;
	String format;
	char marker;
	JosaComposer josaComposer;

	FormatSpecifier(String format, char marker) {
		this.format = format;
		this.marker = marker;
	}

	public String getFormat() {
		return format;
	}

	/**
	 * String regularExpression = "\\%[sdf]"
	 * 
	 * @return
	 */
	public static String getRegularExpression() {
		StringBuilder sb = new StringBuilder();
		String prefix = "\\%[";
		String suffix = "]";
		//
		sb.append(prefix);
		for (FormatSpecifier form : values()) {
			sb.append(form.marker);
		}
		//
		sb.append(suffix);
		return sb.toString();
	}

	public static int getLongestFormatLength() {
		int longer = 0;
		for (FormatSpecifier form : values()) {
			int length = form.format.length();
			if (length > longer) {
				longer = length;
			}
		}
		return longer;
	}

	public static FormatSpecifier getProperType(Object value) {
		if (value == null) {
			return null;
		} else if (value instanceof Short) {
			return _d;
		} else if (value instanceof Integer) {
			return _d;
		} else if (value instanceof Long) {
			return _d;
		} else {
			return _s;
		}
	}

	abstract public JosaComposer getComposer();
}
