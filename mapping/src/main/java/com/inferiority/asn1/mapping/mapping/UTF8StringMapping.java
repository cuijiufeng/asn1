package com.inferiority.asn1.mapping.mapping;

import com.inferiority.asn1.codec.oer.ASN1Object;
import com.inferiority.asn1.codec.oer.ASN1UTF8String;

/**
 * @author cuijiufeng
 * @date 2023/3/25 14:27
 */
public class UTF8StringMapping extends AbstractStringMapping {
    public static final UTF8StringMapping MAPPING = new UTF8StringMapping();

    @Override
    public Class<? extends ASN1Object> getSuperclass() {
        return ASN1UTF8String.class;
    }
}
