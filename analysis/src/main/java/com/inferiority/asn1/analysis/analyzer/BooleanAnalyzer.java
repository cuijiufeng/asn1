package com.inferiority.asn1.analysis.analyzer;

import com.inferiority.asn1.analysis.AnalysisException;
import com.inferiority.asn1.analysis.common.Operator;
import com.inferiority.asn1.analysis.common.Reserved;
import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.analysis.util.RegexUtil;

import java.util.AbstractMap;
import java.util.ArrayList;
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
    public Definition parse(String primitiveType, String text, String moduleText) throws AnalysisException {
        if (!RegexUtil.matches(REGEX_BOOLEAN, text)) {
            throw new AnalysisException("not a valid boolean type definition.\n" + text);
        }
        String primitive = Reserved.SEQUENCE + " " + Reserved.OF;
        Definition definition = new Definition();
        definition.setPrimitiveType(primitive);
        definition.setDefinitionText(text);
        //identifier
        definition.setIdentifier(RegexUtil.matcher(REGEX_IDENTIFIER, text));
        //value
        CharSequence t = moduleText;
        List<AbstractMap.SimpleEntry<String, String>> values = new ArrayList<>(16);
        while (t != null) {
            t = RegexUtil.matcherConsumerRet(String.format(REGEX_BOOLEAN_VAL, definition.getIdentifier()), t, valueText -> {
                String[] split = valueText.split("\\s+");
                values.add(new AbstractMap.SimpleEntry<>(split[0], split[split.length - 1]));
            });
        }
        definition.setValues(values.isEmpty() ? null : values.toArray(new AbstractMap.SimpleEntry[0]));
        return definition;
    }
}
