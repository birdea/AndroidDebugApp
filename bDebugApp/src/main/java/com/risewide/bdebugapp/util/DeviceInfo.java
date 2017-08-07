package com.risewide.bdebugapp.util;

import java.lang.reflect.Field;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * Created by birdea on 2017-08-03.
 */

public class DeviceInfo {

	public static final String[] permission = { Manifest.permission.READ_PHONE_STATE, };

	public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 7;

	public static boolean hasPermissions(final Activity activity) {
		if (ContextCompat.checkSelfPermission(activity,
				Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(activity, permission, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
			return false;
		}
		return true;
	}

	public static String getPhoneNumber(Activity activity) {
		if (hasPermissions(activity)) {
			TelephonyManager t = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
			return t.getLine1Number();
		} else {
			return "n/a";
		}
	}

	public static String getDeviceAndroidOsInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("android : ").append(Build.VERSION.RELEASE);

		Field[] fields = Build.VERSION_CODES.class.getFields();
		for (Field field : fields) {
			String fieldName = field.getName();
			int fieldValue = -1;

			try {
				fieldValue = field.getInt(new Object());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}

			if (fieldValue == Build.VERSION.SDK_INT) {
				sb.append(" : ").append(fieldName).append(" : ");
				sb.append("sdk=").append(fieldValue);
			}
		}
		return sb.toString();
	}

	/** Returns the consumer friendly device name */
	public static String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			return capitalize(model);
		}
		return capitalize(manufacturer) + " " + model;
	}

	private static String capitalize(String str) {
		if (TextUtils.isEmpty(str)) {
			return str;
		}
		char[] arr = str.toCharArray();
		boolean capitalizeNext = true;

		StringBuilder phrase = new StringBuilder();
		for (char c : arr) {
			if (capitalizeNext && Character.isLetter(c)) {
				phrase.append(Character.toUpperCase(c));
				capitalizeNext = false;
				continue;
			} else if (Character.isWhitespace(c)) {
				capitalizeNext = true;
			}
			phrase.append(c);
		}

		return phrase.toString();
	}

	public static String getNetworkOperatorName(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getNetworkOperatorName();
	}

	/**
	 * MCC MNC
	 * kt - 45008
	 * skt - 45005
	 * lg - 45006
	 * @param context
	 * @return
	 */
	//
	public static String getMcc(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		String networkOperator = telephonyManager.getSimOperator();
		if (networkOperator != null && networkOperator.length() >= 3) {
			if (networkOperator.length() == 3) {
				return networkOperator;
			} else {
				return networkOperator.substring(0, 3);
			}
		}
		return null;
	}
}
