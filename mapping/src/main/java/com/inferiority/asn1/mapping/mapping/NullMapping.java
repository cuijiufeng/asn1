package com.inferiority.asn1.mapping.mapping;

import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.codec.oer.ASN1Null;
import com.inferiority.asn1.mapping.model.MappingContext;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.util.Map;

/**
 * @author cuijiufeng
 * @date 2023/3/25 13:08
 */
public class NullMapping extends AbstractMapping {
    public static final NullMapping MAPPING = new NullMapping();

    @Override
    protected TypeSpec mappingInternal(MappingContext context) {
        Definition definition = context.getDefinition();

        TypeSpec.Builder nullPoet = TypeSpec.classBuilder(definition.getIdentifier())
                .addModifiers(Modifier.PUBLIC)
                .superclass(ASN1Null.class)
                .addAnnotation(getGeneratedAnno(definition));
        if (definition.getValues() != null) {
            for (Map.Entry<String, String> entry : definition.getValues()) {
                FieldSpec fieldSpec = FieldSpec.builder(ClassName.bestGuess(definition.getIdentifier()), entry.getKey(), Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("new $N($L)", nullPoet.build(), entry.getValue())
                        .build();
                nullPoet.addField(fieldSpec);
            }
        }
        return nullPoet.build();
    }
}
