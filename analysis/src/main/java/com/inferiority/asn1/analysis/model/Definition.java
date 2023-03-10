package com.inferiority.asn1.analysis.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

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
    private String sequenceOf;
    private String definitionText;
    private String bodyText;
    private List<Map.Entry<String, String>> subDefs;
    private String rangeMin;
    private String rangeMax;
    private String size;
    private List<Map.Entry<String, String>> values;
}
