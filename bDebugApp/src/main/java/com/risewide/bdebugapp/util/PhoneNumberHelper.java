package com.risewide.bdebugapp.util;

import java.util.Locale;

import android.os.Build;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;

/**
 * @author hyunho.mo
 *
 * @since 2017.08.10
 */
public class PhoneNumberHelper {

    /**
     * equals to Pattern.compile("^(01|821)[016789]")
     *
     * @param number
     *
     * @return true if number is mobile phone number, otherwise false.
     */
    public static boolean isMobilePhoneNumber(String number) {
        if (!TextUtils.isEmpty(number)) {
            int next = -1;
            if (number.startsWith("01")) {
                next = 2;
            } else if (number.startsWith("821")) {
                next = 3;
            }
            if (next != -1) {
                char ch = number.charAt(next);
                for (char mobileNumberType : new char[] {'0', '1', '6', '7', '8', '9'}) {
                    if (ch == mobileNumberType) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * equals to Pattern.compile("^(0|82)70")
     *
     * @param number
     *
     * @return true if number is internet phone number, otherwise false.
     */
    public static boolean isInternetPhoneNumber(String number) {
        if (!TextUtils.isEmpty(number)) {
            int next = -1;
            if (number.startsWith("0")) {
                next = 1;
            } else if (number.startsWith("82")) {
                next = 2;
            }

            if (next != -1) {
                try {
                    return "70".equals(number.substring(next, next+2));
                } catch (IndexOutOfBoundsException e) {
                    // Do nothing.
                }
            }
        }
        return false;
    }

    /**
     * @param phoneNumber
     * @return
     */
    public static String convertFormattedPhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return phoneNumber;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return PhoneNumberUtils.formatNumber(phoneNumber, Locale.getDefault().getCountry());
        } else {
            return PhoneNumberUtils.formatNumber(phoneNumber);
        }
    }

    /**
     * @param phoneNumber1
     * @param phoneNumber2
     * @return
     */
    public static boolean comparePhoneNumber(String phoneNumber1, String phoneNumber2) {
        if (TextUtils.isEmpty(phoneNumber1) || TextUtils.isEmpty(phoneNumber2)) {
            return false;
        }
        return PhoneNumberUtils.compare(phoneNumber1, phoneNumber2);
    }

    /**
     * @param phoneNumber
     * @return
     */
    public static boolean isValidPhoneNumger(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return false;
        }
        String normalizedPhoneNumber = StringHelper.removeCharAll(phoneNumber, '-');
        return TextUtils.isDigitsOnly(normalizedPhoneNumber);
    }
}
