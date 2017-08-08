package com.risewide.bdebugapp.communication;

import android.content.Context;

/**
 * Created by birdea on 2017-08-08.
 */

public abstract class AbsMessageReader {

	public abstract void read(Context context, SmsUnifyMessageReader.OnReadTextMessageListener listener);
}
