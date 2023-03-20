package com.inferiority.asn1.analysis.analyzer;

import com.inferiority.asn1.analysis.AnalysisException;
import com.inferiority.asn1.analysis.common.Operator;
import com.inferiority.asn1.analysis.model.CircleDependency;
import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.analysis.model.Module;
import com.inferiority.asn1.analysis.util.AopUtil;
import com.inferiority.asn1.analysis.util.ReflectUtil;
import com.inferiority.asn1.analysis.util.RegexUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * @author cuijiufeng
 * @date 2023/3/18 14:55
 */
public class UnknownAnalyzer extends AbstractAnalyzer {
    public static final UnknownAnalyzer PROXY_OBJECT;
    public static final List<CircleDependency> UNKNOWN_CACHE = new LinkedList<>();

    public static final String REGEX_UNKNOWN = REGEX_IDENTIFIER + CRLF + Operator.ASSIGNMENT + CRLF + "[\\s\\S]*" + CRLF;

    static {
        PROXY_OBJECT = AopUtil.proxyObject(UnknownAnalyzer.class, "parse", (obj, method, args, proxy) -> {
            Object ret = proxy.invokeSuper(obj, args);
            UNKNOWN_CACHE.add(new CircleDependency(args, ReflectUtil.cast(Definition.class, ret)));
            return ret;
        });
    }

    @Override
    public Definition parse(List<Module> modules, Module module, String primitiveType, List<Definition> parents, String text, String moduleText) throws AnalysisException {
        if (!RegexUtil.matches(REGEX_UNKNOWN, text)) {
            throw new AnalysisException("not a valid type definition.\n" + text);
        }
        Definition definition = new Definition();
        definition.setPrimitiveType(primitiveType);
        definition.setDefinitionText(text);
        //identifier
        definition.setIdentifier(RegexUtil.matcher(REGEX_IDENTIFIER, text));
        return definition;
    }
}
