package com.inferiority.asn1.analysis.analyzer;

import com.inferiority.asn1.analysis.AnalysisException;
import com.inferiority.asn1.analysis.common.Operator;
import com.inferiority.asn1.analysis.common.Reserved;
import com.inferiority.asn1.analysis.util.RegexUtil;

/**
 * @author cuijiufeng
 * @Class UTF8StringAnalyzer
 * @Date 2023/3/17 14:13
 */
public class UTF8StringAnalyzer extends AbstractStringAnalyzer {
    private static final UTF8StringAnalyzer analyzer = new UTF8StringAnalyzer();

    public static final String REGEX_UTF8STRING_RANGE = "(" + Operator.LEFT_BRACKET + Reserved.SIZE + CRLF +
            Operator.LEFT_BRACKET + REGEX_NUM_COMPOUND + "(" + Operator.RANGE + REGEX_NUM_COMPOUND + ")?" + Operator.RIGHT_BRACKET +
            Operator.RIGHT_BRACKET + ")";

    public static final String REGEX_UTF8STRING = REGEX_IDENTIFIER + CRLF + Operator.ASSIGNMENT + CRLF +
            REGEX_IDENTIFIER + CRLF + REGEX_UTF8STRING_RANGE + "?" + CRLF;

    public static AbstractAnalyzer getInstance() {
        return analyzer;
    }

    @Override
    public String getRegexStringRange() {
        return REGEX_UTF8STRING_RANGE;
    }

    @Override
    public void validate(String text) {
        if (!RegexUtil.matches(REGEX_UTF8STRING, text)) {
            throw new AnalysisException("not a valid utf8-string type definition.\n" + text);
        }
    }
}
