package com.risewide.bdebugapp.communication.helper;

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

}
