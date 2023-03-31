package io.inferiority.asn1.mapping.mapping;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import io.inferiority.asn1.analysis.common.Operator;
import io.inferiority.asn1.analysis.model.Definition;
import io.inferiority.asn1.analysis.util.RegexUtil;
import io.inferiority.asn1.codec.oer.ASN1Enumerated;
import io.inferiority.asn1.mapping.model.MappingContext;

import javax.lang.model.element.Modifier;

/**
 * @author cuijiufeng
 * @date 2023/3/25 13:19
 */
public class EnumeratedMapping extends AbstractMapping {
    public static final EnumeratedMapping MAPPING = new EnumeratedMapping();

    @Override
    protected TypeSpec mappingInternal(MappingContext context) {
        Definition definition = context.getDefinition();

        TypeSpec.Builder enumBuilder = TypeSpec.enumBuilder(context.getEnumPrefix() + definition.getIdentifier() + context.getEnumSuffix())
                .addModifiers(Modifier.PUBLIC);
        for (Definition subDef : definition.getSubDefs()) {
            if (!RegexUtil.matches(Operator.ELLIPSIS, subDef.getIdentifier())) {
                enumBuilder.addEnumConstant(subDef.getIdentifier());
            }
        }
        TypeSpec enumPoet = enumBuilder.build();

        MethodSpec constructor1 = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("super($T.class)", ClassName.bestGuess(context.getEnumPrefix() + definition.getIdentifier() + context.getEnumSuffix()))
                .build();
        MethodSpec constructor2 = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.bestGuess(context.getEnumPrefix() + definition.getIdentifier() + context.getEnumSuffix()), "enumerated")
                .addStatement("super($N)", "enumerated")
                .build();

        TypeSpec.Builder enumeratedPoet = TypeSpec.classBuilder(definition.getIdentifier())
                .addAnnotation(getGeneratedAnno(definition))
                .addModifiers(context.isInnerClass() ? new Modifier[]{Modifier.PUBLIC, Modifier.STATIC} : new Modifier[]{Modifier.PUBLIC})
                .superclass(ASN1Enumerated.class)
                .addType(enumPoet)
                .addMethod(constructor1)
                .addMethod(constructor2);
        return enumeratedPoet.build();
    }
}
