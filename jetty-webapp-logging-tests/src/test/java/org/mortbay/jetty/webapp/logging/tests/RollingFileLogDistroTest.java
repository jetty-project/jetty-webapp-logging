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

package org.mortbay.jetty.webapp.logging.tests;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.start.FS;
import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.junit.jupiter.api.Test;
import org.mortbay.jetty.tests.releases.JettyHomeTester;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RollingFileLogDistroTest extends AbstractTest
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
            .mavenLocalRepository(System.getProperty("mavenRepoPath"))
            .build();

        // Copy Test Wars & Configs
        for (String context : CONTEXTS)
        {
            File warFile = jetty.resolveArtifact("org.mortbay.jetty.testwars:" + context + ":" + TEST_WAR_VERSION + ":war");
            jetty.installWarFile(warFile, context);
            File configJarFile = jetty.resolveArtifact("org.mortbay.jetty.testwars:" + context + ":" + TEST_WAR_VERSION + ":jar:config");
            jetty.installConfigurationJar(configJarFile);
        }

        // Copy Slf4j Libs
        Path libLogging = jetty.getJettyBase().resolve("lib/logging");
        FS.ensureDirectoryExists(libLogging);
        jetty.installJarLib("org.slf4j:slf4j-api", libLogging);
        jetty.installJarLib("org.slf4j:jcl-over-slf4j", libLogging);
        jetty.installJarLib("org.slf4j:jul-to-slf4j", libLogging);
        jetty.installJarLib("org.slf4j:log4j-over-slf4j", libLogging);

        // Copy Logback Libs
        jetty.installJarLib("ch.qos.logback:logback-core", libLogging);
        jetty.installJarLib("ch.qos.logback:logback-classic", libLogging);

        // Copy webapp logging lib
        jetty.installJarLib("org.mortbay.jetty.extras:jetty-webapp-logging", libLogging);
        File configJarFile = jetty.resolveArtifact("org.mortbay.jetty.extras:jetty-webapp-logging:jar:config");
        jetty.installConfigurationJar(configJarFile);

        // Overlay Manual Config
        Path basicDir = MavenTestingUtils.getTestResourcePathDir("rolling");
        jetty.installConfigurationDir(basicDir);

        int httpPort = jetty.freePort();

        String[] args = {
            "jetty.http.port=" + httpPort
        };

        Path logsDir = jetty.getJettyBase().resolve("logs");
        LogAssert.assertNoLogsRegex(logsDir, "jetty-roll-20[0-9][0-9]-[01][0-9]-[0-3][0-9]_[012][0-9]-[0-5][0-9].log");

        try (JettyHomeTester.Run run1 = jetty.start(args))
        {
            assertTrue(run1.awaitConsoleLogsFor("Started @", 20, TimeUnit.SECONDS));

            HttpClient client = startHttpClient();

            long now = System.currentTimeMillis();
            long duration = (long)(1000 * 60 * (1.5)); // 1.5 minutes
            long end = now + duration;
            long left;
            while (System.currentTimeMillis() < end)
            {
                left = (end - System.currentTimeMillis());
                System.out.printf("%,d milliseconds left%n", left);
                for (String context : CONTEXTS)
                {
                    ContentResponse response = client.GET("http://localhost:" + httpPort + "/" + context + "/logging");
                    assertEquals(HttpStatus.OK_200, response.getStatus());
                    assertThat(response.getContentAsString(), containsString("PathInfo"));
                    assertThat(response.getContentAsString(), not(containsString("<%")));
                }
                Thread.sleep(Math.min(20000, left)); // every 20s.
            }
        }

        LogAssert.assertLogExistsRegex(logsDir, "jetty-roll-20[0-9][0-9]-[01][0-9]-[0-3][0-9]_[012][0-9]-[0-5][0-9].log");
    }
}
