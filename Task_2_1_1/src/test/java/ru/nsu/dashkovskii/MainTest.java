package ru.nsu.dashkovskii;

import org.junit.jupiter.api.Test;

class MainTest {
    @Test
    void testMainBenchmarkDoesNotCrash() {
        Main.runBenchmark(10);
    }
}
