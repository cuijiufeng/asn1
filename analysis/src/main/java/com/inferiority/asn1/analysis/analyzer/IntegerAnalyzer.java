package com.inferiority.asn1.analysis.analyzer;

import com.inferiority.asn1.analysis.AnalysisException;
import com.inferiority.asn1.analysis.common.Operator;
import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.analysis.model.Module;
import com.inferiority.asn1.analysis.util.RegexUtil;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cuijiufeng
 * @Class IntegerAnalyzer
 * @Date 2023/2/28 13:27
 */
public class IntegerAnalyzer extends AbstractAnalyzer {
    private static final IntegerAnalyzer analyzer = new IntegerAnalyzer();

    public static final String REGEX_INTEGER_BODY = "(" + Operator.OPENING_BRACE + CRLF + "(" + REGEX_IDENTIFIER + CRLF_LEAST + "[\\S]+[,]?" + CRLF + ")+" +
            Operator.CLOSING_BRACE + ")";

    public static final String REGEX_INTEGER_RANGE = "(" + Operator.LEFT_BRACKET +
            REGEX_NUM_COMPOUND + "(" + Operator.RANGE + REGEX_NUM_COMPOUND + ")?" +
            Operator.RIGHT_BRACKET + ")";

    public static final String REGEX_INTEGER = CRLF + REGEX_IDENTIFIER + CRLF + Operator.ASSIGNMENT + CRLF + REGEX_IDENTIFIER + CRLF +
            REGEX_INTEGER_BODY + "?" + CRLF + REGEX_INTEGER_RANGE + "?" + CRLF;

    public static final String REGEX_INTEGER_VAL = REGEX_IDENTIFIER + CRLF_LEAST + "%s" + CRLF + Operator.ASSIGNMENT + CRLF + REGEX_NUM + CRLF;

    public static AbstractAnalyzer getInstance() {
        return analyzer;
    }

    @Override
    public Definition parse(List<Module> modules, Module module, String primitiveType, String text, String moduleText) throws AnalysisException {
        if (!RegexUtil.matches(REGEX_INTEGER, text)) {
            throw new AnalysisException("not a valid integer type definition.\n" + text);
        }
        Definition definition = new Definition();
        definition.setPrimitiveType(primitiveType);
        definition.setDefinitionText(text);
        //identifier
        definition.setIdentifier(RegexUtil.matcher(REGEX_IDENTIFIER, text));
        //body
        definition.setSubBodyText(RegexUtil.matcherFunc(REGEX_INTEGER_BODY, text, group -> {
            List<Definition> entries = Arrays.stream(
                    group.replaceAll(Operator.OPENING_BRACE, "")
                            .replaceAll(Operator.CLOSING_BRACE, "")
                            .split(Operator.COMMA))
                    .map(String::trim)
                    .map(str -> new Definition(str.split("\\s+", 2)[0], str.split("\\s+", 2)[1]))
                    .collect(Collectors.toList());
            definition.setSubDefs(entries);
            return group;
        }));
        text = text.replaceAll(REGEX_INTEGER_BODY, "");
        //range
        RegexUtil.matcherConsumer(REGEX_INTEGER_RANGE, text, group -> {
            String[] range = group.replaceAll(Operator.LEFT_BRACKET, "")
                    .replaceAll(Operator.RIGHT_BRACKET, "")
                    .split(Operator.RANGE);
            definition.setRangeMin(range[0]);
            definition.setRangeMax(range[range.length - 1]);
        });
        //value
        definition.setValues(parseValues(String.format(REGEX_INTEGER_VAL, definition.getIdentifier()), moduleText, valueText ->
            new AbstractMap.SimpleEntry<>(valueText.split("\\s+")[0], valueText.split(Operator.ASSIGNMENT)[1].trim())
        ));
        return definition;
    }
}
