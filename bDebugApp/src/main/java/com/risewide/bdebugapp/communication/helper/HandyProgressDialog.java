package com.risewide.bdebugapp.communication.helper;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by birdea on 2017-05-10.
 */

public class HandyProgressDialog {

	ProgressDialog pg;

	public HandyProgressDialog(Context context) {
		pg = new ProgressDialog(context);
	}

	public void show() {
		show("Loading...");
	}

	public void show(String msg) {
		pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pg.setMessage(msg);
		pg.show();
	}

	public void dismiss() {
		pg.dismiss();
	}
}
