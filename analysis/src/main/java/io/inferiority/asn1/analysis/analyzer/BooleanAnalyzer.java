package io.inferiority.asn1.analysis.analyzer;

import io.inferiority.asn1.analysis.AnalysisException;
import io.inferiority.asn1.analysis.common.Operator;
import io.inferiority.asn1.analysis.common.Reserved;
import io.inferiority.asn1.analysis.model.Definition;
import io.inferiority.asn1.analysis.model.Module;
import io.inferiority.asn1.analysis.util.RegexUtil;

import java.util.AbstractMap;
import java.util.List;

/**
 * @author cuijiufeng
 * @Class BooleanAnalyzer
 * @Date 2023/2/28 13:33
 */
public class BooleanAnalyzer extends AbstractAnalyzer {
    private static final BooleanAnalyzer analyzer = new BooleanAnalyzer();

    public static final String REGEX_BOOLEAN = REGEX_IDENTIFIER + CRLF + Operator.ASSIGNMENT + CRLF + Reserved.BOOLEAN + CRLF;

    public static final String REGEX_BOOLEAN_VAL = REGEX_IDENTIFIER + CRLF_LEAST + "%s" + CRLF + Operator.ASSIGNMENT + CRLF +
            "(" + Reserved.TRUE + "|" + Reserved.FALSE + ")" + CRLF;

    public static AbstractAnalyzer getInstance() {
        return analyzer;
    }

    @Override
    protected Definition parseInternal(List<Module> modules, Module module, String primitiveType, List<Definition> parents, String text, String moduleText)
            throws AnalysisException {
        if (!RegexUtil.matches(REGEX_BOOLEAN, text)) {
            throw new AnalysisException("not a valid boolean type definition.\n" + text);
        }
        Definition definition = new Definition();
        definition.setPrimitiveType(primitiveType);
        definition.setDefinitionText(text);
        //identifier
        definition.setIdentifier(RegexUtil.matcher(REGEX_IDENTIFIER, text));
        //value
        definition.setValues(parseValues(String.format(REGEX_BOOLEAN_VAL, definition.getIdentifier()), moduleText, valueText ->
            new AbstractMap.SimpleEntry<>(valueText.split("\\s+")[0], valueText.split(Operator.ASSIGNMENT)[1].trim())
        ));
        return definition;
    }
}
