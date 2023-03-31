package io.inferiority.asn1.analysis.util;

import java.lang.reflect.Array;
import java.util.Arrays;
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

    @SuppressWarnings("unchecked")
    public static <T> T[] concat(T[] ... array) {
        int idx = 0;
        int size = Arrays.stream(array).mapToInt(arr -> arr.length).sum();
        T[] arr = (T[]) Array.newInstance(Object.class, size);
        for (T[] ts : array) {
            System.arraycopy(ts, 0, arr, idx, ts.length);
            idx += ts.length;
        }
        return arr;
    }
}
