package com.inferiority.asn1.plugin;

import com.inferiority.asn1.analysis.Analyzer;
import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.analysis.model.Module;
import com.inferiority.asn1.mapping.Mapping;
import com.inferiority.asn1.mapping.model.MappingContext;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author cuijiufeng
 * @Class GenerateClassMojo
 * @Date 2023/3/21 15:46
 */
@Mojo(name= "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE)
public class GenerateClassMojo extends AbstractMojo {
    private final Mapping MAPPING = new Mapping();

    @Component
    protected MavenProjectHelper projectHelper;

    @Parameter(defaultValue = "${project}", required = true, readonly = true )
    private MavenProject project;
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/asn1-java", required = true, readonly = true)
    private String outputDirectory;
    @Parameter(name = "sourceFile", required = true, readonly = true)
    private File sourceFile;
    @Parameter(name = "attachSourceFile", defaultValue = "true", readonly = true)
    private boolean attachSourceFile;
    @Parameter(name = "packageMapping", readonly = true)
    private Map<String, String> packageMapping;
    @Parameter(name = "ignoreErrorDefinition", defaultValue = "false", readonly = true)
    private boolean ignoreErrorDefinition;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            checkParameters();
        } catch (final Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }

        getLog().info(String.format("asn1 file path: %s", sourceFile.getPath()));
        List<Module> modules;
        try {
            modules = new Analyzer(new FileInputStream(sourceFile)).analyzer();
        } catch (FileNotFoundException e) {
            throw new MojoFailureException("asn1 file not found", e);
        }
        for (Module module : modules) {
            for (Definition definition : module.getDefinitions()) {
                try {
                    String packageName = packageMapping.getOrDefault(definition.getModule().getIdentifier(), definition.getModule().getIdentifier());
                    MAPPING.mapping(new MappingContext(outputDirectory, packageName, definition));
                    getLog().debug(String.format("definition of %s in module %s mapping to class", definition.getIdentifier(), module.getIdentifier()));
                } catch (Exception e) {
                    if (ignoreErrorDefinition) {
                        getLog().warn(String.format("definition of %s in module %s mapping to class error, \n\te --> %s",
                                definition.getIdentifier(), module.getIdentifier(), e));
                        continue;
                    }
                    throw new MojoFailureException(String.format("definition of %s in module %s mapping to class error",
                            definition.getIdentifier(), module.getIdentifier()), e);
                }
            }
        }
        doAttachFiles();
    }

    private void checkParameters() throws MojoExecutionException {
        Objects.requireNonNull(project, "'project' is null");
        Objects.requireNonNull(outputDirectory, "'classes directory' is null");
        Objects.requireNonNull(sourceFile, "'source file' is null");
        if (new File(outputDirectory).isFile()) {
            throw new MojoExecutionException("'classes directory' is a file, not a directory");
        }
        if (!sourceFile.isFile()) {
            throw new MojoExecutionException("'source file' not a file");
        }
        if (!sourceFile.exists()) {
            throw new MojoExecutionException("'source file' not exists");
        }
    }

    private void doAttachFiles() {
        if (attachSourceFile) {
            //拷贝asn1文件到资源文件
            projectHelper.addResource(project, sourceFile.getParent(),
                    Collections.singletonList(sourceFile.getName()), Collections.emptyList());
        }
        project.addCompileSourceRoot(outputDirectory);
    }
}
