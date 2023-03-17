package com.inferiority.asn1.analysis.analyzer;

import com.inferiority.asn1.analysis.AnalysisException;
import com.inferiority.asn1.analysis.common.Operator;
import com.inferiority.asn1.analysis.common.Reserved;
import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.analysis.model.Module;
import com.inferiority.asn1.analysis.util.RegexUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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
            "(" + Operator.LEFT_BRACKET + "[\\s\\S]*" + Operator.RIGHT_BRACKET + ")" + "?" + CRLF;

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
        //约束
        //TODO 2023/3/17 17:19
        //RegexUtil.matcherFunc(REGEX_SEQUENCE_CONSTRAINT, text, str -> {
        //    return str;
        //})
        //definition.setConstraintText();
        //body
        definition.setSubBodyText(RegexUtil.matcherFunc(REGEX_SEQUENCE_BODY, text, body -> {
            definition.setSubDefs(parseBody(modules, module, substringBody(body.toCharArray())));
            return body;
        }));
        return definition;
    }

    private List<Definition> parseBody(List<Module> modules, Module module, String body) {
        List<Definition> subs = new ArrayList<>();
        List<String> split = splitBody(body);
        for (String s : split) {
            if (RegexUtil.matches(Operator.ELLIPSIS, s)) {
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

    private List<String> splitBody(String body) {
        List<String> split = new ArrayList<>(8);
        Stack<Integer> stack = new Stack<>();
        for (int i = 0, j = body.indexOf(Operator.COMMA); i != -1; i = j, j = body.indexOf(Operator.COMMA, i + 1)) {
            String s = body.substring(i + 1, j == -1 ? body.length() : j);
            if (RegexUtil.matches(Operator.OPENING_BRACE, s)) {
                stack.push(i + 1);
                continue;
            } else if (RegexUtil.matches(Operator.CLOSING_BRACE, s)) {
                s = body.substring(stack.pop(), j == -1 ? body.length() : j);
            } else if (!stack.isEmpty()) {
                continue;
            }
            split.add(s.trim());
        }
        return split;
    }
}
