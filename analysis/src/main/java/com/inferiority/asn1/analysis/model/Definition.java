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
    private String definitionText;
    private String identifier;
    private String bodyText;
    private String primitiveType;
    private String sequenceOf;
    private String subBodyText;
    private List<Definition> subDefs;
    private String rangeMin;
    private String rangeMax;
    private String size;
    private Boolean optional;
    private String defaulted;
    private List<Map.Entry<String, String>> values;

    public Definition() {
    }

    public Definition(String identifier, String bodyText) {
        this.identifier = identifier;
        this.bodyText = bodyText;
    }
}
