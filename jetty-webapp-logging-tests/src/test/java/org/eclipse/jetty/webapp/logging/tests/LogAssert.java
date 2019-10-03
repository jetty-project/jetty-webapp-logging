package org.eclipse.jetty.webapp.logging.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.jetty.toolchain.test.IO;
import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.junit.jupiter.api.Assertions;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

public class LogAssert
{
    public static void assertContainsEntries(Path logFile, String expectedEntriesPath) throws IOException
    {
        File expectedEntriesFile = MavenTestingUtils.getTestResourceFile(expectedEntriesPath);
        List<String> expectedEntries = loadExpectedEntries(expectedEntriesFile);

        try (BufferedReader buf = Files.newBufferedReader(logFile))
        {
            String line;
            while ((line = buf.readLine()) != null)
            {
                line = line.trim();
                if (line.length() <= 0)
                {
                    continue; // skip
                }
                removeFoundEntries(expectedEntries, line);
            }

            if (expectedEntries.size() > 0)
            {
                for (String entry : expectedEntries)
                {
                    System.err.println("[Entry Not Found] " + entry);
                }

                Assertions.fail("Failed to find " + expectedEntries.size() + " entries (details found in STDERR output on this test case) in the log file at "
                    + logFile.toString());
            }
        }
    }

    private static void removeFoundEntries(List<String> expectedEntries, String line)
    {
        ListIterator<String> iter = expectedEntries.listIterator();
        while (iter.hasNext())
        {
            String entry = iter.next();
            if (line.contains(entry))
            {
                iter.remove();
            }
        }
    }

    private static List<String> loadExpectedEntries(File expectedEntriesFile) throws IOException
    {
        List<String> entries = new ArrayList<String>();
        FileReader reader = null;
        BufferedReader buf = null;
        try
        {
            reader = new FileReader(expectedEntriesFile);
            buf = new BufferedReader(reader);

            String line;
            while ((line = buf.readLine()) != null)
            {
                line = line.trim();
                if (line.length() <= 0)
                {
                    continue; // skip
                }
                entries.add(line);
            }

            return entries;
        }
        finally
        {
            IO.close(buf);
            IO.close(reader);
        }
    }

    public static void assertNoLogsRegex(Path logDir, String regexPath) throws IOException
    {
        Pattern pat = Pattern.compile(regexPath);

        System.out.printf("Looking for forbidden regex matches on %s in dir %s%n", pat.pattern(), logDir);

        List<Path> forbiddenMatches = Files.list(logDir)
            .filter((logFile) ->
            {
                String logFileName = logFile.getFileName().toString();
                return pat.matcher(logFileName).matches();
            })
            .collect(Collectors.toList());

        forbiddenMatches.forEach((forbidden) -> System.out.println("Found Forbidden Match: " + forbidden));

        assertThat("Forbidden Matches", forbiddenMatches.size(), is(0));
    }

    public static void assertLogExistsRegex(Path logDir, String regexPath) throws IOException
    {
        Pattern pat = Pattern.compile(regexPath);

        System.out.printf("Looking for regex matches on %s in dir %s%n", pat.pattern(), logDir);

        List<Path> matches = Files.list(logDir)
            .filter((logFile) ->
            {
                String logFileName = logFile.getFileName().toString();
                return pat.matcher(logFileName).matches();
            })
            .collect(Collectors.toList());

        matches.forEach((forbidden) -> System.out.println("Found Match: " + forbidden));

        assertThat("Matches", matches.size(), greaterThan(0));
    }
}
