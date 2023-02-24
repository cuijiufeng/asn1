package com.inferiority.asn1.analysis;

import org.junit.Test;

import java.io.InputStream;

/**
 * @Class: AnalyzerTest
 * @Date: 2023/2/24 16:45
 * @author: cuijiufeng
 */
public class AnalyzerTest {

    @Test
    public void analyzer() {
        InputStream is = AnalyzerTest.class.getClassLoader().getResourceAsStream("test.asn");
        new Analyzer(is).analyzer();
    }
}