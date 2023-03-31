package io.inferiority.asn1.mapping.model;

import io.inferiority.asn1.analysis.model.Definition;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;
import java.util.Objects;

/**
 * @author cuijiufeng
 * @Class MappingContext
 * @Date 2023/3/21 13:44
 */
@Setter
@Getter
@ToString
public class MappingContext {
    private final String outputPath;
    private final Map<String, String> packageMapping;
    private final Definition definition;
    private final String enumPrefix;
    private final String enumSuffix;
    private boolean innerClass = false;

    public MappingContext(String outputPath, Map<String, String> packageMapping, Definition definition, String enumPrefix, String enumSuffix) {
        Objects.requireNonNull(definition, "definition can't be null");
        Objects.requireNonNull(enumPrefix, "enum prefix can't be null");
        Objects.requireNonNull(enumSuffix, "enum suffix can't be null");
        this.outputPath = outputPath;
        this.packageMapping = packageMapping;
        this.definition = definition;
        this.enumPrefix = enumPrefix;
        this.enumSuffix = enumSuffix;
    }

    public MappingContext(String outputPath, Map<String, String> packageMapping, Definition definition, String enumPrefix, String enumSuffix, boolean innerClass) {
        this(outputPath, packageMapping, definition, enumPrefix, enumSuffix);
        this.innerClass = innerClass;
    }

    public MappingContext copy(Definition definition) {
        return new MappingContext(outputPath, packageMapping, definition, enumPrefix, enumSuffix, innerClass);
    }
}
