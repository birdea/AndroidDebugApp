package com.risewide.bdebugapp.util.stringcomposer.composer;

import com.risewide.bdebugapp.util.stringcomposer.data.JosaSet;

/**
 * Created by birdea on 2016-11-22.
 */

public abstract class JosaComposer<T> {
	public abstract JosaSet select(T word, String josaWithJongsung, String josaWithoutJongsung);
}
