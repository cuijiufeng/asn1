package com.inferiority.asn1.mapping;

import com.inferiority.asn1.mapping.mapping.AbstractMapping;
import com.inferiority.asn1.mapping.model.MappingContext;
import javassist.NotFoundException;

/**
 * @author cuijiufeng
 * @Class Mapping
 * @Date 2023/3/21 11:55
 */
public class Mapping {
    public Mapping() {
    }

    public void mapping(final MappingContext context) throws NotFoundException {
        AbstractMapping instance = AbstractMapping.getInstance(context);
        instance.mapping(context);
    }
}
