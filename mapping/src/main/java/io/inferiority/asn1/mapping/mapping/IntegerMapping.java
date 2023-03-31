package io.inferiority.asn1.mapping.mapping;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import io.inferiority.asn1.analysis.analyzer.AbstractAnalyzer;
import io.inferiority.asn1.analysis.model.Definition;
import io.inferiority.asn1.analysis.util.RegexUtil;
import io.inferiority.asn1.codec.oer.ASN1Integer;
import io.inferiority.asn1.mapping.model.MappingContext;

import javax.lang.model.element.Modifier;
import java.math.BigInteger;

/**
 * @author cuijiufeng
 * @Class IntegerMapping
 * @Date 2023/3/21 14:50
 */
public class IntegerMapping extends AbstractMapping {
    public static final IntegerMapping MAPPING = new IntegerMapping();

    @Override
    public TypeSpec mappingInternal(MappingContext context) {
        Definition definition = context.getDefinition();

        FieldSpec.Builder rangeMin = FieldSpec.builder(BigInteger.class, "RANGE_MIN", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);
        if (definition.getRangeMin() == null || !RegexUtil.matches(AbstractAnalyzer.REGEX_NUM, definition.getRangeMin())) {
            rangeMin.initializer("null");
        } else {
            rangeMin.initializer("new BigInteger($S)", definition.getRangeMin());
        }

        FieldSpec.Builder rangeMax = FieldSpec.builder(BigInteger.class, "RANGE_MAX", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);
        if (definition.getRangeMax() == null || !RegexUtil.matches(AbstractAnalyzer.REGEX_NUM, definition.getRangeMax())) {
            rangeMax.initializer("null");
        } else {
            rangeMax.initializer("new BigInteger($S)", definition.getRangeMax());
        }

        MethodSpec constructor1 = MethodSpec.constructorBuilder()
                .addStatement("super($N, $N)", rangeMin.build(), rangeMax.build())
                .build();
        MethodSpec constructor2 = MethodSpec.constructorBuilder()
                .addParameter(int.class, "value")
                .addStatement("super($T.valueOf($N), $N, $N)", BigInteger.class, "value", rangeMin.build(), rangeMax.build())
                .build();
        TypeSpec.Builder integerPoet = TypeSpec.classBuilder(definition.getIdentifier())
                .addAnnotation(getGeneratedAnno(definition))
                .addModifiers(context.isInnerClass() ? new Modifier[]{Modifier.PUBLIC, Modifier.STATIC} : new Modifier[]{Modifier.PUBLIC})
                .superclass(ASN1Integer.class)
                .addField(rangeMin.build())
                .addField(rangeMax.build())
                .addMethod(constructor1)
                .addMethod(constructor2);
        valuesField(integerPoet, definition, (field, value) -> field.initializer("new $N($L)", integerPoet.build(), value));
        return integerPoet.build();
    }
}
