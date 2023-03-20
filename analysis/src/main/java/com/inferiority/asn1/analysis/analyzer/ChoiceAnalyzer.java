package com.inferiority.asn1.analysis.analyzer;

import com.inferiority.asn1.analysis.AnalysisException;
import com.inferiority.asn1.analysis.common.Operator;
import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.analysis.model.Module;
import com.inferiority.asn1.analysis.util.RegexUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cuijiufeng
 * @Class ChoiceAnalyzer
 * @Date 2023/3/17 10:30
 */
public class ChoiceAnalyzer extends AbstractAnalyzer {
    private static final ChoiceAnalyzer analyzer = new ChoiceAnalyzer();

    public static final String REGEX_CHOICE_BODY = "(" + Operator.OPENING_BRACE + "[\\s\\S]*" + Operator.CLOSING_BRACE + ")";

    public static final String REGEX_CHOICE = CRLF + REGEX_IDENTIFIER + CRLF + Operator.ASSIGNMENT + CRLF + REGEX_IDENTIFIER + CRLF +
            REGEX_CHOICE_BODY + "?" + CRLF +
            "(" + Operator.LEFT_BRACKET + "[\\s\\S]*" + Operator.RIGHT_BRACKET + ")" + "?" + CRLF;

    public static AbstractAnalyzer getInstance() {
        return analyzer;
    }

    @Override
    public Definition parse(List<Module> modules, Module module, String primitiveType, String text, String moduleText) throws AnalysisException {
        if (!RegexUtil.matches(REGEX_CHOICE, text)) {
            throw new AnalysisException("not a valid choice type definition.\n" + text);
        }
        Definition definition = new Definition();
        definition.setPrimitiveType(primitiveType);
        definition.setDefinitionText(text);
        //identifier
        definition.setIdentifier(RegexUtil.matcher(REGEX_IDENTIFIER, text));
        //body
        String body = substringBody(text.indexOf(Operator.ASSIGNMENT), text.toCharArray(), new Character[]{'{', '}'});
        if (body != null) {
            definition.setSubBodyText(body);
            definition.setSubDefs(parseBody(modules, module, body.substring(1, body.length() - 1)));
        }
        return definition;
    }

    private List<Definition> parseBody(List<Module> modules, Module module, String body) {
        List<Definition> subs = new ArrayList<>();
        List<String> split = splitBody(body.toCharArray(), ',');
        for (String s : split) {
            if (Operator.NO_REG_ELLIPSIS.equals(s)) {
                subs.add(new Definition(s.trim(), null));
                continue;
            }
            s = s.trim().replaceFirst("[ ]+", Operator.ASSIGNMENT);
            String primitiveName = AbstractAnalyzer.getPrimitiveType(s);
            AbstractAnalyzer instance = AbstractAnalyzer.getInstance(modules, module, primitiveName, false);
            subs.add(instance.parse(modules, module, primitiveName, s, null));
        }
        return subs.isEmpty() ? null : subs;
    }
}
