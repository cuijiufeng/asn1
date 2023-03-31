package io.inferiority.asn1.plugin;

import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;
import org.eclipse.aether.DefaultRepositorySystemSession;

import java.io.File;

public class GenerateClassMojoTest extends AbstractMojoTestCase {

    /**
     * @throws Exception
     */
    public void testMojoGenerateGoal() throws Exception {
        File pom = getTestFile( "src/it/project-to-test/pom.xml" );
        assertNotNull( pom );
        assertTrue( pom.exists() );

        ProjectBuildingRequest configuration = new DefaultMavenExecutionRequest()
                .getProjectBuildingRequest()
                .setRepositorySession(new DefaultRepositorySystemSession());
        MavenProject project = lookup(ProjectBuilder.class).build(pom, configuration).getProject();
        GenerateClassMojo mojo = (GenerateClassMojo) lookupConfiguredMojo(project, "generate");
        mojo.execute();
    }
}