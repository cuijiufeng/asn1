package io.inferiority.asn1.mapping.utils;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import io.inferiority.asn1.analysis.common.Reserved;
import io.inferiority.asn1.analysis.model.Definition;
import io.inferiority.asn1.analysis.util.ArrayUtil;
import io.inferiority.asn1.analysis.util.RegexUtil;
import io.inferiority.asn1.codec.oer.ASN1BitString;
import io.inferiority.asn1.codec.oer.ASN1Boolean;
import io.inferiority.asn1.codec.oer.ASN1Choice;
import io.inferiority.asn1.codec.oer.ASN1Enumerated;
import io.inferiority.asn1.codec.oer.ASN1IA5String;
import io.inferiority.asn1.codec.oer.ASN1Integer;
import io.inferiority.asn1.codec.oer.ASN1Null;
import io.inferiority.asn1.codec.oer.ASN1OctetString;
import io.inferiority.asn1.codec.oer.ASN1Sequence;
import io.inferiority.asn1.codec.oer.ASN1SequenceOf;
import io.inferiority.asn1.codec.oer.ASN1UTF8String;
import io.inferiority.asn1.mapping.mapping.AbstractMapping;
import io.inferiority.asn1.mapping.model.MappingContext;

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

    public static Map.Entry<String, Object[]> builderNewStatement(MappingContext context, Definition definition, String withValue, boolean withDefault) {
        if (Reserved.NULL.equals(definition.getPrimitiveType())) {
            return new AbstractMap.SimpleEntry<>("new $T()", new Object[]{ASN1Null.class});
        } else if (Reserved.BOOLEAN.equals(definition.getPrimitiveType())) {
            StringJoiner statement = new StringJoiner(", ", "new $T(", ")");
            List<Object> args = new ArrayList<>(Collections.singletonList(ASN1Boolean.class));
            if (withDefault) {
                if (definition.getDefaulted() != null) {
                    statement.add("$L");
                    args.add(definition.getDefaulted().replaceAll(Reserved.DEFAULT, "").trim());
                }
                return new AbstractMap.SimpleEntry<>("null", new Object[0]);
            } else if (withValue != null) {
                return new AbstractMap.SimpleEntry<>("$L == null ? null : new $T($L)", new Object[]{withValue, ASN1Boolean.class, withValue});
            }
            return new AbstractMap.SimpleEntry<>(statement.toString(), args.toArray());
        } else if (Reserved.INTEGER.equals(definition.getPrimitiveType())) {
            StringJoiner statement = new StringJoiner(", ", "new $T(", ")");
            List<Object> args = new ArrayList<>(Collections.singletonList(ASN1Integer.class));
            if (withDefault) {
                if (definition.getDefaulted() != null) {
                    statement.add("new $T($S)");
                    args.add(BigInteger.class);
                    args.add(definition.getDefaulted().replaceAll(Reserved.DEFAULT, "").trim());
                }
                return new AbstractMap.SimpleEntry<>("null", new Object[0]);
            } else if (withValue != null) {
                statement.add("$T.valueOf($L)");
                args.add(BigInteger.class);
                args.add(withValue);
            }
            if (definition.getRangeMin() != null) {
                statement.add("new $T($L)");
                args.add(BigInteger.class);
                args.add(definition.getRangeMin());
            } else {
                statement.add("null");
            }
            if (definition.getRangeMax() != null) {
                statement.add("new $T($L)");
                args.add(BigInteger.class);
                args.add(definition.getRangeMax());
            } else {
                statement.add("null");
            }
            return withValue != null
                    ? new AbstractMap.SimpleEntry<>("$N == null ? null : " + statement.toString(), ArrayUtil.concat(new Object[]{withValue}, args.toArray()))
                    : new AbstractMap.SimpleEntry<>(statement.toString(), args.toArray());
        } else if (Reserved.ENUMERATED.equals(definition.getPrimitiveType())) {
            StringJoiner statement = new StringJoiner(", ", "new $T(", ")");
            List<Object> args = new ArrayList<>(Collections.singletonList(ASN1Enumerated.class));
            statement.add("$T");
            args.add(ClassName.bestGuess(context.getEnumPrefix() + definition.getIdentifier() + context.getEnumSuffix()));
            return new AbstractMap.SimpleEntry<>(statement.toString(), args.toArray());
        } else if (Reserved.IA5String.equals(definition.getPrimitiveType())) {
            StringJoiner statement = new StringJoiner(", ", "new $T(", ")");
            List<Object> args = new ArrayList<>(Collections.singletonList(ASN1IA5String.class));
            if (withDefault) {
                if (definition.getDefaulted() != null) {
                    statement.add("$L");
                    args.add(definition.getDefaulted().replaceAll(Reserved.DEFAULT, "").trim());
                }
                return new AbstractMap.SimpleEntry<>("null", new Object[0]);
            } else if (withValue != null) {
                return new AbstractMap.SimpleEntry<>("$L == null ? null : new $T($L, $L, $L)",
                        new Object[]{withValue, ASN1IA5String.class, withValue, definition.getRangeMin(), definition.getRangeMax()});
            }
            statement.add("$L");
            args.add(definition.getRangeMin());
            statement.add("$L");
            args.add(definition.getRangeMax());
            return new AbstractMap.SimpleEntry<>(statement.toString(), args.toArray());
        } else if (Reserved.UTF8String.equals(definition.getPrimitiveType())) {
            StringJoiner statement = new StringJoiner(", ", "new $T(", ")");
            List<Object> args = new ArrayList<>(Collections.singletonList(ASN1UTF8String.class));
            if (withDefault) {
                if (definition.getDefaulted() != null) {
                    statement.add("$L");
                    args.add(definition.getDefaulted().replaceAll(Reserved.DEFAULT, "").trim());
                }
                return new AbstractMap.SimpleEntry<>("null", new Object[0]);
            } else if (withValue != null) {
                return new AbstractMap.SimpleEntry<>("$L == null ? null : new $T($L, $L, $L)",
                        new Object[]{withValue, ASN1UTF8String.class, withValue, definition.getRangeMin(), definition.getRangeMax()});
            }
            statement.add("$L");
            args.add(definition.getRangeMin());
            statement.add("$L");
            args.add(definition.getRangeMax());
            return new AbstractMap.SimpleEntry<>(statement.toString(), args.toArray());
        } else if (Reserved.SEQUENCE.equals(definition.getPrimitiveType())) {
            throw new IllegalArgumentException("unsupported type");
        } else if (Reserved.CHOICE.equals(definition.getPrimitiveType())) {
            throw new IllegalArgumentException("unsupported type");
        } else if (RegexUtil.matches(Reserved.BIT + "\\s+" + Reserved.STRING, definition.getPrimitiveType())) {
            throw new IllegalArgumentException("unsupported type");
        } else if (RegexUtil.matches(Reserved.OCTET + "\\s+" + Reserved.STRING, definition.getPrimitiveType())) {
            StringJoiner statement = new StringJoiner(", ", "new $T(", ")");
            List<Object> args = new ArrayList<>(Collections.singletonList(ASN1OctetString.class));
            if (withDefault) {
                if (definition.getDefaulted() != null) {
                    statement.add("$L");
                    args.add(definition.getDefaulted().replaceAll(Reserved.DEFAULT, "").trim());
                }
                return new AbstractMap.SimpleEntry<>("null", new Object[0]);
            } else if (withValue != null) {
                return new AbstractMap.SimpleEntry<>("$L == null ? null : new $T($L, $L, $L)",
                        new Object[]{withValue, ASN1OctetString.class, withValue, definition.getRangeMin(), definition.getRangeMax()});
            }
            statement.add("$L");
            args.add(definition.getRangeMin());
            statement.add("$L");
            args.add(definition.getRangeMax());
            return new AbstractMap.SimpleEntry<>(statement.toString(), args.toArray());
        } else if (RegexUtil.matches(Reserved.SEQUENCE + "\\s+" + Reserved.OF, definition.getPrimitiveType())) {
            throw new IllegalArgumentException("unsupported type");
        }
        if (withDefault) {
            if (definition.getDefaulted() != null) {
                return new AbstractMap.SimpleEntry<>("new $T($L)", new Object[]{
                        ClassName.get(AbstractMapping.getPrimitiveTypePackageName(context, definition), definition.getPrimitiveType()),
                        definition.getDefaulted().replaceAll(Reserved.DEFAULT, "").trim()});
            }
            return new AbstractMap.SimpleEntry<>("null", new Object[0]);
        } else if (withValue != null) {
            return new AbstractMap.SimpleEntry<>("$L == null ? null : $L", new Object[]{withValue, withValue});
        }
        return new AbstractMap.SimpleEntry<>("new $T()", new Object[]{
                ClassName.get(AbstractMapping.getPrimitiveTypePackageName(context, definition), definition.getPrimitiveType())});
    }

    public static TypeName javaTypeName(MappingContext context, Definition definition) {
        if (Reserved.NULL.equals(definition.getPrimitiveType())) {
            return ClassName.get(Void.class);
        } else if (Reserved.BOOLEAN.equals(definition.getPrimitiveType())) {
            return ClassName.get(Boolean.class);
        } else if (Reserved.INTEGER.equals(definition.getPrimitiveType())) {
            return ClassName.get(Long.class);
        } else if (Reserved.ENUMERATED.equals(definition.getPrimitiveType())) {
            return ClassName.bestGuess(context.getEnumPrefix() + definition.getIdentifier() + context.getEnumSuffix());
        } else if (Reserved.IA5String.equals(definition.getPrimitiveType())) {
            return ClassName.get(String.class);
        } else if (Reserved.UTF8String.equals(definition.getPrimitiveType())) {
            return ClassName.get(String.class);
        } else if (Reserved.SEQUENCE.equals(definition.getPrimitiveType())) {
            throw new IllegalArgumentException("unsupported type");
        } else if (Reserved.CHOICE.equals(definition.getPrimitiveType())) {
            throw new IllegalArgumentException("unsupported type");
        } else if (RegexUtil.matches(Reserved.BIT + "\\s+" + Reserved.STRING, definition.getPrimitiveType())) {
            return ArrayTypeName.get(byte[].class);
        } else if (RegexUtil.matches(Reserved.OCTET + "\\s+" + Reserved.STRING, definition.getPrimitiveType())) {
            return ArrayTypeName.get(byte[].class);
        } else if (RegexUtil.matches(Reserved.SEQUENCE + "\\s+" + Reserved.OF, definition.getPrimitiveType())) {
            throw new IllegalArgumentException("unsupported type");
        }
        return ClassName.get(AbstractMapping.getPrimitiveTypePackageName(context, definition), definition.getPrimitiveType());
    }

    public static TypeName primitiveTypeName(MappingContext context) {
        Definition definition = context.getDefinition();
        if (Reserved.NULL.equals(definition.getPrimitiveType())) {
            return ClassName.get(ASN1Null.class);
        } else if (Reserved.BOOLEAN.equals(definition.getPrimitiveType())) {
            return ClassName.get(ASN1Boolean.class);
        } else if (Reserved.INTEGER.equals(definition.getPrimitiveType())) {
            return ClassName.get(ASN1Integer.class);
        } else if (Reserved.ENUMERATED.equals(definition.getPrimitiveType())) {
            return ParameterizedTypeName.get(ClassName.get(ASN1Enumerated.class),
                    ClassName.bestGuess(definition.getIdentifier() + "." + context.getEnumPrefix() + definition.getIdentifier() + context.getEnumSuffix()));
        } else if (Reserved.IA5String.equals(definition.getPrimitiveType())) {
            return ClassName.get(ASN1IA5String.class);
        } else if (Reserved.UTF8String.equals(definition.getPrimitiveType())) {
            return ClassName.get(ASN1UTF8String.class);
        } else if (Reserved.SEQUENCE.equals(definition.getPrimitiveType())) {
            return ClassName.get(ASN1Sequence.class);
        } else if (Reserved.CHOICE.equals(definition.getPrimitiveType())) {
            return ParameterizedTypeName.get(ClassName.get(ASN1Choice.class),
                    ClassName.bestGuess(definition.getIdentifier() + "." + context.getEnumPrefix() + definition.getIdentifier() + context.getEnumSuffix()));
        } else if (RegexUtil.matches(Reserved.BIT + "\\s+" + Reserved.STRING, definition.getPrimitiveType())) {
            return ClassName.get(ASN1BitString.class);
        } else if (RegexUtil.matches(Reserved.OCTET + "\\s+" + Reserved.STRING, definition.getPrimitiveType())) {
            return ClassName.get(ASN1OctetString.class);
        } else if (RegexUtil.matches(Reserved.SEQUENCE + "\\s+" + Reserved.OF, definition.getPrimitiveType())) {
            String[] split = definition.getPrimitiveType().split("[ ]+");
            String primitiveType = split[split.length - 1];
            return ParameterizedTypeName.get(ClassName.get(ASN1SequenceOf.class), ClassName.bestGuess(primitiveType));
        }
        return ClassName.get(AbstractMapping.getPrimitiveTypePackageName(context, definition), definition.getPrimitiveType());
    }
}
