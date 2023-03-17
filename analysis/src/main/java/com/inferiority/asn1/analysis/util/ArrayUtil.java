package com.inferiority.asn1.analysis.util;

import java.util.Objects;

/**
 * @author cuijiufeng
 * @Class ArrayUtil
 * @Date 2023/3/17 14:41
 */
public class ArrayUtil {
    public static boolean contains(Object[] arr, Object o) {
        Objects.requireNonNull(arr, "arr can't be null");
        Objects.requireNonNull(o, "element can't be null");
        for (Object e : arr) {
            if (o.equals(e)) {
                return true;
            }
        }
        return false;
    }
}
