package com.inferiority.asn1.analysis.analyzer;

import com.inferiority.asn1.analysis.common.Operator;
import com.inferiority.asn1.analysis.common.Reserved;

import java.util.regex.Pattern;

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
    public static final String REGEX_IDENTIFIER = "(?!.*-{2,})(?<!-{2,1024}.{0,1024})[A-Za-z][A-Za-z0-9-]*";

    public static final Pattern PATTERN_IDENTIFIER = Pattern.compile(REGEX_IDENTIFIER);

    public static final String REGEX_BOOLEAN = CRLF + REGEX_IDENTIFIER + CRLF + Operator.ASSIGNMENT + CRLF + Reserved.BOOLEAN + CRLF;
    public static final Pattern PATTERN_BOOLEAN = Pattern.compile(REGEX_BOOLEAN);

    public static final String REGEX_INTEGER = CRLF + REGEX_IDENTIFIER + CRLF + Operator.ASSIGNMENT + CRLF + Reserved.INTEGER + CRLF + ;
    public static final Pattern PATTERN_INTEGER = Pattern.compile(REGEX_INTEGER);
}
