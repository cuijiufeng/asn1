package com.inferiority.asn1.mapping.utils;

import com.inferiority.asn1.analysis.common.Reserved;
import com.inferiority.asn1.analysis.model.Definition;
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
import com.squareup.javapoet.TypeName;

import java.math.BigInteger;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @author cuijiufeng
 * @date 2023/3/28 21:45
 */
public class JavaPoetUtil {

    public static Map.Entry<String, Object[]> builderNewStatement(Definition definition, boolean withDefault) {
        if (Reserved.NULL.equals(definition.getPrimitiveType())) {
            return new AbstractMap.SimpleEntry<>("new $T()",
                    new Object[]{ASN1Null.class});
        } else if (Reserved.BOOLEAN.equals(definition.getPrimitiveType())) {
            if (withDefault && definition.getDefaulted() != null) {
                String defaulted = definition.getDefaulted().replaceAll(Reserved.DEFAULT, "").trim();
                return new AbstractMap.SimpleEntry<>("new $T($L)",
                        new Object[]{ASN1Boolean.class, defaulted});
            }
            return new AbstractMap.SimpleEntry<>("new $T()",
                    new Object[]{ASN1Boolean.class});
        } else if (Reserved.INTEGER.equals(definition.getPrimitiveType())) {
            StringJoiner statement = new StringJoiner(", ", "new $T(", ")");
            List<Object> args = new ArrayList<>(Collections.singletonList(ASN1Integer.class));
            if (withDefault) {
                if (definition.getDefaulted() != null) {
                    String defaulted = definition.getDefaulted().replaceAll(Reserved.DEFAULT, "").trim();
                    statement.add("new $T($S)");
                    args.add(BigInteger.class);
                    args.add(defaulted);
                } else {
                    statement.add("$L");
                    args.add("null");
                }
            }
            if (definition.getRangeMin() != null) {
                statement.add("new $T($L)");
                args.add(BigInteger.class);
                args.add(definition.getRangeMin());
            } else {
                statement.add("$L");
                args.add(definition.getRangeMin());
            }
            if (definition.getRangeMax() != null) {
                statement.add("new $T($L)");
                args.add(BigInteger.class);
                args.add(definition.getRangeMax());
            } else {
                statement.add("$L");
                args.add(definition.getRangeMax());
            }
            return new AbstractMap.SimpleEntry<>(statement.toString(), args.toArray());
        } else if (Reserved.ENUMERATED.equals(definition.getPrimitiveType())) {
            throw new IllegalArgumentException("unsupported type");
            //return new AbstractMap.SimpleEntry<>("new $T()",
            //        new Object[]{ASN1Enumerated.class});
        } else if (Reserved.IA5String.equals(definition.getPrimitiveType())) {
            return new AbstractMap.SimpleEntry<>("new $T($L, $L)",
                    new Object[]{ASN1IA5String.class, definition.getRangeMin(), definition.getRangeMax()});
        } else if (Reserved.UTF8String.equals(definition.getPrimitiveType())) {
            return new AbstractMap.SimpleEntry<>("new $T($L, $L)",
                    new Object[]{ASN1UTF8String.class, definition.getRangeMin(), definition.getRangeMax()});
        } else if (Reserved.SEQUENCE.equals(definition.getPrimitiveType())) {
            return new AbstractMap.SimpleEntry<>("new $T()",
                    new Object[]{ClassName.bestGuess(StringUtil.throughline2hump(definition.getIdentifier()))});
        } else if (Reserved.CHOICE.equals(definition.getPrimitiveType())) {
            return new AbstractMap.SimpleEntry<>("new $T()",
                    new Object[]{ASN1Choice.class});
        } else if (RegexUtil.matches(Reserved.BIT + "\\s+" + Reserved.STRING, definition.getPrimitiveType())) {
            throw new IllegalArgumentException("unsupported type");
            //return new AbstractMap.SimpleEntry<>("new $T()",
            //        new Object[]{ASN1BitString.class});
        } else if (RegexUtil.matches(Reserved.OCTET + "\\s+" + Reserved.STRING, definition.getPrimitiveType())) {
            return new AbstractMap.SimpleEntry<>("new $T($L, $L)",
                    new Object[]{ASN1OctetString.class, definition.getRangeMin(), definition.getRangeMax()});
        } else if (RegexUtil.matches(Reserved.SEQUENCE + "\\s+" + Reserved.OF, definition.getPrimitiveType())) {
            throw new IllegalArgumentException("unsupported type");
            //return new AbstractMap.SimpleEntry<>("new $T()",
            //        new Object[]{ASN1SequenceOf.class});
        }
        return new AbstractMap.SimpleEntry<>("new $T()",
                new Object[]{ClassName.bestGuess(definition.getPrimitiveType())});
    }

    public static TypeName primitiveTypeName(Definition definition) {
        if (Reserved.NULL.equals(definition.getPrimitiveType())) {
            return ClassName.get(ASN1Null.class);
        } else if (Reserved.BOOLEAN.equals(definition.getPrimitiveType())) {
            return ClassName.get(ASN1Boolean.class);
        } else if (Reserved.INTEGER.equals(definition.getPrimitiveType())) {
            return ClassName.get(ASN1Integer.class);
        } else if (Reserved.ENUMERATED.equals(definition.getPrimitiveType())) {
            return ClassName.get(ASN1Enumerated.class);
        } else if (Reserved.IA5String.equals(definition.getPrimitiveType())) {
            return ClassName.get(ASN1IA5String.class);
        } else if (Reserved.UTF8String.equals(definition.getPrimitiveType())) {
            return ClassName.get(ASN1UTF8String.class);
        } else if (Reserved.SEQUENCE.equals(definition.getPrimitiveType())) {
            return ClassName.get(ASN1Sequence.class);
        } else if (Reserved.CHOICE.equals(definition.getPrimitiveType())) {
            return ClassName.get(ASN1Choice.class);
        } else if (RegexUtil.matches(Reserved.BIT + "\\s+" + Reserved.STRING, definition.getPrimitiveType())) {
            return ClassName.get(ASN1BitString.class);
        } else if (RegexUtil.matches(Reserved.OCTET + "\\s+" + Reserved.STRING, definition.getPrimitiveType())) {
            return ClassName.get(ASN1OctetString.class);
        } else if (RegexUtil.matches(Reserved.SEQUENCE + "\\s+" + Reserved.OF, definition.getPrimitiveType())) {
            return ClassName.get(ASN1SequenceOf.class);
        }
        return ClassName.bestGuess(definition.getPrimitiveType());
    }
}
