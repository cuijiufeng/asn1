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

    private static Matcher compileMatcher(String regex, CharSequence input) {
        return PATTERN_CACHED.computeIfAbsent(regex, Pattern::compile).matcher(input);
    }

    public static boolean matches(String regex, CharSequence input) {
        return compileMatcher(regex, input).find();
    }

    public static int start(int start, String regex, CharSequence input) {
        Matcher matcher = compileMatcher(regex, input);
        return matcher.find(start) ? matcher.start() : -1;
    }

    public static int end(int start, String regex, CharSequence input) {
        Matcher matcher = compileMatcher(regex, input);
        return matcher.find(start) ? matcher.end() : -1;
    }

    public static String matcher(String regex, CharSequence input) {
        Matcher matcher = compileMatcher(regex, input);
        return matcher.find() ? matcher.group() : null;
    }

    public static <T> T matcherFunction(String regex, CharSequence input, Function<String, T> function) {
        Matcher matcher = compileMatcher(regex, input);
        return matcher.find() ? function.apply(matcher.group()) : null;
    }

    public static void matcherConsumer(String regex, CharSequence input, Consumer<String> consumer) {
        Matcher matcher = compileMatcher(regex, input);
        if (matcher.find()) {
            consumer.accept(matcher.group());
        }
    }
}
