package com.inferiority.asn1.mapping.utils;

/**
 * @author cuijiufeng
 * @date 2023/3/28 21:42
 */
public class StringUtil {

    public static String throughline2Underline(String identifier) {
        return identifier.replaceAll("-", "_");
    }
}
