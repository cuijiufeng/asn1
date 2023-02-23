package com.inferiority.codec;

import java.io.IOException;

/**
 * @Class: Codeable
 * @Date: 2023/2/3 13:37
 * @author: cuijiufeng
 */
public interface Codeable {

    void encode(ASN1OutputStream os) throws CodecException;

    void decode(ASN1InputStream is) throws IOException;

    boolean asn1Equals(Codeable obj);

    String toObjectString();

    default String toXmlString() {
        return toObjectString();
    }

    default String toJsonString() {
        return toObjectString();
    }
}