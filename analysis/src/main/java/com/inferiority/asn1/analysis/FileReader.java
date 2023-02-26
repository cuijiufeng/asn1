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
    private boolean multiComment = false;

    public FileReader(InputStream is) {
        this.reader = new Scanner(is);
    }

    public String nextValidLine() throws AnalysisException {
        while (reader.hasNext()) {
            //String line = reader.nextLine().trim();
            String line = reader.nextLine();
            //多行注释
            if (multiComment) {
                int multiCommentIdx = line.indexOf(Operator.MULTI_COMMENT_END);
                if (multiCommentIdx == -1) {
                    continue;
                }
                multiComment = false;
                line = line.substring(multiCommentIdx + Operator.MULTI_COMMENT_END.length());
            }
            //单行注释
            int singleCommentIdx = line.indexOf(Operator.COMMENT);
            //多行注释
            int multiCommentIdx = line.indexOf(Operator.MULTI_COMMENT_START);
            if (multiCommentIdx != -1 || singleCommentIdx != -1) {
                if (multiCommentIdx != -1 && singleCommentIdx != -1 && multiCommentIdx < singleCommentIdx || singleCommentIdx == -1) {
                    multiComment = true;
                    line = line.substring(0, multiCommentIdx);
                } else {
                    line = line.substring(0, singleCommentIdx);
                }
            }
            //空行
            if (0 == line.length()) {
                continue;
            }
            return line;
        }
        if (multiComment) {
            throw new AnalysisException("missing end of comment");
        }
        return null;
    }
}
