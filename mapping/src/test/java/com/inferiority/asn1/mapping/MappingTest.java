package com.inferiority.asn1.mapping;

import com.inferiority.asn1.analysis.AnalysisException;
import com.inferiority.asn1.analysis.Analyzer;
import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.analysis.model.Module;
import com.inferiority.asn1.mapping.model.MappingContext;
import javassist.NotFoundException;
import org.junit.Test;

import java.util.List;

/**
 * @Class: AnalyzerTest
 * @Date: 2023/2/24 16:45
 * @author: cuijiufeng
 */
public class MappingTest {

    @Test
    public void mapping() throws AnalysisException, NotFoundException {
        List<Module> modules = new Analyzer(MappingTest.class.getClassLoader().getResourceAsStream("test.asn")).analyzer();
        Mapping mapping = new Mapping();
        for (Module module : modules) {
            for (Definition definition : module.getDefinitions()) {
                mapping.mapping(new MappingContext(definition));
            }
        }
    }
}