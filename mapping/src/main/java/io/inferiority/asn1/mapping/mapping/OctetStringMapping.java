package io.inferiority.asn1.mapping.mapping;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import io.inferiority.asn1.analysis.analyzer.AbstractAnalyzer;
import io.inferiority.asn1.analysis.model.Definition;
import io.inferiority.asn1.analysis.util.RegexUtil;
import io.inferiority.asn1.mapping.model.MappingContext;
import io.inferiority.asn1.mapping.utils.JavaPoetUtil;

import javax.lang.model.element.Modifier;

/**
 * @author cuijiufeng
 * @date 2023/3/25 12:58
 */
public class OctetStringMapping extends AbstractMapping {
    public static final OctetStringMapping MAPPING = new OctetStringMapping();

    @Override
    protected TypeSpec mappingInternal(MappingContext context) {
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
                .addParameter(byte[].class, "value")
                .addStatement("super($N, $N, $N)", "value", rangeMin.build(), rangeMax.build())
                .build();

        TypeSpec.Builder octetStringPoet = TypeSpec.classBuilder(definition.getIdentifier())
                .addAnnotation(getGeneratedAnno(definition))
                .addModifiers(context.isInnerClass() ? new Modifier[]{Modifier.PUBLIC, Modifier.STATIC} : new Modifier[]{Modifier.PUBLIC})
                .superclass(JavaPoetUtil.primitiveTypeName(definition))
                .addField(rangeMin.build())
                .addField(rangeMax.build())
                .addMethod(constructor1)
                .addMethod(constructor2);
        return octetStringPoet.build();
    }
}
