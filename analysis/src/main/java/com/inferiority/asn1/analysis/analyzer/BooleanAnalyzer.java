package com.inferiority.asn1.analysis.analyzer;

import com.inferiority.asn1.analysis.AnalysisException;
import com.inferiority.asn1.analysis.common.Operator;
import com.inferiority.asn1.analysis.common.Reserved;
import com.inferiority.asn1.analysis.model.Definition;

/**
 * @author cuijiufeng
 * @Class BooleanAnalyzer
 * @Date 2023/2/28 13:33
 */
public class BooleanAnalyzer extends AbstractAnalyzer {
    private static final BooleanAnalyzer analyzer = new BooleanAnalyzer();

    public static final String REGEX_BOOLEAN = CRLF + REGEX_IDENTIFIER + CRLF + Operator.ASSIGNMENT + CRLF + Reserved.BOOLEAN + CRLF;

    public static AbstractAnalyzer getInstance() {
        return analyzer;
    }

    @Override
    public Definition parse(String primitiveType, String text, String moduleText) throws AnalysisException {
        return null;
    }
}
