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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PathUtils
{
    public static void assertDirExists(String description, Path path)
    {
        if (!Files.exists(path))
        {
            throw new RuntimeException(description + " does not exist: " + path);
        }

        if (!Files.isDirectory(path))
        {
            throw new RuntimeException(description + " not a directory: " + path);
        }
    }

    public static void assertFileExists(Path file)
    {
        if (!Files.exists(file))
        {
            throw new RuntimeException("Unable to find expected file: " + file.toString());
        }

        if (!Files.isRegularFile(file))
        {
            throw new RuntimeException("Not expected file (name conflict): " + file.toString());
        }
    }

    public static Path ensureDirExists(Path dir) throws IOException
    {
        if (Files.exists(dir))
        {
            if (!Files.isDirectory(dir))
            {
                throw new IOException("Unable to create directory (name conflict): " + dir);
            }
            return dir;
        }
        else
        {
            return Files.createDirectories(dir);
        }
    }
}
