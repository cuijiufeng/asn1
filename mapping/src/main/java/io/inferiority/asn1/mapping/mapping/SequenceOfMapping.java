package io.inferiority.asn1.mapping.mapping;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import io.inferiority.asn1.analysis.model.Definition;
import io.inferiority.asn1.codec.oer.ASN1SequenceOf;
import io.inferiority.asn1.mapping.model.MappingContext;

import javax.lang.model.element.Modifier;
import java.util.function.Supplier;

/**
 * @author cuijiufeng
 * @date 2023/3/25 1:48
 */
public class SequenceOfMapping extends AbstractMapping {
    public static final SequenceOfMapping MAPPING = new SequenceOfMapping();

    @Override
    protected TypeSpec mappingInternal(MappingContext context) {
        Definition definition = context.getDefinition();
        String[] split = definition.getPrimitiveType().split("[ ]+");
        String primitiveType = split[split.length - 1];

        MethodSpec constructor1 = MethodSpec.constructorBuilder()
                .addParameter(ParameterizedTypeName.get(ClassName.get(Supplier.class), ClassName.bestGuess(primitiveType)), "instance")
                .addStatement("super($N)", "instance")
                .build();
        MethodSpec constructor2 = MethodSpec.constructorBuilder()
                .addParameter(ArrayTypeName.of(ClassName.bestGuess(primitiveType)), "sequences")
                .addStatement("super($N)", "sequences")
                .build();
        TypeSpec.Builder sequenceOfPoet = TypeSpec.classBuilder(definition.getIdentifier())
                .addAnnotation(getGeneratedAnno(definition))
                .addModifiers(context.isInnerClass() ? new Modifier[]{Modifier.PUBLIC, Modifier.STATIC} : new Modifier[]{Modifier.PUBLIC})
                .superclass(ParameterizedTypeName.get(ClassName.get(ASN1SequenceOf.class), ClassName.bestGuess(primitiveType)))
                .addMethod(constructor1)
                .addMethod(constructor2);
        return sequenceOfPoet.build();
    }
}
