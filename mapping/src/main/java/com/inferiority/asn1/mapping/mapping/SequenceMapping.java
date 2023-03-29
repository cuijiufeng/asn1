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

        int extensionIdx = -1;
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
                .addStatement("super($L)", extensionIdx != -1);
        MethodSpec.Builder constructor2 = MethodSpec.constructorBuilder()
                .addStatement("super($L)", extensionIdx != -1);

        for (int i = 0; i < definition.getSubDefs().size(); i++) {
            Definition subDef = definition.getSubDefs().get(i);
            Map.Entry<String, Object[]> newStatement = JavaPoetUtil.builderNewStatement(subDef);
            Object[] args = new Object[newStatement.getValue().length + 5];
            {subDef.getIdentifier(), i, i > extensionIdx, subDef.getOptional(), , };
            constructor1.addStatement("setElement($L, $L, $L, $L, " + newStatement.getKey() + ", $L)", args);
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
