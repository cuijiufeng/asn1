package com.inferiority.asn1.analysis.analyzer;

import com.inferiority.asn1.analysis.AnalysisException;
import com.inferiority.asn1.analysis.common.Operator;
import com.inferiority.asn1.analysis.common.Reserved;
import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.analysis.model.Module;
import com.inferiority.asn1.analysis.util.RegexUtil;

import java.util.List;

/**
 * @author cuijiufeng
 * @Class OctetStringAnalyzer
 * @Date 2023/3/9 17:07
 */
public class OctetStringAnalyzer extends AbstractAnalyzer {
    private static final OctetStringAnalyzer analyzer = new OctetStringAnalyzer();

    public static final String REGEX_OCTET_STRING_RANGE = "(" + Operator.LEFT_BRACKET + Reserved.SIZE + Operator.LEFT_BRACKET +
            REGEX_NUM_COMPOUND + "(" + Operator.RANGE + REGEX_NUM_COMPOUND + ")?" +
            Operator.RIGHT_BRACKET + Operator.RIGHT_BRACKET + ")";

    public static final String REGEX_OCTET_STRING = REGEX_IDENTIFIER + CRLF + Operator.ASSIGNMENT + CRLF +
            "(" + REGEX_IDENTIFIER + "[ ]*)+" + CRLF + REGEX_OCTET_STRING_RANGE + "?" + CRLF;

    public static AbstractAnalyzer getInstance() {
        return analyzer;
    }

    @Override
    public Definition parse(List<Module> modules, Module module, String primitiveType, String text, String moduleText) throws AnalysisException {
        if (!RegexUtil.matches(REGEX_OCTET_STRING, text)) {
            throw new AnalysisException("not a valid octet-string type definition.\n" + text);
        }
        Definition definition = new Definition();
        definition.setPrimitiveType(primitiveType);
        definition.setDefinitionText(text);
        //identifier
        definition.setIdentifier(RegexUtil.matcher(REGEX_IDENTIFIER, text));
        //range
        RegexUtil.matcherConsumer(REGEX_OCTET_STRING_RANGE, text, group -> {
            String[] range = group.replaceAll(Operator.LEFT_BRACKET, "")
                    .replaceAll(Operator.RIGHT_BRACKET, "")
                    .replaceAll(Reserved.SIZE, "")
                    .split(Operator.RANGE);
            definition.setRangeMin(range[0]);
            definition.setRangeMax(range[range.length - 1]);
        });
        return definition;
    }
}
