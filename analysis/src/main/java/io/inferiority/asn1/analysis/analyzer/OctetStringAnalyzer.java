package io.inferiority.asn1.analysis.analyzer;

import io.inferiority.asn1.analysis.AnalysisException;
import io.inferiority.asn1.analysis.common.Operator;
import io.inferiority.asn1.analysis.common.Reserved;
import io.inferiority.asn1.analysis.util.RegexUtil;

/**
 * @author cuijiufeng
 * @Class OctetStringAnalyzer
 * @Date 2023/3/9 17:07
 */
public class OctetStringAnalyzer extends AbstractStringAnalyzer {
    private static final OctetStringAnalyzer analyzer = new OctetStringAnalyzer();

    public static final String REGEX_OCTET_STRING_RANGE = "(" + Operator.LEFT_BRACKET + Reserved.SIZE + CRLF +
            Operator.LEFT_BRACKET + REGEX_NUM_COMPOUND + "(" + Operator.RANGE + REGEX_NUM_COMPOUND + ")?" + Operator.RIGHT_BRACKET +
            Operator.RIGHT_BRACKET + ")";

    public static final String REGEX_OCTET_STRING = REGEX_IDENTIFIER + CRLF + Operator.ASSIGNMENT + CRLF +
            "(" + REGEX_IDENTIFIER + "[ ]*)+" + CRLF + REGEX_OCTET_STRING_RANGE + "?" + CRLF;

    public static AbstractAnalyzer getInstance() {
        return analyzer;
    }

    @Override
    public String getRegexStringRange() {
        return REGEX_OCTET_STRING_RANGE;
    }

    @Override
    public void validate(String text) {
        if (!RegexUtil.matches(REGEX_OCTET_STRING, text)) {
            throw new AnalysisException("not a valid octet-string type definition.\n" + text);
        }
    }
}
