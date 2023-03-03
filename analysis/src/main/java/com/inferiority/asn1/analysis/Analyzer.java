package com.inferiority.asn1.analysis;

import com.inferiority.asn1.analysis.analyzer.ModuleAnalyzer;
import com.inferiority.asn1.analysis.model.Module;
import com.inferiority.asn1.analysis.util.RegexUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
        List<Module> modules = new ArrayList<>(16);
        String moduleText = null;
        while ((moduleText = nextModule(reader)) != null) {
            log.trace("model text:\n{}", moduleText);
            Module module = moduleAnalyzer.parse(modules, moduleText);
            modules.add(module);
            log.debug("model entity:\n{}", module);
        }
        return modules;
    }

    public String nextModule(FileReader reader) throws AnalysisException {
        String line = null;
        StringBuilder module = new StringBuilder();
        while ((line = reader.nextValidLine()) != null) {
            module.append(module.length() > 0 ? "\n" : "").append(line);
            if (RegexUtil.matches(ModuleAnalyzer.REGEX_MODULE, module.toString())) {
                return module.toString();
            }
        }
        return null;
    }
}
