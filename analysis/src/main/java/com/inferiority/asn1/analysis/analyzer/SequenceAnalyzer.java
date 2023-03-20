package com.inferiority.asn1.analysis.analyzer;

import com.inferiority.asn1.analysis.AnalysisException;
import com.inferiority.asn1.analysis.common.Operator;
import com.inferiority.asn1.analysis.common.Reserved;
import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.analysis.model.Module;
import com.inferiority.asn1.analysis.util.RegexUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * @author cuijiufeng
 * @Class SequenceAnalyzer
 * @Date 2023/3/9 17:46
 */
public class SequenceAnalyzer extends AbstractAnalyzer {
    private static final SequenceAnalyzer analyzer = new SequenceAnalyzer();

    public static final String REGEX_SEQUENCE_PARAM = "(" + Operator.OPENING_BRACE +
            "(" + REGEX_IDENTIFIER + Operator.COMMA + "?" + CRLF + ")+" + Operator.CLOSING_BRACE + ")";

    public static final String REGEX_SEQUENCE_CONSTRAINT = "(" + Operator.LEFT_BRACKET +
            Reserved.WITH + " " + Reserved.COMPONENTS + "[\\S\\s]*" + Operator.RIGHT_BRACKET + ")";

    public static final String REGEX_SEQUENCE_BODY = "(" + Operator.OPENING_BRACE + "[\\s\\S]*" + Operator.CLOSING_BRACE + ")";

    public static final String REGEX_SEQUENCE = CRLF + REGEX_IDENTIFIER + CRLF +
            REGEX_SEQUENCE_PARAM + "?" + CRLF +
            Operator.ASSIGNMENT + CRLF +
            REGEX_IDENTIFIER + CRLF +
            REGEX_SEQUENCE_BODY + "?" + CRLF +
            REGEX_SEQUENCE_CONSTRAINT + "?" + CRLF;

    public static AbstractAnalyzer getInstance() {
        return analyzer;
    }

    @Override
    public Definition parse(List<Module> modules, Module module, String primitiveType, List<Definition> parents, String text, String moduleText) throws AnalysisException {
        if (!RegexUtil.matches(REGEX_SEQUENCE, text)) {
            throw new AnalysisException("not a valid sequence type definition.\n" + text);
        }
        Definition definition = new Definition();
        definition.setPrimitiveType(primitiveType);
        definition.setDefinitionText(text);
        //identifier
        definition.setIdentifier(RegexUtil.matcher(REGEX_IDENTIFIER, text));
        //parameter
        definition.setSequenceParameters(RegexUtil.matcherFunc(
                REGEX_SEQUENCE_PARAM,
                text.substring(0, text.indexOf(Operator.ASSIGNMENT)),
                param -> Arrays.stream(param.replaceAll(Operator.OPENING_BRACE, "")
                    .replaceAll(Operator.CLOSING_BRACE, "")
                    .split(Operator.COMMA))
                .map(String::trim)
                .collect(Collectors.toList())));
        //body
        String body = substringBody(text.indexOf(Operator.ASSIGNMENT), text.toCharArray(), new Character[]{'{', '}'});
        definition.setSubBodyText(body);
        //非参数化类型定义
        if (body != null && !definition.isParameterizedTypes() && (parents.isEmpty() || !parents.get(0).isParameterizedTypes())) {
            definition.setSubDefs(parseBody(modules, module, body.substring(1, body.length() - 1)));
        }
        //constraint
        String constraint = substringBody(text.indexOf(Operator.ASSIGNMENT), text.toCharArray(), new Character[]{'(', ')'});
        definition.setConstraintText(constraint);
        if (constraint != null) {
            // TODO: 2023/3/18 处理约束
        }
        return definition;
    }

    private List<Definition> parseBody(List<Module> modules, Module module, String body) {
        List<Definition> subs = new ArrayList<>();
        List<String> split = splitBody(body.toCharArray(), ',');
        for (String s : split) {
            String optional = null;
            String defaulted = null;
            if (Operator.NO_REG_ELLIPSIS.equals(s)) {
                subs.add(new Definition(s.trim(), null));
                continue;
            }
            s = s.trim().replaceFirst("[ ]+", Operator.ASSIGNMENT);
            if ((optional = RegexUtil.matcher(Reserved.OPTIONAL, s)) != null) {
                if (inBrace(s.indexOf(optional), s.toCharArray())) {
                    s = s.replace(Reserved.OPTIONAL, "").trim();
                }
            }
            if ((defaulted = RegexUtil.matcher(Reserved.DEFAULT + CRLF_LEAST + "[^" + Operator.COMMA + "\\s]+", s)) != null) {
                if (inBrace(s.indexOf(defaulted), s.toCharArray())) {
                    s = s.replace(defaulted, "").trim();
                }
            }
            String primitiveName = AbstractAnalyzer.getPrimitiveType(s);
            Map.Entry<AbstractAnalyzer, List<Definition>> entry = AbstractAnalyzer.getInstance(modules, module, primitiveName);
            Definition definition = entry.getKey().parse(modules, module, primitiveName, entry.getValue(), s, null);
            definition.setOptional(optional);
            definition.setDefaulted(defaulted);
            subs.add(definition);
        }
        return subs.isEmpty() ? null : subs;
    }

    private boolean inBrace(int pos, char[] chars) {
        Stack<Character> stack = new Stack<>();
        for (int i = 0; i < pos; i++) {
            if (chars[i] == '{') {
                stack.push(chars[i]);
            } else if (chars[i] == '}') {
                stack.pop();
            }
        }
        return stack.isEmpty();
    }
}
