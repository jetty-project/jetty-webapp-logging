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

import java.nio.file.Paths;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.component.LifeCycle;
import org.junit.jupiter.api.AfterEach;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractTest
{
    public static final String JETTY_VERSION = "9.4.20.v20190813";
    public static final String TEST_WAR_VERSION = "9.0.0.v20130315";

    private HttpClient client;

    public String getJettyVersion()
    {
        String jettyVersion = System.getProperty("jettyVersion");
        if (StringUtil.isBlank(jettyVersion))
        {
            jettyVersion = JETTY_VERSION;
        }
        return jettyVersion;
    }

    public String getDependencyVersion(String depName)
    {
        String version = System.getProperty(depName + ".version");
        if (StringUtil.isBlank(version))
        {
            throw new RuntimeException("surefire configuration missing " + depName + ".version system property");
        }
        return version;
    }

    public String getSlf4jVersion()
    {
        return getDependencyVersion("slf4j");
    }

    public String getLogbackVersion()
    {
        return getDependencyVersion("logback");
    }

    public String getProjectVersion()
    {
        return getDependencyVersion("project");
    }

    public String getMavenLocalRepoPath()
    {
        String localRepoPath = System.getProperty("mavenRepoPath");
        if (StringUtil.isBlank(localRepoPath))
        {
            return Paths.get(System.getProperty("user.home"), ".m2/repository").toString();
        }
        return localRepoPath;
    }

    protected HttpClient startHttpClient() throws Exception
    {
        client = new HttpClient();
        client.start();
        return client;
    }

    @AfterEach
    public void stopClient()
    {
        LifeCycle.stop(client);
    }

    protected void assertHttpResponseOK(ContentResponse response)
    {
        if (response.getStatus() != HttpStatus.OK_200)
        {
            System.err.printf("Requested: %s%n", response.getRequest().getURI());
            System.err.printf("%s %s %s%n", response.getVersion(), response.getStatus(), response.getReason());
            System.err.println(response.getHeaders());
            System.err.println(response.getContentAsString());
            assertEquals(HttpStatus.OK_200, response.getStatus());
        }
    }
}
