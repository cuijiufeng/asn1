package com.inferiority.asn1.analysis.analyzer;

import com.inferiority.asn1.analysis.AnalysisException;
import com.inferiority.asn1.analysis.common.Operator;
import com.inferiority.asn1.analysis.common.Reserved;
import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.analysis.model.Module;
import com.inferiority.asn1.analysis.util.RegexUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cuijiufeng
 * @Class SequenceAnalyzer
 * @Date 2023/3/9 17:46
 */
public class SequenceAnalyzer extends AbstractAnalyzer {
    private static final SequenceAnalyzer analyzer = new SequenceAnalyzer();

    public static final String REGEX_SEQUENCE_CONSTRAINT = "(" + Operator.LEFT_BRACKET +
            Reserved.WITH + " " + Reserved.COMPONENTS + "[\\S\\s]*" + Operator.RIGHT_BRACKET + ")";

    public static final String REGEX_SEQUENCE_BODY = "(" + Operator.OPENING_BRACE + "[\\s\\S]*" + Operator.CLOSING_BRACE + ")";

    public static final String REGEX_SEQUENCE = CRLF + REGEX_IDENTIFIER + CRLF + Operator.ASSIGNMENT + CRLF + REGEX_IDENTIFIER + CRLF +
            REGEX_SEQUENCE_BODY + "?" + CRLF +
            REGEX_SEQUENCE_CONSTRAINT + "?" + CRLF;

    public static AbstractAnalyzer getInstance() {
        return analyzer;
    }

    @Override
    public Definition parse(List<Module> modules, Module module, String primitiveType, String text, String moduleText) throws AnalysisException {
        if (!RegexUtil.matches(REGEX_SEQUENCE, text)) {
            throw new AnalysisException("not a valid sequence type definition.\n" + text);
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
            text = text.replace(body, "");
        }
        //constraint
        String constraint = substringBody(text.indexOf(Operator.ASSIGNMENT), text.toCharArray(), new Character[]{'(', ')'});
        if (constraint != null) {
            // TODO: 2023/3/18 处理约束
            definition.setConstraintText(constraint);
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
            //optional
            Boolean optional = null;
            if (RegexUtil.matches(Reserved.OPTIONAL, s)) {
                optional = true;
                s = s.replace(Reserved.OPTIONAL, "").trim();
            }
            String defaulted = RegexUtil.matcher(Reserved.DEFAULT + CRLF_LEAST + "(\\S)*", s);
            if (defaulted != null) {
                s = s.replace(defaulted, "").trim();
                defaulted = defaulted.replace(Reserved.DEFAULT, "").trim();
            }
            String primitiveName = AbstractAnalyzer.getPrimitiveType(s);
            AbstractAnalyzer instance = AbstractAnalyzer.getInstance(modules, module, primitiveName);
            Definition definition = instance.parse(modules, module, primitiveName, s, null);
            definition.setOptional(optional);
            definition.setDefaulted(defaulted);
            subs.add(definition);
        }
        return subs.isEmpty() ? null : subs;
    }
}
