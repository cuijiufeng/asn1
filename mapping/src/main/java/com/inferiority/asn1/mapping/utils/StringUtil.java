package com.inferiority.asn1.mapping.utils;

import java.util.Objects;

/**
 * @author cuijiufeng
 * @date 2023/3/28 21:42
 */
public class StringUtil {

    public static String throughline2underline(String identifier) {
        return identifier.replaceAll("-", "_");
    }

    public static String throughline2hump(String identifier) {
        Objects.requireNonNull(identifier, "identifier can't be null");
        boolean flag = false;
        char[] chars = identifier.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        for (int i = 1; i < chars.length; i++) {
            if (flag) {
                chars[i] = Character.toUpperCase(chars[i]);
                flag = false;
            }
            if (chars[i] == '-') {
                flag = true;
            }
        }
        return String.valueOf(chars);
    }
}
