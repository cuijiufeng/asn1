package com.inferiority.asn1.mapping.mapping;

import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.codec.oer.ASN1Integer;
import com.inferiority.asn1.mapping.model.MappingContext;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.Generated;
import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

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

        FieldSpec rangeMin = FieldSpec.builder(BigInteger.class, "RANGE_MIN")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("new BigInteger($S)", definition.getRangeMin())
                .build();
        FieldSpec rangeMax = FieldSpec.builder(BigInteger.class, "RANGE_MAX")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("new BigInteger($S)", definition.getRangeMax())
                .build();
        MethodSpec constructor1 = MethodSpec.constructorBuilder()
                .addStatement("super($N, $N)", rangeMin, rangeMax)
                .build();
        MethodSpec constructor2 = MethodSpec.constructorBuilder()
                .addParameter(int.class, "value")
                .addStatement("super(new BigInteger(String.valueOf($N)), $N, $N)", "value", rangeMin, rangeMax)
                .build();
        AnnotationSpec GeneratedAnno = AnnotationSpec.builder(Generated.class)
                .addMember("value", "$S", "by " + this.getClass().getSimpleName() + " generated")
                .addMember("comments", "$S", "Source: " + definition.getModule().getIdentifier() + " --> " + definition.getIdentifier())
                .build();
        TypeSpec integerPoet = TypeSpec.classBuilder(definition.getIdentifier())
                .addModifiers(Modifier.PUBLIC)
                .addField(rangeMin)
                .addField(rangeMax)
                .addAnnotation(GeneratedAnno)
                .superclass(ASN1Integer.class)
                .addMethod(constructor1)
                .addMethod(constructor2)
                .build();
        //申明一个java文件输出对象
        JavaFile javaFile = JavaFile
                .builder(context.getPackageName(), integerPoet)
                .build();
        //输出文件
        javaFile.writeTo(new File(context.getOutputPath()));
    }
}
