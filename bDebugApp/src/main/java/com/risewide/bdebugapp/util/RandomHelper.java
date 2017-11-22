package com.risewide.bdebugapp.util;

import java.util.Random;

/**
 * @author hyunho.mo
 *
 * @since 2017.06.13
 */
public class RandomHelper {

    /**
     * @param start
     * @param end
     * @return
     */
    public static final int random(int start, int end) {
        if ((start > end) || (start < 0) || (end < 0)) {
            return 0;
        }
        return (int)(Math.random() * (end - start + 1)) + start;
    }

    /**
     * @param elements
     * @param length
     * @return
     */
    public static String randomText(String elements, int length) {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        while (builder.length() < length) {
            int index = (int)(random.nextFloat() * elements.length());
            builder.append(elements.charAt(index));
        }
        return builder.toString();
    }
}
