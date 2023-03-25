package com.inferiority.asn1.mapping.mapping;

import com.inferiority.asn1.analysis.common.Reserved;
import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.analysis.util.RegexUtil;
import com.inferiority.asn1.mapping.MappingException;
import com.inferiority.asn1.mapping.model.MappingContext;
import com.squareup.javapoet.AnnotationSpec;

import javax.annotation.Generated;
import java.io.IOException;

/**
 * @author cuijiufeng
 * @Class AbstractMapping
 * @Date 2023/3/21 14:30
 */
public abstract class AbstractMapping {

    public static AbstractMapping getInstance(MappingContext context) {
        if (Reserved.NULL.equals(context.getDefinition().getPrimitiveType())) {
            return NullMapping.MAPPING;
        } else if (Reserved.BOOLEAN.equals(context.getDefinition().getPrimitiveType())) {
            return null;
        } else if (Reserved.INTEGER.equals(context.getDefinition().getPrimitiveType())) {
            return IntegerMapping.MAPPING;
        } else if (Reserved.ENUMERATED.equals(context.getDefinition().getPrimitiveType())) {
            return null;
        } else if (Reserved.IA5String.equals(context.getDefinition().getPrimitiveType())) {
            return null;
        } else if (Reserved.UTF8String.equals(context.getDefinition().getPrimitiveType())) {
            return null;
        } else if (Reserved.SEQUENCE.equals(context.getDefinition().getPrimitiveType())) {
            return null;
        } else if (Reserved.CHOICE.equals(context.getDefinition().getPrimitiveType())) {
            return null;
        } else if (context.getDefinition().getPrimitiveType().equals(Reserved.BIT + " " + Reserved.STRING)) {
            return null;
        } else if (context.getDefinition().getPrimitiveType().equals(Reserved.OCTET + " " + Reserved.STRING)) {
            return OctetStringMapping.MAPPING;
        } else if (RegexUtil.matches(Reserved.SEQUENCE + "\\s*" + Reserved.OF, context.getDefinition().getPrimitiveType())) {
            return SequenceOfMapping.MAPPING;
        }
        return null;
    }

    public void mapping(MappingContext context) {
        try {
            mappingInternal(context);
        } catch (Exception e) {
            throw new MappingException("mapping to class error -> " + e.getMessage() + "\n" + context.getDefinition(), e);
        }
    }

    protected abstract void mappingInternal(MappingContext context) throws IOException;

    protected AnnotationSpec getGeneratedAnno(Definition definition) {
        return AnnotationSpec.builder(Generated.class)
                .addMember("value", "$S", "by " + this.getClass().getSimpleName() + " generated")
                .addMember("comments", "$S", "Source: " + definition.getModule().getIdentifier() + " --> " + definition.getIdentifier())
                .build();
    }
}
