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
        boolean flag = true;
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < identifier.length(); i++) {
            if (flag) {
                ret.append(Character.toUpperCase(identifier.charAt(i)));
                flag = false;
            }
            if (identifier.charAt(i) == '-') {
                flag = true;
            }
        }
        return ret.toString();
    }
}
