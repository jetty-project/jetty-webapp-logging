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

package org.eclipse.jetty.tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public final class MavenUtils
{
    private static Path basePath;
    private static Path testResourcesPath;
    private static Path targetPath;

    public static Path resolveProjectBaseDir()
    {
        if (basePath == null)
        {
            String cwd = System.getProperty("basedir");

            if (cwd == null)
            {
                cwd = System.getProperty("user.dir");
            }

            try
            {
                basePath = new File(cwd).toPath().toRealPath();
            }
            catch (IOException e)
            {
                // if toRealPath() fails, fallback to as detected version.
                basePath = new File(cwd).getAbsoluteFile().toPath();
            }
        }

        return basePath;
    }

    public static Path resolveProjectBasePath(String path)
    {
        return resolveProjectBaseDir().resolve(path);
    }

    public static Path resolveProjectBaseFile(String path)
    {
        Path file = resolveProjectBaseDir(path);
        PathUtils.assertFileExists(file);
        return file;
    }

    public static Path resolveProjectBaseDir(String path)
    {
        Path dir = resolveProjectBasePath(path);
        PathUtils.assertDirExists("Expected", dir);
        return dir;
    }

    public static Path resolveTestResourceDir()
    {
        if (testResourcesPath == null)
        {
            testResourcesPath = resolveProjectBaseDir("src/test/resources");
            PathUtils.assertDirExists("Test Resources Dir", testResourcesPath);
        }
        return testResourcesPath;
    }

    public static Path resolveTestResourceDir(String path)
    {
        Path dir = resolveTestResourceDir().resolve(path);
        PathUtils.assertDirExists("Expected", dir);
        return dir;
    }

    public static Path resolveTestResourceFile(String path)
    {
        Path file = resolveTestResourceDir().resolve(path);
        PathUtils.assertFileExists(file);
        return file;
    }

    public static Path resolveTargetDir()
    {
        if (targetPath == null)
        {
            targetPath = resolveProjectBaseDir("target");
            PathUtils.assertDirExists("Target Dir", targetPath);
        }
        return targetPath;
    }

    public static Path resolveTargetTestingPath(String path)
    {
        return resolveTargetDir().resolve("tests/" + path);
    }
}
