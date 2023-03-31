package io.inferiority.asn1.mapping.mapping;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import io.inferiority.asn1.analysis.analyzer.AbstractAnalyzer;
import io.inferiority.asn1.analysis.model.Definition;
import io.inferiority.asn1.analysis.util.RegexUtil;
import io.inferiority.asn1.codec.oer.ASN1Object;
import io.inferiority.asn1.mapping.model.MappingContext;

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
                .addModifiers(Modifier.PUBLIC)
                .addStatement("super($N, $N)", rangeMin.build(), rangeMax.build())
                .build();
        MethodSpec constructor2 = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "string")
                .addStatement("super($N, $N, $N)", "string", rangeMin.build(), rangeMax.build())
                .build();

        TypeSpec.Builder stringPoet = TypeSpec.classBuilder(definition.getIdentifier())
                .addAnnotation(getGeneratedAnno(definition))
                .addModifiers(context.isInnerClass() ? new Modifier[]{Modifier.PUBLIC, Modifier.STATIC} : new Modifier[]{Modifier.PUBLIC})
                .superclass(getSuperclass())
                .addField(rangeMin.build())
                .addField(rangeMax.build())
                .addMethod(constructor1)
                .addMethod(constructor2);
        return stringPoet.build();
    }
}
