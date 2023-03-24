package com.inferiority.asn1.mapping;

import com.inferiority.asn1.codec.oer.ASN1Integer;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.junit.Test;

import javax.annotation.Generated;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.math.BigInteger;

/**
 * @author cuijiufeng
 * @Class TestGenerateClass
 * @Date 2023/3/24 17:12
 */
public class GenerateClassTest {
    @Test
    public void testGenerateClass() throws IOException {
        FieldSpec rangeMin = FieldSpec.builder(BigInteger.class, "RANGE_MIN")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("new BigInteger($S)", 0)
                .build();
        FieldSpec rangeMax = FieldSpec.builder(BigInteger.class, "RANGE_MIN")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("new BigInteger($S)", 255)
                .build();
        MethodSpec constructor1 = MethodSpec.constructorBuilder()
                .addStatement("super($N, $N)", rangeMin, rangeMax)
                .build();
        MethodSpec constructor2 = MethodSpec.constructorBuilder()
                .addParameter(int.class, "value")
                .addStatement("super($N, $N, $N)", "value", rangeMin, rangeMax)
                .build();
        AnnotationSpec GeneratedAnno = AnnotationSpec.builder(Generated.class)
                .addMember("value", "fadsfdasfa")
                .addMember("comments", "")
                .build();
        TypeSpec.Builder integerPoet = TypeSpec.classBuilder("Uint3")
                .addModifiers(Modifier.PUBLIC)
                .addField(rangeMin)
                .addField(rangeMax)
                .addAnnotation(GeneratedAnno)
                .superclass(ASN1Integer.class)
                .addMethod(constructor1)
                .addMethod(constructor2);
        FieldSpec fieldSpec = FieldSpec.builder(ClassName.bestGuess("Uint3"), "UnknownLongitude", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("new $N($L)", integerPoet.build(), "900000001")
                .build();
        integerPoet.addField(fieldSpec);
        //申明一个java文件输出对象
        JavaFile javaFile = JavaFile
                .builder("a", integerPoet.build())
                .build();
        //输出文件
        javaFile.writeTo(System.out);
    }
}
