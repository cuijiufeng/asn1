package com.inferiority.asn1.mapping.model;

import com.inferiority.asn1.analysis.model.Definition;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

/**
 * @author cuijiufeng
 * @Class MappingContext
 * @Date 2023/3/21 13:44
 */
@Getter
@ToString
public class MappingContext {
    private final Definition definition;

    public MappingContext(Definition definition) {
        Objects.requireNonNull(definition, "definition can't be null");
        this.definition = definition;
    }
}
