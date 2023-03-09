package com.inferiority.asn1.analysis.analyzer;

import com.inferiority.asn1.analysis.AnalysisException;
import com.inferiority.asn1.analysis.common.Operator;
import com.inferiority.asn1.analysis.common.Reserved;
import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.analysis.util.RegexUtil;

/**
 * @author cuijiufeng
 * @Class SequenceAnalyzer
 * @Date 2023/3/9 17:46
 */
public class SequenceAnalyzer extends AbstractAnalyzer {
    private static final SequenceAnalyzer analyzer = new SequenceAnalyzer();

    public static final String REGEX_SEQUENCE_BODY = "(" + Operator.OPENING_BRACE + CRLF + "(" + REGEX_IDENTIFIER + CRLF_LEAST + "[\\S]+[,]?" + CRLF + ")+" +
            Operator.CLOSING_BRACE + ")";

    public static final String REGEX_SEQUENCE = CRLF + REGEX_IDENTIFIER + CRLF + Operator.ASSIGNMENT + CRLF + REGEX_IDENTIFIER + CRLF +
            REGEX_SEQUENCE_BODY + "?" + CRLF +
            "(" + Operator.LEFT_BRACKET + "[\\s\\S]*" + Operator.RIGHT_BRACKET + ")" + "?" + CRLF;

    public static AbstractAnalyzer getInstance() {
        return analyzer;
    }

    @Override
    public Definition parse(String primitiveType, String text, String moduleText) throws AnalysisException {
        //TODO 2023/3/9 18:20 REGEX_SEQUENCE 有可能是多层的定义
        if (!RegexUtil.matches(REGEX_SEQUENCE, text)) {
            throw new AnalysisException("not a valid sequence type definition.\n" + text);
        }
        String primitive = Reserved.SEQUENCE + " " + Reserved.OF;
        Definition definition = new Definition();
        definition.setPrimitiveType(primitive);
        definition.setDefinitionText(text);
        //identifier
        definition.setIdentifier(RegexUtil.matcher(REGEX_IDENTIFIER, text));
        return definition;
    }
}
