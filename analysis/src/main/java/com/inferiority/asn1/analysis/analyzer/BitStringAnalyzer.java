package com.inferiority.asn1.analysis.analyzer;

import com.inferiority.asn1.analysis.AnalysisException;
import com.inferiority.asn1.analysis.common.Operator;
import com.inferiority.asn1.analysis.common.Reserved;
import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.analysis.model.Module;
import com.inferiority.asn1.analysis.util.RegexUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cuijiufeng
 * @Class BitStringAnalyzer
 * @Date 2023/3/17 16:39
 */
public class BitStringAnalyzer extends AbstractAnalyzer {
    private static final BitStringAnalyzer analyzer = new BitStringAnalyzer();

    private static final String REGEX_BITSTRING_BODY = "(" + Operator.OPENING_BRACE + CRLF +
            "(" + REGEX_IDENTIFIER + CRLF + Operator.LEFT_BRACKET + REGEX_NUM + Operator.RIGHT_BRACKET + Operator.COMMA + "?" + CRLF + ")+" +
            Operator.CLOSING_BRACE + ")";

    private static final String REGEX_BITSTRING_SIZE = "(" + Operator.LEFT_BRACKET + Reserved.SIZE + CRLF + Operator.LEFT_BRACKET +
            REGEX_NUM + Operator.RIGHT_BRACKET + Operator.RIGHT_BRACKET + ")";

    public static final String REGEX_BITSTRING = CRLF + REGEX_IDENTIFIER + CRLF + Operator.ASSIGNMENT + CRLF +
            "(" + REGEX_IDENTIFIER + "[ ]*)+" + REGEX_BITSTRING_BODY + "?" + CRLF + REGEX_BITSTRING_SIZE + "?" + CRLF;

    public static AbstractAnalyzer getInstance() {
        return analyzer;
    }

    @Override
    public Definition parse(List<Module> modules, Module module, String primitiveType, List<Definition> parents, String text, String moduleText) throws AnalysisException {
        if (!RegexUtil.matches(REGEX_BITSTRING, text)) {
            throw new AnalysisException("not a valid bit-string type definition.\n" + text);
        }
        Definition definition = new Definition();
        definition.setPrimitiveType(primitiveType);
        definition.setDefinitionText(text);
        //identifier
        definition.setIdentifier(RegexUtil.matcher(REGEX_IDENTIFIER, text));
        //range
        RegexUtil.matcherConsumer(REGEX_BITSTRING_SIZE, text, group -> {
            String[] range = group.replaceAll(Operator.LEFT_BRACKET, "")
                    .replaceAll(Operator.RIGHT_BRACKET, "")
                    .replaceAll(Reserved.SIZE, "")
                    .split(Operator.RANGE);
            definition.setRangeMin(range[0]);
            definition.setRangeMax(range[range.length - 1]);
        });
        //body
        definition.setSubBodyText(RegexUtil.matcherFunc(REGEX_BITSTRING_BODY, text, group -> {
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
        return definition;
    }
}
