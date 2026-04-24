package ru.nsu.dashkovskii.checker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Парсер JUnit XML-отчётов gradle.
 */
public final class TestResultsParser {

    /** Триплет число-пройденных/число-упавших/число-пропущенных. */
    public record Summary(int passed, int failed, int skipped) {
        public Summary add(Summary other) {
            return new Summary(passed + other.passed, failed + other.failed, skipped + other.skipped);
        }
    }

    private TestResultsParser() {
    }

    /**
     * Собирает результаты в {@code projectDir/build/test-results/test/*.xml}.
     */
    public static Summary parse(File projectDir) {
        File resultsDir = new File(projectDir, "build/test-results/test");
        if (!resultsDir.isDirectory()) {
            return new Summary(0, 0, 0);
        }
        File[] files = resultsDir.listFiles((d, n) -> n.endsWith(".xml"));
        if (files == null) {
            return new Summary(0, 0, 0);
        }
        Summary total = new Summary(0, 0, 0);
        for (File f : files) {
            total = total.add(parseOne(f));
        }
        return total;
    }

    private static Summary parseOne(File file) {
        try (InputStream in = new FileInputStream(file)) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(
                    "http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder db = factory.newDocumentBuilder();
            Document doc = db.parse(in);
            Element root = doc.getDocumentElement();
            int tests = readInt(root, "tests");
            int failures = readInt(root, "failures");
            int errors = readInt(root, "errors");
            int skipped = readInt(root, "skipped");
            // gradle JUnit5 XML: атрибут "skipped" есть всегда, иначе считаем по элементам
            if (skipped == 0) {
                NodeList skippedNodes = root.getElementsByTagName("skipped");
                skipped = skippedNodes.getLength();
            }
            int failed = failures + errors;
            int passed = Math.max(0, tests - failed - skipped);
            return new Summary(passed, failed, skipped);
        } catch (IOException | SAXException | ParserConfigurationException e) {
            return new Summary(0, 0, 0);
        }
    }

    private static int readInt(Element el, String attr) {
        String v = el.getAttribute(attr);
        if (v.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
