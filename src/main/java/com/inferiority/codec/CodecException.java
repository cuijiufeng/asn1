package com.inferiority.codec;

import com.inferiority.codec.asn1.ASN1Object;

/**
 * @author cuijiufeng
 * @Class CodecException
 * @Date 2023/2/23 16:40
 */
public class CodecException extends Exception {

    public CodecException(String tag, ASN1Object object, Throwable cause) {
        super(String.format("%s %s type failed!\ncurrent position:\n%s",
                tag, object.getClass().getSimpleName(), object.toString()), cause);
    }
}
