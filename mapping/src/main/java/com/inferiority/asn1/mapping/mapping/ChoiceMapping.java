package com.inferiority.asn1.mapping.mapping;

import com.inferiority.asn1.analysis.common.Operator;
import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.analysis.util.RegexUtil;
import com.inferiority.asn1.codec.oer.ASN1Choice;
import com.inferiority.asn1.codec.oer.ASN1Object;
import com.inferiority.asn1.mapping.model.MappingContext;
import com.inferiority.asn1.mapping.utils.JavaPoetUtil;
import com.inferiority.asn1.mapping.utils.StringUtil;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.util.Map;

/**
 * @author cuijiufeng
 * @date 2023/3/25 14:34
 */
public class ChoiceMapping extends AbstractMapping {
    public static final ChoiceMapping MAPPING = new ChoiceMapping();

    @Override
    protected TypeSpec mappingInternal(MappingContext context) {
        Definition definition = context.getDefinition();

        TypeSpec.Builder enumBuilder = TypeSpec.enumBuilder(context.getEnumPrefix() + definition.getIdentifier() + context.getEnumSuffix())
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ASN1Choice.ASN1ChoiceEnum.class);
        boolean isExtension = false;
        for (Definition subDef : definition.getSubDefs()) {
            if (!RegexUtil.matches(Operator.ELLIPSIS, subDef.getIdentifier())) {
                Map.Entry<String, Object[]> returnStatement = JavaPoetUtil.builderReturnStatement(subDef);
                TypeSpec typeSpec = TypeSpec.anonymousClassBuilder("")
                        .addMethod(MethodSpec.methodBuilder("isExtension")
                                .addAnnotation(Override.class)
                                .addModifiers(Modifier.PUBLIC)
                                .returns(boolean.class)
                                .addStatement("return $L", isExtension)
                                .build())
                        .addMethod(MethodSpec.methodBuilder("getInstance")
                                .addAnnotation(Override.class)
                                .addModifiers(Modifier.PUBLIC)
                                .returns(ASN1Object.class)
                                .addStatement(returnStatement.getKey(), returnStatement.getValue())
                                .build())
                        .build();
                enumBuilder.addEnumConstant(StringUtil.throughline2Underline(subDef.getIdentifier()), typeSpec);
            } else {
                isExtension = true;
            }
        }
        TypeSpec enumPoet = enumBuilder.build();

        MethodSpec constructor1 = MethodSpec.constructorBuilder()
                .addParameter(ParameterizedTypeName.get(
                        ClassName.get(Class.class),
                        ClassName.bestGuess(context.getEnumPrefix() + definition.getIdentifier() + context.getEnumSuffix())), "clazz")
                .addStatement("super($N)", "clazz")
                .build();
        MethodSpec constructor2 = MethodSpec.constructorBuilder()
                .addParameter(ClassName.bestGuess(context.getEnumPrefix() + definition.getIdentifier() + context.getEnumSuffix()), "choice")
                .addParameter(ASN1Object.class, "value")
                .addStatement("super($N, $N)", "choice", "value")
                .build();

        TypeSpec.Builder choicePoet = TypeSpec.classBuilder(definition.getIdentifier())
                .addModifiers(Modifier.PUBLIC)
                .superclass(ASN1Choice.class)
                .addAnnotation(getGeneratedAnno(definition))
                .addType(enumPoet)
                .addMethod(constructor1)
                .addMethod(constructor2);
        return choicePoet.build();
    }
}
