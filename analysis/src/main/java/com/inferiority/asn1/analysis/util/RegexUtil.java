package com.inferiority.asn1.analysis.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
        return Objects.nonNull(matcher(regex, input));
    }

    public static String matcher(String regex, CharSequence input) {
        return matcher(0, regex, input);
    }

    public static String matcher(int start, String regex, CharSequence input) {
        Matcher matcher = compileMatcher(regex, input);
        return matcher.find(start) ? matcher.group() : null;
    }

    public static <T> T matcherFunc(String regex, CharSequence input, Function<String, T> function) {
        Matcher matcher = compileMatcher(regex, input);
        return matcher.find() ? function.apply(matcher.group()) : null;
    }

    public static void matcherConsumer(String regex, CharSequence input, Consumer<String> consumer) {
        Matcher matcher = compileMatcher(regex, input);
        if (matcher.find()) {
            consumer.accept(matcher.group());
        }
    }

    /**
     * @param regex
     * @param input
     * @param consumer
     * @return java.lang.CharSequence 返回input.replace(matcher.group(), "")
    */
    public static String matcherReplaceConsumer(String regex, String input, Consumer<String> consumer) {
        Matcher matcher = compileMatcher(regex, input);
        if (matcher.find()) {
            consumer.accept(matcher.group());
            return input.substring(0, matcher.start()) + input.substring(matcher.end());
        }
        return null;
    }

    /**
     * @param regex
     * @param input
     * @param consumer 两次匹配之间的值
     * @return java.lang.String 返回input.replace(matcher.group(), "")
    */
    public static String matcherReplaceBetweenConsumer(String regex, String input, Consumer<CharSequence> consumer) {
        Matcher matcherOne = compileMatcher(regex, input);
        if (!matcherOne.find()) {
            return null;
        }
        Matcher matcherTwo = compileMatcher(regex, input);
        if (!matcherTwo.find(matcherOne.end())) {
            consumer.accept(input);
            return null;
        }
        consumer.accept(input.subSequence(matcherOne.start(), matcherTwo.start()));
        return input.substring(0, matcherOne.start()) + input.substring(matcherTwo.start());
    }
}
