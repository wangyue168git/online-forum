package com.bolo.util;

/**
 * @Author wangyue
 * @Date 17:10
 */
public class ArrayUtil {
    public static int indexOf(Object[] array, Object objectToFind) {
        return indexOf(array, objectToFind, 0);
    }
    public static int indexOf(Object[] array, Object objectToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (objectToFind == null) {
            for (int i = startIndex; i < array.length; i++) {
                if (array[i] == null) {
                    return i;
                }
            }
        } else if (array.getClass().getComponentType().isInstance(objectToFind)) {
            String findstr = objectToFind.toString().toLowerCase();
            for (int i = startIndex; i < array.length; i++) {
                if (array[i] != null && findstr.indexOf(array[i].toString().toLowerCase()) >= 0) {
                    return i;
                }
            }
        }
        return -1;
    }
}
