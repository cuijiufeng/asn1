package com.inferiority.asn1.analysis.analyzer;

import com.inferiority.asn1.analysis.AnalysisException;
import com.inferiority.asn1.analysis.common.Operator;
import com.inferiority.asn1.analysis.common.Reserved;
import com.inferiority.asn1.analysis.model.Definition;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cuijiufeng
 * @Class IntegerAnalyzer
 * @Date 2023/2/28 13:27
 */
public class IntegerAnalyzer extends AbstractAnalyzer {
    private static final IntegerAnalyzer analyzer = new IntegerAnalyzer();

    public static final String REGEX_INTEGER_BODY = Operator.OPENING_BRACE + CRLF + "(" + REGEX_IDENTIFIER + CRLF_LEAST + "[\\S]+," + CRLF + ")*(" +
            REGEX_IDENTIFIER + CRLF_LEAST + "[\\S]+" + CRLF + ")" + Operator.CLOSING_BRACE;
    public static final Pattern PATTERN_INTEGER_BODY = Pattern.compile(REGEX_INTEGER_BODY);

    public static final String REGEX_INTEGER_RANGE = Operator.LEFT_BRACKET +
            "(" + REGEX_NUM_COMPOUND + Operator.RANGE + REGEX_NUM_COMPOUND + "|" + REGEX_NUM_COMPOUND + ")" +
            Operator.RIGHT_BRACKET;
    public static final Pattern PATTERN_INTEGER_RANGE = Pattern.compile(REGEX_INTEGER_RANGE);

    public static final String REGEX_INTEGER_VAL = REGEX_IDENTIFIER + CRLF_LEAST + "%s" + CRLF + Operator.ASSIGNMENT + CRLF + REGEX_NUM + CRLF;

    public static final String REGEX_INTEGER = CRLF + REGEX_IDENTIFIER + CRLF + Operator.ASSIGNMENT + CRLF + Reserved.INTEGER + CRLF +
            "(" + REGEX_INTEGER_BODY + ")?" + CRLF +
            "(" + REGEX_INTEGER_RANGE + ")?" + CRLF;
    public static final Pattern PATTERN_INTEGER = Pattern.compile(REGEX_INTEGER);


    public static AbstractAnalyzer getInstance() {
        return analyzer;
    }

    @Override
    public Definition parse(String text, StringBuilder moduleText) throws AnalysisException {
        Definition definition = new Definition();
        definition.setPrimitiveType(Reserved.INTEGER);
        definition.setDefinitionText(text);
        //identifier
        Matcher identifierMatcher = AbstractAnalyzer.PATTERN_IDENTIFIER.matcher(text);
        if (!identifierMatcher.find()) {
            throw new AnalysisException("no valid type identifier found");
        }
        definition.setIdentifier(identifierMatcher.group());
        //body
        Matcher bodyMatcher = PATTERN_INTEGER_BODY.matcher(text);
        if (bodyMatcher.find()) {
            definition.setBodyText(bodyMatcher.group());
            AbstractMap.SimpleEntry<String, String>[] entries =
                    Arrays.stream(definition.getBodyText()
                        .replaceAll(Operator.OPENING_BRACE, "")
                        .replaceAll(Operator.CLOSING_BRACE, "")
                        .split(Operator.COMMA))
                    .map(String::trim)
                    .map(str -> new AbstractMap.SimpleEntry<>(str.split("\\s+")[0], str.split("\\s+")[1]))
                    .toArray(AbstractMap.SimpleEntry[]::new);
            definition.setSubDefs(entries);
            text = text.replaceAll(REGEX_INTEGER_BODY, "");
        }
        //range
        Matcher rangeMatcher = PATTERN_INTEGER_RANGE.matcher(text);
        if (rangeMatcher.find()) {
            String[] range = rangeMatcher.group()
                    .replaceAll(Operator.LEFT_BRACKET, "")
                    .replaceAll(Operator.RIGHT_BRACKET, "")
                    .split(Operator.RANGE);
            definition.setRangeMin(range[0]);
            definition.setRangeMax(range[range.length - 1]);
        }
        //value
        Pattern valuePattern = Pattern.compile(String.format(REGEX_INTEGER_VAL, definition.getIdentifier()));
        Matcher valueMatcher = null;
        List<AbstractMap.SimpleEntry<String, String>> values = new ArrayList<>(16);
        while ((valueMatcher = valuePattern.matcher(moduleText)).find()) {
            String valueText = valueMatcher.group().trim();
            String[] split = valueText.split("\\s+");
            values.add(new AbstractMap.SimpleEntry<>(split[0], split[split.length - 1]));
            moduleText.delete(valueMatcher.start(), valueMatcher.end());
        }
        definition.setValues(values.isEmpty() ? null : values.toArray(new AbstractMap.SimpleEntry[0]));
        return definition;
    }
}
