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
    private final String outputPath;
    private final String packageName;
    private final Definition definition;
    private final String enumPrefix;
    private final String enumSuffix;
    private boolean anonymousClass = false;

    public MappingContext(String outputPath, String packageName, Definition definition, String enumPrefix, String enumSuffix) {
        Objects.requireNonNull(definition, "definition can't be null");
        Objects.requireNonNull(enumPrefix, "enum prefix can't be null");
        Objects.requireNonNull(enumSuffix, "enum suffix can't be null");
        this.outputPath = outputPath;
        this.packageName = packageName;
        this.definition = definition;
        this.enumPrefix = enumPrefix;
        this.enumSuffix = enumSuffix;
    }

    public MappingContext(String outputPath, String packageName, Definition definition, String enumPrefix, String enumSuffix, boolean anonymousClass) {
        this(outputPath, packageName, definition, enumPrefix, enumSuffix);
        this.anonymousClass = anonymousClass;
        if (anonymousClass) {
            definition.setIdentifier(definition.getIdentifier() + "Anonymous");
        }
    }
}
