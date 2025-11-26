package ru.nsu.dashkovskii;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Тесты для SubstringFinder.
 */
public class SubstringFinderTest {

    @TempDir
    File tempDir;

    private File createTestFile(String content) throws IOException {
        File file = new File(tempDir, "test.txt");
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write(content);
        }
        return file;
    }

    @Test
    void testSimpleSearchCyrillic() throws IOException {
        File file = createTestFile("абракадабра");
        SubstringFinder finder = new SubstringFinder();
        List<Long> result = finder.find(file.getAbsolutePath(), "бра");

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0));
        assertEquals(8L, result.get(1));
    }

    @Test
    void testNoMatches() throws IOException {
        File file = createTestFile("абракадабра");
        SubstringFinder finder = new SubstringFinder();
        List<Long> result = finder.find(file.getAbsolutePath(), "xyz");

        assertTrue(result.isEmpty());
    }

    @Test
    void testSingleMatch() throws IOException {
        File file = createTestFile("hello world");
        SubstringFinder finder = new SubstringFinder();
        List<Long> result = finder.find(file.getAbsolutePath(), "world");

        assertEquals(1, result.size());
        assertEquals(6L, result.get(0));
    }

    @Test
    void testOverlappingMatches() throws IOException {
        File file = createTestFile("aaaa");
        SubstringFinder finder = new SubstringFinder();
        List<Long> result = finder.find(file.getAbsolutePath(), "aa");

        assertEquals(3, result.size());
        assertEquals(0L, result.get(0));
        assertEquals(1L, result.get(1));
        assertEquals(2L, result.get(2));
    }

    @Test
    void testUtf8Characters() throws IOException {
        File file = createTestFile("Привет мир Привет");
        SubstringFinder finder = new SubstringFinder();
        List<Long> result = finder.find(file.getAbsolutePath(), "Привет");

        assertEquals(2, result.size());
        assertEquals(0L, result.get(0));
        assertEquals(11L, result.get(1));
    }

    @Test
    void testEmptyPattern() throws IOException {
        File file = createTestFile("test");
        SubstringFinder finder = new SubstringFinder();

        assertThrows(IllegalArgumentException.class, () -> {
            finder.find(file.getAbsolutePath(), "");
        });
    }

    @Test
    void testNullPattern() throws IOException {
        File file = createTestFile("test");
        SubstringFinder finder = new SubstringFinder();

        assertThrows(IllegalArgumentException.class, () -> {
            finder.find(file.getAbsolutePath(), null);
        });
    }

    @Test
    void testLargeFile() throws IOException {
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            content.append("test ");
        }
        content.append("найди");
        for (int i = 0; i < 10000; i++) {
            content.append(" test");
        }

        File file = createTestFile(content.toString());
        SubstringFinder finder = new SubstringFinder();
        List<Long> result = finder.find(file.getAbsolutePath(), "найди");

        assertEquals(1, result.size());
    }

    @Test
    void testPatternAtBufferBoundary() throws IOException {
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 8000; i++) {
            content.append("a");
        }
        content.append("pattern");
        for (int i = 0; i < 8000; i++) {
            content.append("b");
        }

        File file = createTestFile(content.toString());
        SubstringFinder finder = new SubstringFinder();
        List<Long> result = finder.find(file.getAbsolutePath(), "pattern");

        assertEquals(1, result.size());
        assertEquals(8000L, result.get(0));
    }

    @Test
    void testMultipleOccurrences() throws IOException {
        File file = createTestFile("abc abc abc");
        SubstringFinder finder = new SubstringFinder();
        List<Long> result = finder.find(file.getAbsolutePath(), "abc");

        assertEquals(3, result.size());
        assertEquals(0L, result.get(0));
        assertEquals(4L, result.get(1));
        assertEquals(8L, result.get(2));
    }

    @Test
    void testPatternAtStart() throws IOException {
        File file = createTestFile("pattern in the beginning");
        SubstringFinder finder = new SubstringFinder();
        List<Long> result = finder.find(file.getAbsolutePath(), "pattern");

        assertEquals(1, result.size());
        assertEquals(0L, result.get(0));
    }

    @Test
    void testPatternAtEnd() throws IOException {
        File file = createTestFile("at the end pattern");
        SubstringFinder finder = new SubstringFinder();
        List<Long> result = finder.find(file.getAbsolutePath(), "pattern");

        assertEquals(1, result.size());
        assertEquals(11L, result.get(0));
    }

    @Test
    void testVeryLargeFile() throws IOException {
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 100000; i++) {
            content.append("abcdefgh ");
        }
        content.append("ИСКОМОЕ");
        for (int i = 0; i < 100000; i++) {
            content.append(" 12345678");
        }

        File file = createTestFile(content.toString());
        SubstringFinder finder = new SubstringFinder();
        List<Long> result = finder.find(file.getAbsolutePath(), "ИСКОМОЕ");

        assertEquals(1, result.size());
    }
}

