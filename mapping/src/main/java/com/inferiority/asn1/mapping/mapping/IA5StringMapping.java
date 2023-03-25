package com.inferiority.asn1.mapping.mapping;

import com.inferiority.asn1.codec.oer.ASN1IA5String;
import com.inferiority.asn1.codec.oer.ASN1Object;

/**
 * @author cuijiufeng
 * @date 2023/3/25 14:26
 */
public class IA5StringMapping extends AbstractStringMapping {
    public static final IA5StringMapping MAPPING = new IA5StringMapping();

    @Override
    public Class<? extends ASN1Object> getSuperclass() {
        return ASN1IA5String.class;
    }
}
