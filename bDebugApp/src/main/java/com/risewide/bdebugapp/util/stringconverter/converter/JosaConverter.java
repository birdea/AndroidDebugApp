package com.risewide.bdebugapp.util.stringconverter.converter;

import com.risewide.bdebugapp.util.stringconverter.data.JosaSet;

/**
 * Created by birdea on 2016-11-22.
 */

public abstract class JosaConverter<T> {
	public abstract JosaSet select(T word, String josaWithJongsung, String josaWithoutJongsung);
}
