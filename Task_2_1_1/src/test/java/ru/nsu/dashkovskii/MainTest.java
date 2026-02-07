package ru.nsu.dashkovskii;

import org.junit.jupiter.api.Test;

class MainTest {
    @Test
    void testMainBenchmarkDoesNotCrash() {
        // Run with a very small size to ensure it completes quickly and doesn't crash
        Main.runBenchmark(10);
    }
}
