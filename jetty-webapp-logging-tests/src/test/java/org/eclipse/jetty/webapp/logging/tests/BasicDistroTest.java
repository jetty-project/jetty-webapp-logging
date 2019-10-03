//
//  ========================================================================
//  Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.eclipse.jetty.webapp.logging.tests;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.tests.JettyHomeTester;
import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BasicDistroTest extends AbstractTest
{
    private static final String[] CONTEXTS =
        {
            "test-war-commons_logging_1.1",
            "test-war-java_util_logging",
            "test-war-log4j_1.2.15",
            "test-war-slf4j_1.5.6"
        };

    @Test
    public void testLogging() throws Exception
    {
        JettyHomeTester jetty = JettyHomeTester.Builder.newInstance()
            .jettyVersion(getJettyVersion())
            .mavenLocalRepository(getMavenLocalRepoPath())
            .build();

        // Unpack logging config
        File configJarFile = jetty.resolveArtifact("org.eclipse.jetty:jetty-webapp-logging:jar:config:" + getProjectVersion());
        jetty.installConfigurationJar(configJarFile);

        String[] setupArgs = {
            "--approve-all-licenses",
            "--add-to-start=http,deploy,centralized-webapp-logging"
        };

        // Setup the jetty instance
        try (JettyHomeTester.Run setup = jetty.start(setupArgs))
        {
            assertTrue(setup.awaitFor(5, TimeUnit.SECONDS));
            assertEquals(0, setup.getExitValue());

            // Copy Test Wars & Configs
            for (String context : CONTEXTS)
            {
                File warFile = jetty.resolveArtifact("org.mortbay.jetty.testwars:" + context + ":war:" + TEST_WAR_VERSION);
                jetty.installWarFile(warFile, context);
                configJarFile = jetty.resolveArtifact("org.mortbay.jetty.testwars:" + context + ":jar:config:" + TEST_WAR_VERSION);
                jetty.installConfigurationJar(configJarFile);
            }

            // Overlay Manual Config
            Path basicDir = MavenTestingUtils.getTestResourcePathDir("basic");
            jetty.installConfigurationDir(basicDir);

            int httpPort = jetty.freePort();

            String[] runArgs = {
                "jetty.http.port=" + httpPort
            };

            // Run the server instance
            try (JettyHomeTester.Run run = jetty.start(runArgs))
            {
                assertTrue(run.awaitConsoleLogsFor("Started @", 3, TimeUnit.SECONDS));

                HttpClient client = startHttpClient();

                for (String context : CONTEXTS)
                {
                    ContentResponse response = client.GET("http://localhost:" + httpPort + "/" + context + "/logging");
                    assertHttpResponseOK(response);
                    assertThat(response.getContentAsString(), not(containsString("Exception")));
                }
            }

            Path logFile = jetty.getJettyBase().resolve("logs/jetty-central.log");
            LogAssert.assertContainsEntries(logFile, "expected-basic.txt");
        }
    }
}
