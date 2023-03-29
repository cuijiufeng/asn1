package com.inferiority.asn1.mapping.mapping;

import com.inferiority.asn1.mapping.model.MappingContext;
import com.squareup.javapoet.TypeSpec;

/**
 * @author cuijiufeng
 * @date 2023/3/25 14:35
 */
public class SequenceMapping extends AbstractMapping {
    public static final SequenceMapping MAPPING = new SequenceMapping();

    @Override
    protected TypeSpec mappingInternal(MappingContext context) {
        return null;
    }
}
