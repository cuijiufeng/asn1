package com.inferiority.asn1.analysis.analyzer;

import com.inferiority.asn1.analysis.AnalysisException;
import com.inferiority.asn1.analysis.common.Operator;
import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.analysis.model.Module;
import com.inferiority.asn1.analysis.util.RegexUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cuijiufeng
 * @Class EnumeratedAnalyzer
 * @Date 2023/3/17 11:13
 */
public class EnumeratedAnalyzer extends AbstractAnalyzer {
    private static final EnumeratedAnalyzer analyzer = new EnumeratedAnalyzer();

    public static final String REGEX_ENUMERATED_BODY = "(" + Operator.OPENING_BRACE + CRLF +
            "((" + Operator.ELLIPSIS + Operator.COMMA + "?" + CRLF + ")|(" + REGEX_IDENTIFIER + "(" + Operator.COMMA + "?)" + CRLF + "))+" +
            CRLF + Operator.CLOSING_BRACE + ")";

    public static final String REGEX_ENUMERATED = CRLF + REGEX_IDENTIFIER + CRLF + Operator.ASSIGNMENT + CRLF + REGEX_IDENTIFIER + CRLF +
            REGEX_ENUMERATED_BODY + "?" + CRLF;

    public static AbstractAnalyzer getInstance() {
        return analyzer;
    }

    @Override
    public Definition parse(List<Module> modules, Module module, String primitiveType, List<Definition> parents, String text, String moduleText) throws AnalysisException {
        if (!RegexUtil.matches(REGEX_ENUMERATED, text)) {
            throw new AnalysisException("not a valid enumerated type definition.\n" + text);
        }
        Definition definition = new Definition();
        definition.setPrimitiveType(primitiveType);
        definition.setDefinitionText(text);
        //identifier
        definition.setIdentifier(RegexUtil.matcher(REGEX_IDENTIFIER, text));
        //body
        definition.setSubBodyText(RegexUtil.matcherFunc(REGEX_ENUMERATED_BODY, text, body -> {
            List<Definition> entries = Arrays.stream(body.replaceAll(Operator.OPENING_BRACE, "")
                    .replaceAll(Operator.CLOSING_BRACE, "")
                    .split(Operator.COMMA))
                    .map(s -> new Definition(s.trim(), null))
                    .collect(Collectors.toList());
            definition.setSubDefs(entries);
            return body;
        }));
        return definition;
    }
}
