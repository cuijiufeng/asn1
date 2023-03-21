package com.inferiority.asn1.analysis;

import com.inferiority.asn1.analysis.analyzer.AbstractAnalyzer;
import com.inferiority.asn1.analysis.analyzer.ModuleAnalyzer;
import com.inferiority.asn1.analysis.analyzer.UnknownAnalyzer;
import com.inferiority.asn1.analysis.model.CircleDependency;
import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.analysis.model.Module;
import com.inferiority.asn1.analysis.util.ReflectUtil;
import com.inferiority.asn1.analysis.util.RegexUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author cuijiufeng
 * @Class Analyzer
 * @Date 2023/2/24 15:55
 */
@Slf4j
public class Analyzer {
    private final InputStream is;
    private final ModuleAnalyzer moduleAnalyzer = new ModuleAnalyzer();

    public Analyzer(InputStream is) {
        this.is = is;
    }

    /**
     * 将asn1 oer编码文件解析为model对象
     * @return void
     * @throws
    */
    public List<Module> analyzer() throws AnalysisException {
        FileReader reader = new FileReader(this.is);
        List<Module> modules = new LinkedList<>();
        String moduleText = null;
        while ((moduleText = nextModule(reader)) != null) {
            log.trace("model text:\n{}", moduleText);
            modules.add(moduleAnalyzer.parse(modules, moduleText));
            log.trace("model entity:\n{}", modules.get(modules.size() - 1));

            for (int i = 0; i < UnknownAnalyzer.UNKNOWN_CACHE.size(); i++) {
                CircleDependency cd = UnknownAnalyzer.UNKNOWN_CACHE.get(i);
                try {
                    Object[] parseArgs = cd.getArgs();
                    Map.Entry<AbstractAnalyzer, List<Definition>> entry = ReflectUtil.invokeMethod(AbstractAnalyzer.METHOD_GET_INSTANCE, null,
                            parseArgs[0], parseArgs[1], parseArgs[2]);
                    if (entry.getKey() instanceof UnknownAnalyzer) {
                        continue;
                    }
                    parseArgs[3] = entry.getValue();
                    Definition definition = ReflectUtil.invokeMethod(AbstractAnalyzer.METHOD_PARSE, entry.getKey(), parseArgs);
                    log.debug("callback parse circle dependency: {}", definition);
                    ReflectUtil.deepCopyBean(Definition.class, definition, cd.getRet());
                    UnknownAnalyzer.UNKNOWN_CACHE.remove(cd);
                    i--;
                } catch (ReflectiveOperationException e) {
                    throw new AnalysisException("reflect invoke method error", e);
                }
            }
        }
        if (!UnknownAnalyzer.UNKNOWN_CACHE.isEmpty()) {
            StringBuilder sb = new StringBuilder("unsupported types\n");
            for (CircleDependency cd : UnknownAnalyzer.UNKNOWN_CACHE) {
                Object[] args = cd.getArgs();
                sb.append(String.format("%s in module %s ", args[2], ReflectUtil.cast(Module.class, args[1]).getIdentifier()))
                        .append('\n');
            }
            throw new AnalysisException(sb.toString());
        }
        return modules;
    }

    private String nextModule(FileReader reader) throws AnalysisException {
        String line = null;
        StringBuilder module = new StringBuilder();
        while ((line = reader.nextValidLine()) != null) {
            module.append(module.length() > 0 ? "\n" : "").append(line);
            if (RegexUtil.matches(ModuleAnalyzer.REGEX_MODULE, module.toString())) {
                return module.toString();
            }
        }
        if (module.length() != 0) {
            throw new AnalysisException("module definition syntax error");
        }
        return null;
    }
}
