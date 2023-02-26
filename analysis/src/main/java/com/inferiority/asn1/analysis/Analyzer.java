package com.inferiority.asn1.analysis;

import com.inferiority.asn1.analysis.model.Namespace;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cuijiufeng
 * @Class Analyzer
 * @Date 2023/2/24 15:55
 */
public class Analyzer {
    private final InputStream is;

    public Analyzer(InputStream is) {
        this.is = is;
    }

    /**
     * 将asn1 oer编码文件解析为model对象
     * @return void
     * @throws
    */
    public void analyzer() throws AnalysisException {
        List<Namespace> namespaces = new ArrayList<>(16);
        String line = null;
        FileReader reader = new FileReader(this.is);
        while ((line = reader.nextValidLine()) != null) {
            System.out.println(line);
        }
    }
}
