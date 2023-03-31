package io.inferiority.asn1.mapping;

import io.inferiority.asn1.mapping.mapping.AbstractMapping;
import io.inferiority.asn1.mapping.model.MappingContext;

/**
 * @author cuijiufeng
 * @Class Mapping
 * @Date 2023/3/21 11:55
 */
public class Mapping {
    public Mapping() {
    }

    public void mapping(final MappingContext context) {
        AbstractMapping instance = AbstractMapping.getInstance(context);
        instance.mapping(context);
    }
}
