package com.inferiority.asn1.mapping.utils;

import com.inferiority.asn1.analysis.common.Reserved;
import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.analysis.util.RegexUtil;
import com.inferiority.asn1.codec.oer.ASN1Boolean;
import com.inferiority.asn1.codec.oer.ASN1Choice;
import com.inferiority.asn1.codec.oer.ASN1IA5String;
import com.inferiority.asn1.codec.oer.ASN1Integer;
import com.inferiority.asn1.codec.oer.ASN1Null;
import com.inferiority.asn1.codec.oer.ASN1OctetString;
import com.inferiority.asn1.codec.oer.ASN1UTF8String;
import com.squareup.javapoet.ClassName;

import java.util.AbstractMap;
import java.util.Map;

/**
 * @author cuijiufeng
 * @date 2023/3/28 21:45
 */
public class JavaPoetUtil {

    public static Map.Entry<String, Object[]> builderNewStatement(Definition definition) {
        if (Reserved.NULL.equals(definition.getPrimitiveType())) {
            return new AbstractMap.SimpleEntry<>("new $T()",
                    new Object[]{ClassName.get(ASN1Null.class)});
        } else if (Reserved.BOOLEAN.equals(definition.getPrimitiveType())) {
            return new AbstractMap.SimpleEntry<>("new $T()",
                    new Object[]{ClassName.get(ASN1Boolean.class)});
        } else if (Reserved.INTEGER.equals(definition.getPrimitiveType())) {
            return new AbstractMap.SimpleEntry<>("new $T(new BigInteger($S), new BigInteger($S))",
                    new Object[]{ClassName.get(ASN1Integer.class), definition.getRangeMin(), definition.getRangeMax()});
        } else if (Reserved.ENUMERATED.equals(definition.getPrimitiveType())) {
            throw new IllegalArgumentException("unsupported type");
            //return new AbstractMap.SimpleEntry<>("new $T()",
            //        new Object[]{ClassName.get(ASN1Enumerated.class)});
        } else if (Reserved.IA5String.equals(definition.getPrimitiveType())) {
            return new AbstractMap.SimpleEntry<>("new $T($L, $L)",
                    new Object[]{ClassName.get(ASN1IA5String.class), definition.getRangeMin(), definition.getRangeMax()});
        } else if (Reserved.UTF8String.equals(definition.getPrimitiveType())) {
            return new AbstractMap.SimpleEntry<>("new $T($L, $L)",
                    new Object[]{ClassName.get(ASN1UTF8String.class), definition.getRangeMin(), definition.getRangeMax()});
        } else if (Reserved.SEQUENCE.equals(definition.getPrimitiveType())) {
            return new AbstractMap.SimpleEntry<>("new $T()",
                    new Object[]{ClassName.bestGuess(StringUtil.throughline2hump(definition.getIdentifier()))});
        } else if (Reserved.CHOICE.equals(definition.getPrimitiveType())) {
            return new AbstractMap.SimpleEntry<>("new $T()",
                    new Object[]{ClassName.get(ASN1Choice.class)});
        } else if (definition.getPrimitiveType().equals(Reserved.BIT + " " + Reserved.STRING)) {
            throw new IllegalArgumentException("unsupported type");
            //return new AbstractMap.SimpleEntry<>("new $T()",
            //        new Object[]{ClassName.get(ASN1BitString.class)});
        } else if (definition.getPrimitiveType().equals(Reserved.OCTET + " " + Reserved.STRING)) {
            return new AbstractMap.SimpleEntry<>("new $T($L, $L)",
                    new Object[]{ClassName.get(ASN1OctetString.class), definition.getRangeMin(), definition.getRangeMax()});
        } else if (RegexUtil.matches(Reserved.SEQUENCE + "\\s+" + Reserved.OF, definition.getPrimitiveType())) {
            throw new IllegalArgumentException("unsupported type");
            //return new AbstractMap.SimpleEntry<>("new $T()",
            //        new Object[]{ClassName.get(ASN1SequenceOf.class)});
        }
        return new AbstractMap.SimpleEntry<>("new $T()",
                new Object[]{ClassName.bestGuess(definition.getPrimitiveType())});
    }
}
