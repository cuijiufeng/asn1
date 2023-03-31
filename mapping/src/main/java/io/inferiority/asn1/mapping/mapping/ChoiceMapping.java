package io.inferiority.asn1.mapping.mapping;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import io.inferiority.asn1.analysis.common.Operator;
import io.inferiority.asn1.analysis.model.Definition;
import io.inferiority.asn1.analysis.util.RegexUtil;
import io.inferiority.asn1.codec.oer.ASN1Choice;
import io.inferiority.asn1.codec.oer.ASN1Object;
import io.inferiority.asn1.mapping.model.MappingContext;
import io.inferiority.asn1.mapping.utils.JavaPoetUtil;
import io.inferiority.asn1.mapping.utils.StringUtil;

import javax.lang.model.element.Modifier;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
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
        List<TypeSpec> subDefInnerClasses = new ArrayList<>();

        if (definition.getSubDefs() != null && !definition.getSubDefs().isEmpty()) {
            for (Definition subDef : definition.getSubDefs()) {
                if (!RegexUtil.matches(Operator.ELLIPSIS, subDef.getIdentifier())) {
                    TypeName innerTypeName = subDefInnerClass(context, subDef, subDefInnerClasses::add);

                    Map.Entry<String, Object[]> newStatement = innerTypeName == null
                            ? JavaPoetUtil.builderNewStatement(context, subDef, null, false)
                            : new AbstractMap.SimpleEntry<>("new $T()", new Object[]{innerTypeName});
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
                                    .addStatement("return " + newStatement.getKey(), newStatement.getValue())
                                    .build())
                            .build();
                    enumBuilder.addEnumConstant(StringUtil.throughline2underline(subDef.getIdentifier()), typeSpec);
                } else {
                    isExtension = true;
                }
            }
        }
        TypeSpec enumPoet = enumBuilder.build();

        MethodSpec constructor1 = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("super()")
                .build();
        MethodSpec constructor2 = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.bestGuess(context.getEnumPrefix() + definition.getIdentifier() + context.getEnumSuffix()), "choice")
                .addParameter(ASN1Object.class, "value")
                .addStatement("super($N, $N)", "choice", "value")
                .build();

        TypeSpec.Builder choicePoet = TypeSpec.classBuilder(context.isInnerClass() ? StringUtil.throughline2hump(definition.getIdentifier(), true) : definition.getIdentifier())
                .addAnnotation(getGeneratedAnno(definition))
                .addModifiers(context.isInnerClass() ? new Modifier[]{Modifier.PUBLIC, Modifier.STATIC} : new Modifier[]{Modifier.PUBLIC})
                .superclass(JavaPoetUtil.primitiveTypeName(context))
                .addType(enumPoet)
                .addMethod(constructor1)
                .addMethod(constructor2);
        subDefInnerClasses.forEach(choicePoet::addType);
        return choicePoet.build();
    }
}
