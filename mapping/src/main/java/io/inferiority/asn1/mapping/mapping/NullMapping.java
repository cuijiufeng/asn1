package io.inferiority.asn1.mapping.mapping;

import com.squareup.javapoet.TypeSpec;
import io.inferiority.asn1.analysis.model.Definition;
import io.inferiority.asn1.codec.oer.ASN1Null;
import io.inferiority.asn1.mapping.model.MappingContext;

import javax.lang.model.element.Modifier;

/**
 * @author cuijiufeng
 * @date 2023/3/25 13:08
 */
public class NullMapping extends AbstractMapping {
    public static final NullMapping MAPPING = new NullMapping();

    @Override
    protected TypeSpec mappingInternal(MappingContext context) {
        Definition definition = context.getDefinition();

        TypeSpec.Builder nullPoet = TypeSpec.classBuilder(definition.getIdentifier())
                .addAnnotation(getGeneratedAnno(definition))
                .addModifiers(context.isInnerClass() ? new Modifier[]{Modifier.PUBLIC, Modifier.STATIC} : new Modifier[]{Modifier.PUBLIC})
                .superclass(ASN1Null.class);
        valuesField(nullPoet, definition, (field, value) -> field.initializer("new $N($L)", nullPoet.build(), value));
        return nullPoet.build();
    }
}
