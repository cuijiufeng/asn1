package com.inferiority.asn1.plugin;

import com.inferiority.asn1.analysis.Analyzer;
import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.analysis.model.Module;
import com.inferiority.asn1.mapping.Mapping;
import com.inferiority.asn1.mapping.model.MappingContext;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

/**
 * @author cuijiufeng
 * @Class GenerateClassMojo
 * @Date 2023/3/21 15:46
 */
@Mojo(name= "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateClassMojo extends AbstractMojo {
    private final Mapping MAPPING = new Mapping();

    @Parameter(defaultValue = "${project}", required = true, readonly = true )
    private MavenProject project;
    @Parameter(name = "asn1FilePath", required = true, readonly = true)
    private String asn1FilePath;
    @Parameter(name = "outputPath", defaultValue = "${project.build.directory}/classes", required = false, readonly = true)
    private String outputPath;
    @Parameter(name = "packageMapping", required = false, readonly = true)
    private Map<String, String> packageMapping;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info(String.format("asn1 file path: %s", asn1FilePath));
        List<Module> modules;
        try {
            modules = new Analyzer(new FileInputStream(asn1FilePath)).analyzer();
        } catch (FileNotFoundException e) {
            throw new MojoFailureException("asn1 file not found", e);
        }
        for (Module module : modules) {
            for (Definition definition : module.getDefinitions()) {
                try {
                    MAPPING.mapping(new MappingContext(outputPath, packageMapping.get(definition.getModule().getIdentifier()), definition));
                } catch (Exception e) {
                    throw new MojoFailureException("asn1 struct mapping to class error", e);
                }
            }
        }
    }
}
