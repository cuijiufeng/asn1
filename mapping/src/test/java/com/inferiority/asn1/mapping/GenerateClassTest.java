package com.inferiority.asn1.mapping;

import com.inferiority.asn1.codec.oer.ASN1Enumerated;
import com.inferiority.asn1.codec.oer.ASN1Integer;
import com.inferiority.asn1.codec.oer.ASN1SequenceOf;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import org.junit.Test;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.math.BigInteger;
import java.util.function.Supplier;

/**
 * @author cuijiufeng
 * @Class TestGenerateClass
 * @Date 2023/3/24 17:12
 */
public class GenerateClassTest {
    @Test
    public void testIntegerGenerateClass() throws IOException {
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
        TypeSpec.Builder integerPoet = TypeSpec.classBuilder("Uint3")
                .addModifiers(Modifier.PUBLIC)
                .addField(rangeMin)
                .addField(rangeMax)
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

    @Test
    public void testSequenceOfGeneratedClass() throws IOException {
        MethodSpec constructor1 = MethodSpec.constructorBuilder()
                .addParameter(Supplier.class, "instance")
                .addStatement("super($N)", "instance")
                .build();
        MethodSpec constructor2 = MethodSpec.constructorBuilder()
                .addParameter(ArrayTypeName.of(ClassName.bestGuess("Uint3")), "sequences")
                .addStatement("super($N)", "sequences")
                .build();
        TypeSpec.Builder integerPoet = TypeSpec.classBuilder("SequenceOfUint3")
                .addModifiers(Modifier.PUBLIC)
                .superclass(ParameterizedTypeName.get(ClassName.get(ASN1SequenceOf.class), ClassName.bestGuess("SequenceOfUint3")))
                .addMethod(constructor1)
                .addMethod(constructor2);
        //申明一个java文件输出对象
        JavaFile javaFile = JavaFile
                .builder("a", integerPoet.build())
                .build();
        //输出文件
        javaFile.writeTo(System.out);
    }

    @Test
    public void testEnumeratedGeneratedClass() throws IOException {

        TypeSpec enumPoet = TypeSpec.enumBuilder("SymmAlgorithmType")
                .addModifiers(Modifier.PUBLIC)
                .addEnumConstant("aes128Ccm")
                .build();

        MethodSpec constructor1 = MethodSpec.constructorBuilder()
                .addParameter(int.class, "clazz")
                .addStatement("super($N)", "clazz")
                .build();
        MethodSpec constructor2 = MethodSpec.constructorBuilder()
                .addParameter(int.class, "enumerated")
                .addStatement("super($N)", "value")
                .build();

        TypeSpec.Builder enumeratedPoet = TypeSpec.classBuilder("SymmAlgorithm")
                .addModifiers(Modifier.PUBLIC)
                .superclass(ASN1Enumerated.class)
                .addType(enumPoet)
                .addMethod(constructor1)
                .addMethod(constructor2);

        //申明一个java文件输出对象
        JavaFile javaFile = JavaFile
                .builder("a", enumeratedPoet.build())
                .build();
        //输出文件
        javaFile.writeTo(System.out);
    }
}
