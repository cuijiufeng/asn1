package com.inferiority.asn1.analysis.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cuijiufeng
 * @Class RegexUtil
 * @Date 2023/3/1 11:29
 */
public class RegexUtil {
    public static final Map<String, Pattern> PATTERN_CACHED = new HashMap<>(16);

    public static boolean matches(String regex, CharSequence input) {
        Matcher matcher = PATTERN_CACHED.computeIfAbsent(regex, Pattern::compile).matcher(input);
        return matcher.find();
    }

    public static String matcher(String regex, CharSequence input) {
        Matcher matcher = PATTERN_CACHED.computeIfAbsent(regex, Pattern::compile).matcher(input);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public static <T> T matcherFunction(String regex, CharSequence input, Function<String, T> function) {
        Matcher matcher = PATTERN_CACHED.computeIfAbsent(regex, Pattern::compile).matcher(input);
        if (matcher.find()) {
            return function.apply(matcher.group());
        }
        return null;
    }

    public static void matcherConsumer(String regex, CharSequence input, Consumer<String> consumer) {
        Matcher matcher = PATTERN_CACHED.computeIfAbsent(regex, Pattern::compile).matcher(input);
        if (matcher.find()) {
            consumer.accept(matcher.group());
        }
    }
}
