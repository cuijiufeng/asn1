package com.inferiority.asn1.mapping.mapping;

import com.inferiority.asn1.analysis.analyzer.AbstractAnalyzer;
import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.analysis.util.RegexUtil;
import com.inferiority.asn1.codec.oer.ASN1BitString;
import com.inferiority.asn1.mapping.model.MappingContext;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author cuijiufeng
 * @date 2023/3/25 14:34
 */
public class BitStringMapping extends AbstractMapping {
    public static final BitStringMapping MAPPING = new BitStringMapping();

    @Override
    protected void mappingInternal(MappingContext context) throws IOException {
        Definition definition = context.getDefinition();

        assert 0 == Objects.compare(definition.getRangeMin(), definition.getRangeMax(), String.CASE_INSENSITIVE_ORDER);

        FieldSpec.Builder sizeField = FieldSpec.builder(Integer.class, "SIZE", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);

        MethodSpec.Builder constructor1 = MethodSpec.constructorBuilder();
        MethodSpec.Builder constructor2 = MethodSpec.constructorBuilder();

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

            int size = Integer.parseInt(definition.getRangeMin());
            constructor1.addStatement("super($L, $L)", ((size + 7) & ~7) / 8, bitLabels);
            if (size > 1) {
                constructor2.addParameter(byte[].class, "bits")
                        .addStatement("super($N, $L, $L)", "bits", "true", bitLabels);
            } else {
                constructor2.addParameter(byte.class, "bits")
                        .addStatement("super(new byte[]{$N}, $L, $L)", "bits", "true", bitLabels);
            }
        }

        TypeSpec.Builder bitStringPoet = TypeSpec.classBuilder(definition.getIdentifier())
                .addModifiers(Modifier.PUBLIC)
                .superclass(ASN1BitString.class)
                .addAnnotation(getGeneratedAnno(definition))
                .addField(sizeField.build())
                .addMethod(constructor1.build())
                .addMethod(constructor2.build());

        //申明一个java文件输出对象
        JavaFile javaFile = JavaFile
                .builder(context.getPackageName(), bitStringPoet.build())
                .build();
        //输出文件
        javaFile.writeTo(new File(context.getOutputPath()));
    }
}
