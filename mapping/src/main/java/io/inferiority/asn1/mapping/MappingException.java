package io.inferiority.asn1.mapping;

/**
 * @author cuijiufeng
 * @date 2023/2/25 11:34
 */
public class MappingException extends RuntimeException {
    public MappingException(String message) {
        super(message);
    }

    public MappingException(String message, Throwable cause) {
        super(message, cause);
    }
}
