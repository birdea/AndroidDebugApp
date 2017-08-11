package com.risewide.bdebugapp.communication.util;

import android.view.View;
import android.widget.EditText;

/**
 * Created by birdea on 2017-08-04.
 */

public class WidgetHelper {

	public static String getText(EditText et) {
		String result = "";
		try {
			result = et.getText().toString();
		} catch (Exception ignore){}
		return result;
	}

	public static int changeVisiblity(View view) {
		if (view == null) {
			return Integer.MIN_VALUE;
		}
		int result;
		switch (view.getVisibility()) {
			case View.VISIBLE:
				result = View.GONE;
				break;
			default:
				result = View.VISIBLE;
				break;
		}
		view.setVisibility(result);
		return result;
	}
}
