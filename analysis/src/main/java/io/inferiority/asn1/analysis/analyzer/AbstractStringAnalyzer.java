package io.inferiority.asn1.analysis.analyzer;

import io.inferiority.asn1.analysis.AnalysisException;
import io.inferiority.asn1.analysis.common.Operator;
import io.inferiority.asn1.analysis.common.Reserved;
import io.inferiority.asn1.analysis.model.Definition;
import io.inferiority.asn1.analysis.model.Module;
import io.inferiority.asn1.analysis.util.RegexUtil;

import java.util.List;

/**
 * @author cuijiufeng
 * @Class AbstractStringAnalyzer
 * @Date 2023/3/20 12:00
 */
public abstract class AbstractStringAnalyzer extends AbstractAnalyzer {

    public abstract String getRegexStringRange();

    public abstract void validate(String text);

    @Override
    protected Definition parseInternal(List<Module> modules, Module module, String primitiveType, List<Definition> parents, String text, String moduleText)
            throws AnalysisException {
        validate(text);
        Definition definition = new Definition();
        definition.setPrimitiveType(primitiveType);
        definition.setDefinitionText(text);
        //identifier
        definition.setIdentifier(RegexUtil.matcher(REGEX_IDENTIFIER, text));
        //range
        RegexUtil.matcherConsumer(getRegexStringRange(), text, group -> {
            String[] range = group.replaceAll(Operator.LEFT_BRACKET, "")
                    .replaceAll(Operator.RIGHT_BRACKET, "")
                    .replaceAll(Reserved.SIZE, "")
                    .split(Operator.RANGE);
            definition.setRangeMin(range[0].trim());
            definition.setRangeMax(range[range.length - 1].trim());
        });
        return definition;
    }
}
