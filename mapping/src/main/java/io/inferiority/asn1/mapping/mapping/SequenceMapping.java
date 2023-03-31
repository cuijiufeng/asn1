package io.inferiority.asn1.mapping.mapping;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import io.inferiority.asn1.analysis.common.Operator;
import io.inferiority.asn1.analysis.model.Definition;
import io.inferiority.asn1.analysis.util.ArrayUtil;
import io.inferiority.asn1.analysis.util.RegexUtil;
import io.inferiority.asn1.codec.oer.ASN1Sequence;
import io.inferiority.asn1.mapping.model.MappingContext;
import io.inferiority.asn1.mapping.utils.JavaPoetUtil;
import io.inferiority.asn1.mapping.utils.StringUtil;

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
            Map.Entry<String, Object[]> newValue1 = JavaPoetUtil.builderNewStatement(context, subDef, null, false);
            Map.Entry<String, Object[]> newDefault = JavaPoetUtil.builderNewStatement(context, subDef, null, true);
            Object[] args = {subDef.getIdentifier(), i, i > extensionIdx, subDef.getOptional()};
            constructor1.addStatement("setElement($S, $L, $L, $L, " + newValue1.getKey() + ", " + newDefault.getKey() + ")",
                    ArrayUtil.concat(args, newValue1.getValue(), newDefault.getValue()));
            Map.Entry<String, Object[]> newValue2 = JavaPoetUtil.builderNewStatement(context, subDef, subDef.getIdentifier(), false);
            constructor2.addParameter(JavaPoetUtil.javaTypeName(context, subDef), subDef.getIdentifier())
                    .addStatement("setElement($S, $L, $L, $L, " + newValue2.getKey() + "," + newDefault.getKey() + ")",
                            ArrayUtil.concat(args, newValue2.getValue(), newDefault.getValue()));
        }

        TypeSpec.Builder sequencePoet = TypeSpec.classBuilder(context.isInnerClass() ? StringUtil.throughline2hump(definition.getIdentifier()) : definition.getIdentifier())
                .addAnnotation(getGeneratedAnno(definition))
                .addModifiers(context.isInnerClass() ? new Modifier[]{Modifier.PUBLIC, Modifier.STATIC} : new Modifier[]{Modifier.PUBLIC})
                .superclass(ASN1Sequence.class)
                .addMethod(constructor1.build())
                .addMethod(constructor2.build());
        subDefInnerClasses.forEach(sequencePoet::addType);
        return sequencePoet.build();
    }
}
