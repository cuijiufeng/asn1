package com.inferiority.asn1.mapping.utils;

import com.inferiority.asn1.analysis.common.Reserved;
import com.inferiority.asn1.analysis.util.RegexUtil;
import com.inferiority.asn1.codec.oer.ASN1BitString;
import com.inferiority.asn1.codec.oer.ASN1Boolean;
import com.inferiority.asn1.codec.oer.ASN1Choice;
import com.inferiority.asn1.codec.oer.ASN1Enumerated;
import com.inferiority.asn1.codec.oer.ASN1IA5String;
import com.inferiority.asn1.codec.oer.ASN1Integer;
import com.inferiority.asn1.codec.oer.ASN1Null;
import com.inferiority.asn1.codec.oer.ASN1OctetString;
import com.inferiority.asn1.codec.oer.ASN1Sequence;
import com.inferiority.asn1.codec.oer.ASN1SequenceOf;
import com.inferiority.asn1.codec.oer.ASN1UTF8String;
import com.squareup.javapoet.ClassName;

/**
 * @author cuijiufeng
 * @date 2023/3/28 21:45
 */
public class PrimitiveTypeUtil {

    public static ClassName primitiveType(String primitiveType) {
        if (Reserved.NULL.equals(primitiveType)) {
            return ClassName.get(ASN1Null.class);
        } else if (Reserved.BOOLEAN.equals(primitiveType)) {
            return ClassName.get(ASN1Boolean.class);
        } else if (Reserved.INTEGER.equals(primitiveType)) {
            return ClassName.get(ASN1Integer.class);
        } else if (Reserved.ENUMERATED.equals(primitiveType)) {
            return ClassName.get(ASN1Enumerated.class);
        } else if (Reserved.IA5String.equals(primitiveType)) {
            return ClassName.get(ASN1IA5String.class);
        } else if (Reserved.UTF8String.equals(primitiveType)) {
            return ClassName.get(ASN1UTF8String.class);
        } else if (Reserved.SEQUENCE.equals(primitiveType)) {
            return ClassName.get(ASN1Sequence.class);
        } else if (Reserved.CHOICE.equals(primitiveType)) {
            return ClassName.get(ASN1Choice.class);
        } else if (primitiveType.equals(Reserved.BIT + " " + Reserved.STRING)) {
            return ClassName.get(ASN1BitString.class);
        } else if (primitiveType.equals(Reserved.OCTET + " " + Reserved.STRING)) {
            return ClassName.get(ASN1OctetString.class);
        } else if (RegexUtil.matches(Reserved.SEQUENCE + "\\s*" + Reserved.OF, primitiveType)) {
            return ClassName.get(ASN1SequenceOf.class);
        }
        return ClassName.bestGuess(primitiveType);
    }
}
