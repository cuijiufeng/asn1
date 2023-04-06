package io.inferiority.asn1.mapping.mapping;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import io.inferiority.asn1.analysis.analyzer.AbstractAnalyzer;
import io.inferiority.asn1.analysis.model.Definition;
import io.inferiority.asn1.analysis.util.RegexUtil;
import io.inferiority.asn1.mapping.model.MappingContext;
import io.inferiority.asn1.mapping.utils.JavaPoetUtil;
import io.inferiority.asn1.mapping.utils.StringUtil;

import javax.lang.model.element.Modifier;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author cuijiufeng
 * @date 2023/3/25 14:34
 */
public class BitStringMapping extends AbstractMapping {
    public static final BitStringMapping MAPPING = new BitStringMapping();

    @Override
    protected TypeSpec mappingInternal(MappingContext context) {
        Definition definition = context.getDefinition();

        assert 0 == Objects.compare(definition.getRangeMin(), definition.getRangeMax(), String.CASE_INSENSITIVE_ORDER);

        FieldSpec.Builder sizeField = FieldSpec.builder(Integer.class, "SIZE", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);

        MethodSpec.Builder constructor1 = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);
        MethodSpec.Builder constructor2 = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);
        MethodSpec.Builder constructor3 = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);

        String bitLabels = "null";
        if (definition.getSubDefs() != null && !definition.getSubDefs().isEmpty()) {
            bitLabels = definition.getSubDefs()
                    .stream()
                    .sorted((d1, d2) -> Objects.compare(d1.getSubBodyText(), d2.getSubBodyText(), String.CASE_INSENSITIVE_ORDER))
                    .map(Definition::getIdentifier)
                    .map(str -> "\"" + str + "\"")
                    .collect(Collectors.joining(", ", "new String[]{", "}"));
        }

        if (definition.getRangeMin() == null || !RegexUtil.matches(AbstractAnalyzer.REGEX_NUM, definition.getRangeMin())) {
            sizeField.initializer("null");
            constructor1.addStatement("super($L, $L)", "null", bitLabels);
            constructor2.addParameter(byte[].class, "bits")
                        .addStatement("super($N, $L, $L)", "bits", "false", bitLabels);
        } else {
            sizeField.initializer("$L", definition.getRangeMin());

            int size = ((Integer.parseInt(definition.getRangeMin()) + 7) & ~7) / 8;
            constructor1.addStatement("super($L, $L)", size, bitLabels);
            if (size > 1) {
                constructor2.addParameter(byte[].class, "bits")
                        .addStatement("super($N, $L, $L)", "bits", "true", bitLabels);
            } else {
                constructor2.addParameter(byte.class, "bits")
                        .addStatement("super(new byte[]{$N}, $L, $L)", "bits", "true", bitLabels);
            }

            if (definition.getSubDefs() != null && !definition.getSubDefs().isEmpty()) {
                constructor3.addStatement("super(new byte[$L], $L, $L)", size, "true", bitLabels);
                for (Definition subDef : definition.getSubDefs()) {
                    constructor3.addParameter(boolean.class, subDef.getIdentifier())
                            .addStatement("setBit($L, $L)", subDef.getBodyText(), subDef.getIdentifier());
                }
            }
        }

        TypeSpec.Builder bitStringPoet = TypeSpec.classBuilder(StringUtil.delThroughline(definition.getIdentifier()))
                .addAnnotation(getGeneratedAnno(definition))
                .addModifiers(context.isInnerClass() ? new Modifier[]{Modifier.PUBLIC, Modifier.STATIC} : new Modifier[]{Modifier.PUBLIC})
                .superclass(JavaPoetUtil.primitiveTypeName(context))
                .addField(sizeField.build())
                .addMethod(constructor1.build())
                .addMethod(constructor2.build());
        if (definition.getSubDefs() != null && !definition.getSubDefs().isEmpty()) {
            bitStringPoet.addMethod(constructor3.build());
        }
        return bitStringPoet.build();
    }
}
