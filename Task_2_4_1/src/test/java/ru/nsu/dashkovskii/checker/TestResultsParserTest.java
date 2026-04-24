package ru.nsu.dashkovskii.checker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TestResultsParserTest {

    @Test
    void readsJunitXml(@TempDir Path dir) throws Exception {
        Path results = dir.resolve("build/test-results/test");
        Files.createDirectories(results);
        Files.writeString(results.resolve("one.xml"), """
                <?xml version="1.0" encoding="UTF-8"?>
                <testsuite name="A" tests="5" skipped="1" failures="1" errors="0">
                </testsuite>
                """);
        Files.writeString(results.resolve("two.xml"), """
                <?xml version="1.0" encoding="UTF-8"?>
                <testsuite name="B" tests="2" skipped="0" failures="0" errors="0">
                </testsuite>
                """);

        TestResultsParser.Summary summary = TestResultsParser.parse(dir.toFile());
        assertEquals(5, summary.passed());
        assertEquals(1, summary.failed());
        assertEquals(1, summary.skipped());
    }

    @Test
    void returnsZerosWhenNoResults(@TempDir Path dir) {
        TestResultsParser.Summary s = TestResultsParser.parse(dir.toFile());
        assertEquals(0, s.passed());
        assertEquals(0, s.failed());
        assertEquals(0, s.skipped());
    }
}
