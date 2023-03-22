package com.inferiority.asn1.plugin;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

public class GenerateClassMojoTest extends AbstractMojoTestCase {

    protected void setUp() throws Exception {
        // required for mojo lookups to work
        super.setUp();
    }

    protected void tearDown() throws Exception {
        // required
        super.tearDown();
    }

    /**
     * @throws Exception
     */
    public void testMojoGoal() throws Exception {
        File pom = getTestFile( "src/test/resources/project-to-test/pom.xml" );
        assertNotNull( pom );
        assertTrue( pom.exists() );

        GenerateClassMojo mojo = (GenerateClassMojo) lookupMojo( "generate", pom );
        assertNotNull( mojo );
        mojo.execute();
    }
}