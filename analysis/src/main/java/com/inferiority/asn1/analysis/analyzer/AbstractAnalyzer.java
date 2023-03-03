package com.inferiority.asn1.analysis.analyzer;

import com.inferiority.asn1.analysis.AnalysisException;
import com.inferiority.asn1.analysis.common.Operator;
import com.inferiority.asn1.analysis.common.Reserved;
import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.analysis.model.Module;

import java.util.List;

/**
 * @author cuijiufeng
 * @date 2023/2/26 12:10
 */
public abstract class AbstractAnalyzer {

    public static final String CRLF = "(\\s*)";

    public static final String CRLF_LEAST = "(\\s{1,})";

    //An "identifier" shall consist of an arbitrary number (one or more) of letters, digits, and hyphens. The initial character
    //shall be a lower-case letter. A hyphen shall not be the last character. A hyphen shall not be immediately followed by
    //another hyphen.
    public static final String REGEX_IDENTIFIER = "((?!.*-{2,})(?<!-{2,1024}.{0,1024})[A-Za-z][A-Za-z0-9-]*)";

    public static final String REGEX_NUM = "(0|-?[1-9][0-9]*)";

    public static final String REGEX_NUM_COMPOUND = "(" + REGEX_NUM + "|" + Reserved.MIN + "|" + Reserved.MAX + "|" + REGEX_IDENTIFIER + ")";

    public static final String REGEX_DEFINITION = REGEX_IDENTIFIER + CRLF + Operator.ASSIGNMENT + CRLF + REGEX_IDENTIFIER;

    public static AbstractAnalyzer getInstance(List<Module> modules, String typeReserved) throws AnalysisException {
        switch (typeReserved) {
            case Reserved.BOOLEAN: return BooleanAnalyzer.getInstance();
            case Reserved.INTEGER: return IntegerAnalyzer.getInstance();
            default: throw new AnalysisException("unsupported type: " + typeReserved);
        }
    }

    public abstract Definition parse(String primitiveType, String text, String moduleText) throws AnalysisException;
}
