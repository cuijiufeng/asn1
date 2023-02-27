package com.inferiority.asn1.analysis.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author cuijiufeng
 * @Class Namespace
 * @Date 2023/2/24 16:00
 */
@Getter
@Setter
@ToString
public class Module {
    private String identifier;
    private String tagDefault;
    private String extensionDefault;
    private String[] exports;
    private String[] imports;
    private String[] dependencies;
    private String moduleBodyText;
    private List<Definition> definitions;
}
