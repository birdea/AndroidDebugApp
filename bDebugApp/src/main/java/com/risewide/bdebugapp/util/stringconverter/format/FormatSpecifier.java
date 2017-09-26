package com.risewide.bdebugapp.util.stringconverter.format;

import com.risewide.bdebugapp.util.stringconverter.converter.JosaConverterObject;
import com.risewide.bdebugapp.util.stringconverter.converter.JosaConverter;

/**
 * Created by birdea on 2016-11-22.
 */

public enum FormatSpecifier {

	_s("%s", 's') {
		@Override
		public JosaConverter getConverter() {
			if (josaConverter == null) {
				return new JosaConverterObject();
			}
			return josaConverter;
		}
	},
	;
	String format;
	char marker;
	JosaConverter josaConverter;

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

	public static FormatSpecifier getProperType(Object value) {
		if (value == null) {
			return null;
		}
		return _s;
	}

	abstract public JosaConverter getConverter();
}
