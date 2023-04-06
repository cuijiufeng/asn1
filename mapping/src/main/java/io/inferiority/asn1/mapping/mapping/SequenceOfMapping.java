package io.inferiority.asn1.mapping.mapping;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import io.inferiority.asn1.analysis.model.Definition;
import io.inferiority.asn1.mapping.model.MappingContext;
import io.inferiority.asn1.mapping.utils.JavaPoetUtil;
import io.inferiority.asn1.mapping.utils.StringUtil;

import javax.lang.model.element.Modifier;

/**
 * @author cuijiufeng
 * @date 2023/3/25 1:48
 */
public class SequenceOfMapping extends AbstractMapping {
    public static final SequenceOfMapping MAPPING = new SequenceOfMapping();

    @Override
    protected TypeSpec mappingInternal(MappingContext context) {
        Definition definition = context.getDefinition();
        String[] split = definition.getPrimitiveType().split("[ ]+", 3);
        String primitiveType = split[split.length - 1];

        MethodSpec constructor1 = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("super($T::new)", ClassName.bestGuess(primitiveType))
                .build();
        MethodSpec constructor2 = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ArrayTypeName.of(ClassName.bestGuess(primitiveType)), "sequences")
                .addStatement("super($N)", "sequences")
                .build();
        TypeSpec.Builder sequenceOfPoet = TypeSpec.classBuilder(StringUtil.delThroughline(definition.getIdentifier()))
                .addAnnotation(getGeneratedAnno(definition))
                .addModifiers(context.isInnerClass() ? new Modifier[]{Modifier.PUBLIC, Modifier.STATIC} : new Modifier[]{Modifier.PUBLIC})
                .superclass(JavaPoetUtil.primitiveTypeName(context))
                .addMethod(constructor1)
                .addMethod(constructor2);
        return sequenceOfPoet.build();
    }
}
