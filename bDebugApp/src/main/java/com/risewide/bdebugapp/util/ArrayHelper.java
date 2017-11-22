package com.risewide.bdebugapp.util;

import java.util.List;

/**
 * @author hyunho.mo
 *
 * @since 2017.02.21
 */
public class ArrayHelper {

    /**
     * @param arraySize
     * @param index
     * @return
     */
    public static boolean contains(int arraySize, int index) {
        return (arraySize > 1) && (0 <= index) && (index < arraySize);
    }

    /**
     * @param array
     * @param index
     * @return
     */
    public static boolean contains(Object[] array, int index) {
        if (isEmpty(array)) {
            return false;
        }
        return (0 <= index) && (index < array.length);
    }

    /**
     * @param list
     * @param index
     * @return
     */
    public static boolean contains(List list, int index) {
        if (isEmpty(list)) {
            return false;
        }
        return (0 <= index) && (index < list.size());
    }

    /**
     * @param list
     * @param o
     * @return
     */
    public static boolean contains(List list, Object o) {
        return !isEmpty(list) && list.contains(o);
    }

    /**
     * @param array
     * @param index
     * @return
     */
    public static boolean isLast(Object[] array, int index) {
        if (isEmpty(array)) {
            return false;
        }
        return (index == (array.length - 1));
    }

    /**
     * @param array
     * @param index
     * @return
     */
    public static boolean isLast(List array, int index) {
        if (isEmpty(array)) {
            return false;
        }
        return (index == (array.size() - 1));
    }

    /**
     * @param array
     * @param element
     * @return
     */
    public static boolean isLast(List array, Object element) {
        if (isEmpty(array)) {
            return false;
        }
        int index = array.indexOf(element);
        return (index != -1) && (index == (array.size() - 1));

    }

    /**
     * @param array
     * @return
     */
    public static boolean isEmpty(int[] array) {
        return (array == null) || (array.length <= 0);
    }

    /**
     * @param array
     * @return
     */
    public static boolean isEmpty(Object[] array) {
        return (array == null) || (array.length <= 0);
    }

    /**
     * @param list
     * @return
     */
    public static boolean isEmpty(List list) {
        return (list == null) || list.isEmpty();
    }

    /**
     * @param list
     * @return
     */
    public static boolean hasOneElement(List list) {
        return !isEmpty(list) && (list.size() == 1);
    }

    /**
     * @param list
     * @return
     */
    public static boolean hasTwoElement(List list) {
        return !isEmpty(list) && (list.size() == 2);
    }

    /**
     * @param array
     * @return
     */
    public static int[] toPrimitive(Integer[] array) {
        if (array == null) {
            return null;
        }

        if (isEmpty(array)) {
            return null;
        }

        final int[] result = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i];
        }
        return result;
    }
}
