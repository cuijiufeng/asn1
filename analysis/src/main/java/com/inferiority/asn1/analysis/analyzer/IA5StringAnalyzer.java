package com.inferiority.asn1.analysis.analyzer;

import com.inferiority.asn1.analysis.AnalysisException;
import com.inferiority.asn1.analysis.common.Operator;
import com.inferiority.asn1.analysis.common.Reserved;
import com.inferiority.asn1.analysis.util.RegexUtil;

/**
 * @author cuijiufeng
 * @Class IA5StringAnalyzer
 * @Date 2023/3/20 12:36
 */
public class IA5StringAnalyzer extends AbstractStringAnalyzer {
    private static final IA5StringAnalyzer analyzer = new IA5StringAnalyzer();

    public static final String REGEX_IA5_STRING_RANGE = "(" + Operator.LEFT_BRACKET + Reserved.SIZE + CRLF +
            Operator.LEFT_BRACKET + REGEX_NUM_COMPOUND + "(" + Operator.RANGE + REGEX_NUM_COMPOUND + ")?" + Operator.RIGHT_BRACKET +
            Operator.RIGHT_BRACKET + ")";

    public static final String REGEX_IA5_STRING = REGEX_IDENTIFIER + CRLF + Operator.ASSIGNMENT + CRLF +
            "(" + REGEX_IDENTIFIER + "[ ]*)+" + CRLF + REGEX_IA5_STRING_RANGE + "?" + CRLF;

    public static AbstractAnalyzer getInstance() {
        return analyzer;
    }

    @Override
    public String getRegexStringRange() {
        return REGEX_IA5_STRING_RANGE;
    }

    @Override
    public void validate(String text) {
        if (!RegexUtil.matches(REGEX_IA5_STRING, text)) {
            throw new AnalysisException("not a valid ia5-string type definition.\n" + text);
        }
    }
}
