package com.inferiority.asn1.mapping.mapping;

import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.codec.oer.ASN1Boolean;
import com.inferiority.asn1.mapping.model.MappingContext;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author cuijiufeng
 * @date 2023/3/25 13:13
 */
public class BooleanMapping extends AbstractMapping {
    public static final BooleanMapping MAPPING = new BooleanMapping();

    @Override
    protected void mappingInternal(MappingContext context) throws IOException {
        Definition definition = context.getDefinition();

        MethodSpec constructor1 = MethodSpec.constructorBuilder()
                .addStatement("super()")
                .build();
        MethodSpec constructor2 = MethodSpec.constructorBuilder()
                .addParameter(boolean.class, "value")
                .addStatement("super($N)", "value")
                .build();

        TypeSpec.Builder booleanPoet = TypeSpec.classBuilder(definition.getIdentifier())
                .addModifiers(Modifier.PUBLIC)
                .superclass(ASN1Boolean.class)
                .addAnnotation(getGeneratedAnno(definition))
                .addMethod(constructor1)
                .addMethod(constructor2);
        if (definition.getValues() != null) {
            for (Map.Entry<String, String> entry : definition.getValues()) {
                FieldSpec fieldSpec = FieldSpec.builder(ClassName.bestGuess(definition.getIdentifier()), entry.getKey(), Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("new $N($L)", booleanPoet.build(), entry.getValue())
                        .build();
                booleanPoet.addField(fieldSpec);
            }
        }

        //申明一个java文件输出对象
        JavaFile javaFile = JavaFile
                .builder(context.getPackageName(), booleanPoet.build())
                .build();
        //输出文件
        javaFile.writeTo(new File(context.getOutputPath()));
    }
}
