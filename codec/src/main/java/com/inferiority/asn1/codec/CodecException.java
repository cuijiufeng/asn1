package com.inferiority.asn1.codec;

import com.inferiority.asn1.codec.oer.ASN1Object;

/**
 * @author cuijiufeng
 * @Class CodecException
 * @Date 2023/2/23 16:40
 */
public class CodecException extends Exception {

    public CodecException(String tag, ASN1Object object, Throwable cause) {
        super(String.format("%s %s type failed!\ncurrent position:%s",
                tag, object.getClass().getSimpleName(), object), cause);
    }
}
