package com.inferiority.asn1.mapping.mapping;

import com.inferiority.asn1.analysis.analyzer.AbstractAnalyzer;
import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.analysis.util.RegexUtil;
import com.inferiority.asn1.codec.oer.ASN1Object;
import com.inferiority.asn1.mapping.model.MappingContext;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

/**
 * @author cuijiufeng
 * @date 2023/3/25 14:22
 */
public abstract class AbstractStringMapping extends AbstractMapping {

    public abstract Class<? extends ASN1Object> getSuperclass();

    @Override
    public TypeSpec mappingInternal(MappingContext context) {
        Definition definition = context.getDefinition();

        FieldSpec.Builder rangeMin = FieldSpec.builder(Integer.class, "RANGE_MIN", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);
        if (definition.getRangeMin() == null || !RegexUtil.matches(AbstractAnalyzer.REGEX_NUM, definition.getRangeMin())) {
            rangeMin.initializer("null");
        } else {
            rangeMin.initializer("$L", definition.getRangeMin());
        }

        FieldSpec.Builder rangeMax = FieldSpec.builder(Integer.class, "RANGE_MAX", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);
        if (definition.getRangeMax() == null || !RegexUtil.matches(AbstractAnalyzer.REGEX_NUM, definition.getRangeMax())) {
            rangeMax.initializer("null");
        } else {
            rangeMax.initializer("$L", definition.getRangeMax());
        }

        MethodSpec constructor1 = MethodSpec.constructorBuilder()
                .addStatement("super($N, $N)", rangeMin.build(), rangeMax.build())
                .build();
        MethodSpec constructor2 = MethodSpec.constructorBuilder()
                .addParameter(String.class, "string")
                .addStatement("super($N, $N, $N)", "string", rangeMin.build(), rangeMax.build())
                .build();

        TypeSpec.Builder stringPoet = TypeSpec.classBuilder(definition.getIdentifier())
                .addModifiers(Modifier.PUBLIC)
                .superclass(getSuperclass())
                .addAnnotation(getGeneratedAnno(definition))
                .addField(rangeMin.build())
                .addField(rangeMax.build())
                .addMethod(constructor1)
                .addMethod(constructor2);
        return stringPoet.build();
    }
}
