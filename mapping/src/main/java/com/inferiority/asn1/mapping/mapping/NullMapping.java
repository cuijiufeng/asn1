package com.inferiority.asn1.mapping.mapping;

import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.codec.oer.ASN1Null;
import com.inferiority.asn1.mapping.model.MappingContext;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

/**
 * @author cuijiufeng
 * @date 2023/3/25 13:08
 */
public class NullMapping extends AbstractMapping {
    public static final NullMapping MAPPING = new NullMapping();

    @Override
    protected TypeSpec mappingInternal(MappingContext context) {
        Definition definition = context.getDefinition();

        TypeSpec.Builder nullPoet = getBuilder(context, definition)
                .addModifiers(Modifier.PUBLIC)
                .superclass(ASN1Null.class);
        valuesField(nullPoet, definition, (field, value) -> field.initializer("new $N($L)", nullPoet.build(), value));
        return nullPoet.build();
    }
}
