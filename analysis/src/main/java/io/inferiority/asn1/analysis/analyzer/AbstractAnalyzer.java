package io.inferiority.asn1.analysis.analyzer;

import io.inferiority.asn1.analysis.AnalysisException;
import io.inferiority.asn1.analysis.common.Operator;
import io.inferiority.asn1.analysis.common.Reserved;
import io.inferiority.asn1.analysis.model.Definition;
import io.inferiority.asn1.analysis.model.Module;
import io.inferiority.asn1.analysis.util.ArrayUtil;
import io.inferiority.asn1.analysis.util.RegexUtil;

import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Function;

/**
 * @author cuijiufeng
 * @date 2023/2/26 12:10
 */
public abstract class AbstractAnalyzer {
    public static final Character[] BRACES = new Character[]{'(', ')', '[', ']', '{', '}', '<', '>'};
    public static final Method METHOD_GET_INSTANCE;
    public static final Method METHOD_PARSE;

    public static final String CRLF = "(\\s*)";

    public static final String CRLF_LEAST = "(\\s{1,})";

    //public static final String REGEX_IDENTIFIER = "((?!.*-{2,})(?<!-{2,1024}.{0,1024})[A-Za-z][A-Za-z0-9-]*)";
    public static final String REGEX_IDENTIFIER = "([A-Za-z][A-Za-z0-9-]*)";

    public static final String REGEX_NUM = "(0|-?[1-9][0-9]*)";

    public static final String REGEX_NUM_COMPOUND = "(" + REGEX_NUM + "|" + Reserved.MIN + "|" + Reserved.MAX + "|" + REGEX_IDENTIFIER + ")";

    public static final String REGEX_DEFINITION = REGEX_IDENTIFIER + CRLF +
            "(" + Operator.OPENING_BRACE + "(" + REGEX_IDENTIFIER + Operator.COMMA + "?" + CRLF + ")+" + Operator.CLOSING_BRACE + ")?" + CRLF +
            Operator.ASSIGNMENT + CRLF + "(" + REGEX_IDENTIFIER + "[ ]*)+";

    static {
        try {
            METHOD_GET_INSTANCE = AbstractAnalyzer.class.getDeclaredMethod("getInstance", List.class, Module.class, String.class);
            METHOD_PARSE = AbstractAnalyzer.class.getDeclaredMethod("parse", List.class, Module.class, String.class, List.class, String.class, String.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * @param modules 所有模块的定义
     * @param module 当前模块的类型定义
     * @param typeReserved
     * @return com.inferiority.asn1.analysis.analyzer.AbstractAnalyzer
     * @throws
    */
    public static Map.Entry<AbstractAnalyzer, List<Definition>> getInstance(List<Module> modules, Module module, String typeReserved) throws AnalysisException {
        if (Reserved.NULL.equals(typeReserved)) {
            return new AbstractMap.SimpleEntry<>(NullAnalyzer.getInstance(), new ArrayList<>());
        } else if (Reserved.BOOLEAN.equals(typeReserved)) {
            return new AbstractMap.SimpleEntry<>(BooleanAnalyzer.getInstance(), new ArrayList<>());
        } else if (Reserved.INTEGER.equals(typeReserved)) {
            return new AbstractMap.SimpleEntry<>(IntegerAnalyzer.getInstance(), new ArrayList<>());
        } else if (Reserved.ENUMERATED.equals(typeReserved)) {
            return new AbstractMap.SimpleEntry<>(EnumeratedAnalyzer.getInstance(), new ArrayList<>());
        } else if (Reserved.IA5String.equals(typeReserved)) {
            return new AbstractMap.SimpleEntry<>(IA5StringAnalyzer.getInstance(), new ArrayList<>());
        } else if (Reserved.UTF8String.equals(typeReserved)) {
            return new AbstractMap.SimpleEntry<>(UTF8StringAnalyzer.getInstance(), new ArrayList<>());
        } else if (Reserved.SEQUENCE.equals(typeReserved)) {
            return new AbstractMap.SimpleEntry<>(SequenceAnalyzer.getInstance(), new ArrayList<>());
        } else if (Reserved.CHOICE.equals(typeReserved)) {
            return new AbstractMap.SimpleEntry<>(ChoiceAnalyzer.getInstance(), new ArrayList<>());
        } else if (typeReserved.equals(Reserved.BIT + " " + Reserved.STRING)) {
            return new AbstractMap.SimpleEntry<>(BitStringAnalyzer.getInstance(), new ArrayList<>());
        } else if (typeReserved.equals(Reserved.OCTET + " " + Reserved.STRING)) {
            return new AbstractMap.SimpleEntry<>(OctetStringAnalyzer.getInstance(), new ArrayList<>());
        } else if (RegexUtil.matches(Reserved.SEQUENCE + "\\s*" + Reserved.OF, typeReserved)) {
            if (getInstance(modules, module, typeReserved.split("\\s+", 3)[2]).getKey() instanceof UnknownAnalyzer) {
                throw new AnalysisException(String.format("unsupported type %s in module %s ", typeReserved, module.getIdentifier()));
            }
            return new AbstractMap.SimpleEntry<>(SequenceOfAnalyzer.getInstance(), new ArrayList<>());
        }
        //已知的定义类型
        //从当前模块中查找
        for (Definition def : module.getDefinitions()) {
            if (def.getIdentifier().equals(typeReserved)) {
                Map.Entry<AbstractAnalyzer, List<Definition>> entry = getInstance(modules, module, def.getPrimitiveType());
                entry.getValue().add(0, def);
                return entry;
            }
        }
        //从依赖模块中查找
        for (int i = 0; module.getImports() != null && i < module.getImports().size(); i++) {
            Map.Entry<String[], String> entry = module.getImports().get(i);
            if (ArrayUtil.contains(entry.getKey(), typeReserved)) {
                for (Module m : modules) {
                    if (m.getIdentifier().equals(entry.getValue())) {
                        for (Definition def : m.getDefinitions()) {
                            if (def.getIdentifier().equals(typeReserved)) {
                                Map.Entry<AbstractAnalyzer, List<Definition>> listEntry = getInstance(modules, m, def.getPrimitiveType());
                                listEntry.getValue().add(0, def);
                                return listEntry;
                            }
                        }
                        throw new AnalysisException(String.format("type %s not found in module %s", typeReserved, m.getIdentifier()));
                    }
                }
            }
        }
        return new AbstractMap.SimpleEntry<>(UnknownAnalyzer.PROXY_OBJECT, new ArrayList<>());
    }

    public Definition parse(List<Module> modules, Module module, String primitiveType, List<Definition> parents, String text, String moduleText)
            throws AnalysisException {
        Definition definition = parseInternal(modules, module, primitiveType, parents, text, moduleText);
        definition.setDependencies(parents);
        definition.setModule(module);
        return definition;
    }

    protected abstract Definition parseInternal(List<Module> modules, Module module, String primitiveType, List<Definition> parents, String text, String moduleText)
            throws AnalysisException;

    public static String getPrimitiveType(String typeDef) {
        String matcher = RegexUtil.matcher(typeDef.indexOf(Operator.ASSIGNMENT), "(" + AbstractAnalyzer.REGEX_IDENTIFIER + "\\s*)+", typeDef).trim();
        if (RegexUtil.matches(Reserved.SEQUENCE + "\\s*" + Reserved.SIZE, matcher)) {
            typeDef = typeDef.replaceFirst(SequenceOfAnalyzer.REGEX_SEQUENCE_OF_RANGE, "");
            matcher = RegexUtil.matcher(typeDef.indexOf(Operator.ASSIGNMENT), "(" + AbstractAnalyzer.REGEX_IDENTIFIER + "\\s*)+", typeDef).trim();
        }
        return matcher;
    }

    public String substringBody(int start, char[] body, Character[] brace) {
        Stack<Integer> stack = new Stack<>();
        for (int i = start; i < body.length; i++) {
            if (stack.isEmpty() && ArrayUtil.contains(BRACES, body[i]) && !ArrayUtil.contains(brace, body[i])) {
                return null;
            } else if (body[i] == brace[0]) {
                stack.push(i);
            } else if (body[i] == brace[1]) {
                Integer t = stack.pop();
                if (stack.isEmpty()) {
                    return new String(Arrays.copyOfRange(body, t, i + 1));
                }
            }
        }
        return null;
    }

    public List<String> splitBody(char[] body, Character separator) {
        Stack<Integer> stack = new Stack<>();
        List<String> split = new ArrayList<>(8);
        int s = 0;
        for (int i = 0; i < body.length; i++) {
            if ('{' == body[i]) {
                stack.push(i);
            } else if ('}' == body[i]) {
                stack.pop();
            }
            if (stack.isEmpty() && separator == body[i]) {
                split.add(new String(Arrays.copyOfRange(body, s, i)).trim());
                s = i + 1;
            }
        }
        split.add(new String(Arrays.copyOfRange(body, s, body.length)).trim());
        return split;
    }

    public List<Map.Entry<String, String>> parseValues(String regex, String text, Function<String, AbstractMap.SimpleEntry<String, String>> apply) {
        if (text == null) {
            return null;
        }
        List<Map.Entry<String, String>> values = new ArrayList<>(16);
        while (RegexUtil.matches(regex, text)) {
            text = RegexUtil.matcherReplaceConsumer(regex, text, valueText -> values.add(apply.apply(valueText)));
        }
        return values.isEmpty() ? null : values;
    }
}
