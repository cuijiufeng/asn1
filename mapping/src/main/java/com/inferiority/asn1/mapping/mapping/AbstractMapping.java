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
        Definition definition = context.getDefinition();
        if (Reserved.NULL.equals(definition.getPrimitiveType())) {
            return NullMapping.MAPPING;
        } else if (Reserved.BOOLEAN.equals(definition.getPrimitiveType())) {
            return BooleanMapping.MAPPING;
        } else if (Reserved.INTEGER.equals(definition.getPrimitiveType())) {
            return IntegerMapping.MAPPING;
        } else if (Reserved.ENUMERATED.equals(definition.getPrimitiveType())) {
            return EnumeratedMapping.MAPPING;
        } else if (Reserved.IA5String.equals(definition.getPrimitiveType())) {
            return IA5StringMapping.MAPPING;
        } else if (Reserved.UTF8String.equals(definition.getPrimitiveType())) {
            return UTF8StringMapping.MAPPING;
        } else if (Reserved.SEQUENCE.equals(definition.getPrimitiveType())) {
            return SequenceMapping.MAPPING;
        } else if (Reserved.CHOICE.equals(definition.getPrimitiveType())) {
            return ChoiceMapping.MAPPING;
        } else if (definition.getPrimitiveType().equals(Reserved.BIT + " " + Reserved.STRING)) {
            return BitStringMapping.MAPPING;
        } else if (definition.getPrimitiveType().equals(Reserved.OCTET + " " + Reserved.STRING)) {
            return OctetStringMapping.MAPPING;
        } else if (RegexUtil.matches(Reserved.SEQUENCE + "\\s*" + Reserved.OF, definition.getPrimitiveType())) {
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
