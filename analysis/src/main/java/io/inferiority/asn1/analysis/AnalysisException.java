package io.inferiority.asn1.analysis;

/**
 * @author cuijiufeng
 * @date 2023/2/25 11:34
 */
public class AnalysisException extends RuntimeException {
    public AnalysisException(String message) {
        super(message);
    }

    public AnalysisException(String message, Throwable cause) {
        super(message, cause);
    }
}
