package com.inferiority.asn1.mapping.mapping;

import com.inferiority.asn1.analysis.common.Operator;
import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.analysis.util.RegexUtil;
import com.inferiority.asn1.codec.oer.ASN1Sequence;
import com.inferiority.asn1.mapping.model.MappingContext;
import com.inferiority.asn1.mapping.utils.JavaPoetUtil;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author cuijiufeng
 * @date 2023/3/25 14:35
 */
public class SequenceMapping extends AbstractMapping {
    public static final SequenceMapping MAPPING = new SequenceMapping();

    @Override
    protected TypeSpec mappingInternal(MappingContext context) {
        Definition definition = context.getDefinition();

        int extensionIdx = Integer.MAX_VALUE;
        List<TypeSpec> subDefInnerClasses = new ArrayList<>();
        for (int i = 0; i < definition.getSubDefs().size(); i++) {
            Definition subDef = definition.getSubDefs().get(i);
            if (!RegexUtil.matches(Operator.ELLIPSIS, subDef.getIdentifier())) {
                subDefInnerClass(context, subDef, subDefInnerClasses::add);
            } else {
                extensionIdx = i;
            }
        }

        MethodSpec.Builder constructor1 = MethodSpec.constructorBuilder()
                .addStatement("super($L)", extensionIdx != Integer.MAX_VALUE);
        MethodSpec.Builder constructor2 = MethodSpec.constructorBuilder()
                .addStatement("super($L)", extensionIdx != Integer.MAX_VALUE);

        for (int i = 0; i < definition.getSubDefs().size(); i++) {
            Definition subDef = definition.getSubDefs().get(i);
            Map.Entry<String, Object[]> newValue = JavaPoetUtil.builderNewStatement(subDef, false);
            Map.Entry<String, Object[]> newDefault = JavaPoetUtil.builderNewStatement(subDef, true);
            Object[] valueArgs = {subDef.getIdentifier(), i, i > extensionIdx, subDef.getOptional()};
            Object[] args = Arrays.copyOf(valueArgs, valueArgs.length + newValue.getValue().length + newDefault.getValue().length);
            System.arraycopy(newValue.getValue(), 0, args, valueArgs.length, newValue.getValue().length);
            System.arraycopy(newDefault.getValue(), 0, args, valueArgs.length + newValue.getValue().length, newDefault.getValue().length);
            constructor1.addStatement("setElement($S, $L, $L, $L, " + newValue.getKey() + ", " + newDefault.getKey() + ")", args);
        }

        TypeSpec.Builder sequencePoet = TypeSpec.classBuilder(definition.getIdentifier())
                .addAnnotation(getGeneratedAnno(definition))
                .addModifiers(context.isInnerClass() ? new Modifier[]{Modifier.PUBLIC, Modifier.STATIC} : new Modifier[]{Modifier.PUBLIC})
                .superclass(ASN1Sequence.class)
                .addMethod(constructor1.build())
                .addMethod(constructor2.build());
        subDefInnerClasses.forEach(sequencePoet::addType);
        return sequencePoet.build();
    }
}
