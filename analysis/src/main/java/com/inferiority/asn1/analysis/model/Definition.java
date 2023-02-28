package com.inferiority.asn1.analysis.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.AbstractMap;

/**
 * @author cuijiufeng
 * @Class Definition
 * @Date 2023/2/24 16:11
 */
@Getter
@Setter
@ToString
public class Definition {
    private String identifier;
    private String primitiveType;
    private String definitionText;
    private String bodyText;
    private AbstractMap.SimpleEntry<String, String>[] subDefs;
    private String rangeMin;
    private String rangeMax;
    private String size;
    private AbstractMap.SimpleEntry<String, String>[] values;
}
