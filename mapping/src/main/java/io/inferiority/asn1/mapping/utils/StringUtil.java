package io.inferiority.asn1.mapping.utils;

import java.util.Objects;

/**
 * @author cuijiufeng
 * @date 2023/3/28 21:42
 */
public class StringUtil {

    public static String delThroughline(String identifier) {
        return identifier.replaceAll("-", "");
    }

    public static String throughline2underline(String identifier) {
        return identifier.replaceAll("-", "_");
    }

    public static String throughline2hump(String identifier, boolean firstCharUpper) {
        Objects.requireNonNull(identifier, "identifier can't be null");
        boolean flag = firstCharUpper;
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < identifier.length(); i++) {
            char ch = identifier.charAt(i);
            if (flag) {
                ch = Character.toUpperCase(ch);
                flag = false;
            }
            if (ch == '-') {
                flag = true;
                continue;
            }
            ret.append(ch);
        }
        return ret.toString();
    }
}
