package com.inferiority.asn1.analysis.analyzer;

import com.inferiority.asn1.analysis.AnalysisException;
import com.inferiority.asn1.analysis.common.Operator;
import com.inferiority.asn1.analysis.common.Reserved;
import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.analysis.model.Module;
import com.inferiority.asn1.analysis.util.RegexUtil;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author cuijiufeng
 * @date 2023/2/26 12:10
 */
public abstract class AbstractAnalyzer {

    public static final String CRLF = "(\\s*)";

    public static final String CRLF_LEAST = "(\\s{1,})";

    //public static final String REGEX_IDENTIFIER = "((?!.*-{2,})(?<!-{2,1024}.{0,1024})[A-Za-z][A-Za-z0-9-]*)";
    public static final String REGEX_IDENTIFIER = "([A-Za-z][A-Za-z0-9-]*)";

    public static final String REGEX_NUM = "(0|-?[1-9][0-9]*)";

    public static final String REGEX_NUM_COMPOUND = "(" + REGEX_NUM + "|" + Reserved.MIN + "|" + Reserved.MAX + "|" + REGEX_IDENTIFIER + ")";

    public static final String REGEX_DEFINITION = REGEX_IDENTIFIER + CRLF + Operator.ASSIGNMENT + CRLF + "(" + REGEX_IDENTIFIER + "[ ]*)+";

    /**
     * @param modules 所有模块的定义
     * @param module 当前模块的类型定义
     * @param typeReserved
     * @return com.inferiority.asn1.analysis.analyzer.AbstractAnalyzer
     * @throws
    */
    public static AbstractAnalyzer getInstance(List<Module> modules, Module module, String typeReserved) throws AnalysisException {
        if (Reserved.BOOLEAN.equals(typeReserved)) {
            return BooleanAnalyzer.getInstance();
        } else if (Reserved.INTEGER.equals(typeReserved)) {
            return IntegerAnalyzer.getInstance();
        } else if (typeReserved.equals(Reserved.OCTET + " " + Reserved.STRING)) {
            return OctetStringAnalyzer.getInstance();
        } else if (Reserved.SEQUENCE.equals(typeReserved)) {
            return SequenceAnalyzer.getInstance();
        } else if (typeReserved.startsWith(Reserved.SEQUENCE + " " + Reserved.OF)
                || typeReserved.startsWith(Reserved.SEQUENCE + " " + Reserved.SIZE)) {
            return SequenceOfAnalyzer.getInstance();
        }
        //已知的定义类型
        //从当前模块中查找
        for (Definition def : module.getDefinitions()) {
            if (def.getIdentifier().equals(typeReserved)) {
                return getInstance(modules, module, def.getPrimitiveType());
            }
        }
        //从依赖模块中查找
        //TODO 2023/3/9 17:45
        throw new AnalysisException("unsupported type: " + typeReserved);
    }

    public abstract Definition parse(List<Module> modules, Module module, String primitiveType, String text, String moduleText) throws AnalysisException;

    public static String getPrimitiveType(String typeDef) {
        return RegexUtil.matcher(typeDef.indexOf(Operator.ASSIGNMENT), "(" + AbstractAnalyzer.REGEX_IDENTIFIER + "[ ]*)+", typeDef).trim();
    }

    public List<Map.Entry<String, String>> parseValues(String regex, String text, Function<String, AbstractMap.SimpleEntry<String, String>> apply) {
        List<Map.Entry<String, String>> values = new ArrayList<>(16);
        CharSequence t = text;
        while (t != null) {
            t = RegexUtil.matcherConsumerRet(regex, t, valueText -> values.add(apply.apply(valueText)));
        }
        return values.isEmpty() ? null : values;
    }
}
