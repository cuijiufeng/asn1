package com.inferiority.asn1.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * @author cuijiufeng
 * @Class GenerateClassMojo
 * @Date 2023/3/21 15:46
 */
@Mojo(name= "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateClassMojo extends AbstractMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info( "Hello, world." );
    }
}
