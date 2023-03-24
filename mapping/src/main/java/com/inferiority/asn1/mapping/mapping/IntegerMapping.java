package com.inferiority.asn1.mapping.mapping;

import com.inferiority.asn1.analysis.analyzer.AbstractAnalyzer;
import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.analysis.util.RegexUtil;
import com.inferiority.asn1.codec.oer.ASN1Integer;
import com.inferiority.asn1.mapping.model.MappingContext;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.Generated;
import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;

/**
 * @author cuijiufeng
 * @Class IntegerMapping
 * @Date 2023/3/21 14:50
 */
public class IntegerMapping extends AbstractMapping {
    public static final IntegerMapping MAPPING = new IntegerMapping();

    @Override
    public void mappingInternal(MappingContext context) throws IOException {
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
                .addStatement("super(new BigInteger(String.valueOf($N)), $N, $N)", "value", rangeMin.build(), rangeMax.build())
                .build();
        AnnotationSpec GeneratedAnno = AnnotationSpec.builder(Generated.class)
                .addMember("value", "$S", "by " + this.getClass().getSimpleName() + " generated")
                .addMember("comments", "$S", "Source: " + definition.getModule().getIdentifier() + " --> " + definition.getIdentifier())
                .build();
        TypeSpec.Builder integerPoet = TypeSpec.classBuilder(definition.getIdentifier())
                .addModifiers(Modifier.PUBLIC)
                .superclass(ASN1Integer.class)
                .addAnnotation(GeneratedAnno)
                .addField(rangeMin.build())
                .addField(rangeMax.build())
                .addMethod(constructor1)
                .addMethod(constructor2);
        if (definition.getValues() != null) {
            for (Map.Entry<String, String> entry : definition.getValues()) {
                FieldSpec fieldSpec = FieldSpec.builder(ClassName.bestGuess(definition.getIdentifier()), entry.getKey(), Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("new $N($L)", integerPoet.build(), entry.getValue())
                        .build();
                integerPoet.addField(fieldSpec);
            }
        }
        //申明一个java文件输出对象
        JavaFile javaFile = JavaFile
                .builder(context.getPackageName(), integerPoet.build())
                .build();
        //输出文件
        javaFile.writeTo(new File(context.getOutputPath()));
    }
}
