package com.risewide.bdebugapp.communication.helper;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by birdea on 2017-03-20.
 */

public class TToast {

	public static void show(Context context, String text) {
		show(context, text, Toast.LENGTH_SHORT);
	}

	public static void show(Context context, String text, int duration){
		Toast.makeText(context, text, duration).show();
	}
}
