package com.inferiority.asn1.analysis;

import com.inferiority.asn1.analysis.common.Operator;

import java.io.InputStream;
import java.util.Scanner;

/**
 * @author cuijiufeng
 * @Class ReaderUtil
 * @Date 2023/2/24 16:34
 */
public class FileReader {
    private final Scanner reader;

    public FileReader(InputStream is) {
        this.reader = new Scanner(is);
    }

    public String nextValidLine() {
        while (reader.hasNext()) {
            String line = reader.nextLine().trim();
            if (0 == line.length()) {
                continue;
            }
            int noteIdx = line.indexOf(Operator.NOTE);
            if (-1 == noteIdx) {
                return line;
            } else if (0 == noteIdx) {
                continue;
            }
            return line.substring(0, noteIdx);
        }
        return null;
    }
}
