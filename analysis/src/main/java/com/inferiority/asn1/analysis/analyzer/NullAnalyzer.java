package com.inferiority.asn1.analysis.analyzer;

import com.inferiority.asn1.analysis.AnalysisException;
import com.inferiority.asn1.analysis.common.Operator;
import com.inferiority.asn1.analysis.common.Reserved;
import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.analysis.model.Module;
import com.inferiority.asn1.analysis.util.RegexUtil;

import java.util.AbstractMap;
import java.util.List;

/**
 * @author cuijiufeng
 * @Class NullAnalyzer
 * @Date 2023/3/16 17:15
 */
public class NullAnalyzer extends AbstractAnalyzer {
    private static final NullAnalyzer analyzer = new NullAnalyzer();

    public static final String REGEX_NULL = CRLF + REGEX_IDENTIFIER + CRLF + Operator.ASSIGNMENT + CRLF + REGEX_IDENTIFIER + CRLF;

    public static final String REGEX_NULL_VAL = REGEX_IDENTIFIER + CRLF_LEAST + "%s" + CRLF + Operator.ASSIGNMENT + CRLF + Reserved.NULL + CRLF;

    public static AbstractAnalyzer getInstance() {
        return analyzer;
    }

    @Override
    protected Definition parseInternal(List<Module> modules, Module module, String primitiveType, List<Definition> parents, String text, String moduleText)
            throws AnalysisException {
        if (!RegexUtil.matches(REGEX_NULL, text)) {
            throw new AnalysisException("not a valid NULL type definition.\n" + text);
        }
        Definition definition = new Definition();
        definition.setPrimitiveType(primitiveType);
        definition.setDefinitionText(text);
        //identifier
        definition.setIdentifier(RegexUtil.matcher(REGEX_IDENTIFIER, text));
        //value
        definition.setValues(parseValues(String.format(REGEX_NULL_VAL, definition.getIdentifier()), moduleText, valueText ->
                new AbstractMap.SimpleEntry<>(valueText.split("\\s+")[0], valueText.split(Operator.ASSIGNMENT)[1].trim())
        ));
        return definition;
    }
}
