package com.inferiority.asn1.mapping.mapping;

import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.codec.oer.ASN1Boolean;
import com.inferiority.asn1.mapping.model.MappingContext;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.util.Map;

/**
 * @author cuijiufeng
 * @date 2023/3/25 13:13
 */
public class BooleanMapping extends AbstractMapping {
    public static final BooleanMapping MAPPING = new BooleanMapping();

    @Override
    protected TypeSpec mappingInternal(MappingContext context) {
        Definition definition = context.getDefinition();

        MethodSpec constructor1 = MethodSpec.constructorBuilder()
                .addStatement("super()")
                .build();
        MethodSpec constructor2 = MethodSpec.constructorBuilder()
                .addParameter(boolean.class, "value")
                .addStatement("super($N)", "value")
                .build();

        TypeSpec.Builder booleanPoet = TypeSpec.classBuilder(definition.getIdentifier())
                .addModifiers(Modifier.PUBLIC)
                .superclass(ASN1Boolean.class)
                .addAnnotation(getGeneratedAnno(definition))
                .addMethod(constructor1)
                .addMethod(constructor2);
        if (definition.getValues() != null) {
            for (Map.Entry<String, String> entry : definition.getValues()) {
                FieldSpec fieldSpec = FieldSpec.builder(ClassName.bestGuess(definition.getIdentifier()), entry.getKey(), Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("new $N($L)", booleanPoet.build(), entry.getValue())
                        .build();
                booleanPoet.addField(fieldSpec);
            }
        }
        return booleanPoet.build();
    }
}
