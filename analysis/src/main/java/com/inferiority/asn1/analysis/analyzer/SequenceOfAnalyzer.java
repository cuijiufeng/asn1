package com.inferiority.asn1.analysis.analyzer;

import com.inferiority.asn1.analysis.AnalysisException;
import com.inferiority.asn1.analysis.common.Operator;
import com.inferiority.asn1.analysis.common.Reserved;
import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.analysis.util.RegexUtil;

/**
 * @author cuijiufeng
 * @Class SequenceOfAnalyzer
 * @Date 2023/3/9 16:16
 */
public class SequenceOfAnalyzer extends AbstractAnalyzer {
    private static final SequenceOfAnalyzer analyzer = new SequenceOfAnalyzer();

    public static final String REGEX_SEQUENCE_OF_RANGE = "(" + Reserved.SIZE + Operator.LEFT_BRACKET +
            "(" + REGEX_NUM_COMPOUND + Operator.RANGE + REGEX_NUM_COMPOUND + "|" + REGEX_NUM_COMPOUND + ")" +
            Operator.RIGHT_BRACKET + ")";

    public static final String REGEX_SEQUENCE_OF = CRLF + REGEX_IDENTIFIER + CRLF + Operator.ASSIGNMENT + CRLF +
            Reserved.SEQUENCE + " " + REGEX_SEQUENCE_OF_RANGE + "?" + CRLF + Reserved.OF + CRLF_LEAST + REGEX_IDENTIFIER + CRLF;

    public static AbstractAnalyzer getInstance() {
        return analyzer;
    }

    @Override
    public Definition parse(String primitiveType, String text, String moduleText) throws AnalysisException {
        if (!RegexUtil.matches(REGEX_SEQUENCE_OF, text)) {
            throw new AnalysisException("not a valid sequence-of type definition.\n" + text);
        }
        String primitive = Reserved.SEQUENCE + " " + Reserved.OF;
        Definition definition = new Definition();
        definition.setPrimitiveType(primitive);
        definition.setSequenceOf(primitiveType.replace(primitive, "").trim());
        definition.setDefinitionText(text);
        //identifier
        definition.setIdentifier(RegexUtil.matcher(REGEX_IDENTIFIER, text));
        //range
        RegexUtil.matcherConsumer(REGEX_SEQUENCE_OF_RANGE, text, group -> {
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
