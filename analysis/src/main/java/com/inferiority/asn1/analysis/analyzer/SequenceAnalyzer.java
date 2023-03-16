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

    public static final String REGEX_SEQUENCE_BODY = "(" + Operator.OPENING_BRACE + CRLF +
            "(" + Operator.ELLIPSIS + "|" + REGEX_IDENTIFIER + CRLF_LEAST + "[\\S]+[,]?" + CRLF + ")+" +
            Operator.CLOSING_BRACE + ")";

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
        definition.setPrimitiveType(Reserved.SEQUENCE);
        definition.setDefinitionText(text);
        //identifier
        definition.setIdentifier(RegexUtil.matcher(REGEX_IDENTIFIER, text));
        //body
        String body = text.substring(text.indexOf("{") + 1, text.lastIndexOf("}"));
        definition.setSubBodyText(body);
        definition.setSubDefs(parseBody(modules, module, body));
        return definition;
    }

    private List<Definition> parseBody(List<Module> modules, Module module, String body) {
        List<Definition> subs = new ArrayList<>();
        Stack<StringBuilder> stack = new Stack<>();
        String[] split = body.split(Operator.COMMA);
        for (String s : split) {
            if (RegexUtil.matches(Operator.OPENING_BRACE, s)) {
                stack.push(new StringBuilder(s));
            } else if (RegexUtil.matches(Operator.CLOSING_BRACE, s)) {
                StringBuilder def = stack.pop();
                //TODO 2023/3/16 16:37
            }
            if (!stack.isEmpty()) {
                stack.peek().append(s);
                continue;
            }
            if (RegexUtil.matches(Operator.ELLIPSIS, s)) {
                subs.add(new Definition(null, s.trim()));
                continue;
            }
            s = s.trim().replaceFirst("[ ]+", Operator.ASSIGNMENT);
            String primitiveName = AbstractAnalyzer.getPrimitiveType(s);
            AbstractAnalyzer instance = AbstractAnalyzer.getInstance(modules, module, primitiveName);
            subs.add(instance.parse(modules, module, primitiveName, s, null));
        }
        return subs.isEmpty() ? null : subs;
    }
}
