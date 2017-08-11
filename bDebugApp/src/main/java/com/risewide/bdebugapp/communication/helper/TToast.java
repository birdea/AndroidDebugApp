package com.risewide.bdebugapp.communication.helper;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * Created by birdea on 2017-03-20.
 */

public class TToast {

	public static void show(Context context, String text) {
		show(context, text, Toast.LENGTH_SHORT);
	}

	public static void show(final Context context, final String text, final int duration){
		if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
			executeOnMainThread(context, text, duration);
		} else {
			Toast.makeText(context, text, duration).show();
		}
	}

	private static void executeOnMainThread(final Context context, final String text, final int duration) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, text, duration).show();
			}
		});
	}
}
