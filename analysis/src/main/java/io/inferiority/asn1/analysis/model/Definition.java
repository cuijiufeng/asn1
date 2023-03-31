package io.inferiority.asn1.analysis.model;

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
    private List<String> sequenceParameters;
    private String primitiveType;
    private String subBodyText;
    private String rangeMin;
    private String rangeMax;
    @ToString.Exclude
    private List<Definition> subDefs;
    private String bodyText;
    private String optional;
    private String defaulted;
    private String constraintText;
    private List<Map.Entry<String, String>> values;

    //
    @ToString.Exclude
    private List<Definition> dependencies;
    @ToString.Exclude
    private Module module;

    public Definition() {
    }

    public Definition(String identifier, String bodyText) {
        this.identifier = identifier;
        this.bodyText = bodyText;
    }

    public boolean isParameterizedTypes() {
        return sequenceParameters != null;
    }
}
