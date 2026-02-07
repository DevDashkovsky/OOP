package ru.nsu.dashkovskii;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class ThreadPrimesDetectorTest {

    @Test
    void testEmptyList() {
        PrimesDetector detector = new ThreadPrimesDetector(4);
        assertFalse(detector.containsComposite(Collections.emptyList()));
    }

    @Test
    void testOnlyPrimes() {
        PrimesDetector detector = new ThreadPrimesDetector(4);
        List<Integer> primes = Arrays.asList(2, 3, 5, 7, 11, 13, 1000000007);
        assertFalse(detector.containsComposite(primes));
    }

    @Test
    void testHasComposite() {
        PrimesDetector detector = new ThreadPrimesDetector(4);
        List<Integer> numbers = Arrays.asList(2, 3, 4, 5);
        assertTrue(detector.containsComposite(numbers));
    }

    @Test
    void testHasUnit() {
        PrimesDetector detector = new ThreadPrimesDetector(4);
        List<Integer> numbers = Arrays.asList(2, 3, 5, 1);
        assertTrue(detector.containsComposite(numbers));
    }

    @Test
    void testCompositeAtStart() {
        PrimesDetector detector = new ThreadPrimesDetector(4);
        List<Integer> numbers = Arrays.asList(4, 5, 7);
        assertTrue(detector.containsComposite(numbers));
    }

    @Test
    void testCompositeAtEnd() {
        PrimesDetector detector = new ThreadPrimesDetector(4);
        List<Integer> numbers = Arrays.asList(5, 7, 4);
        assertTrue(detector.containsComposite(numbers));
    }

    @Test
    void testSingleThread() {
        PrimesDetector detector = new ThreadPrimesDetector(1);
        List<Integer> numbers = Arrays.asList(2, 3, 5, 4);
        assertTrue(detector.containsComposite(numbers));
        assertFalse(detector.containsComposite(Arrays.asList(2, 3, 5)));
    }

    @Test
    void testManyThreads() {
        PrimesDetector detector = new ThreadPrimesDetector(100);
        List<Integer> numbers = Arrays.asList(2, 3, 5, 4);
        assertTrue(detector.containsComposite(numbers));
        assertFalse(detector.containsComposite(Arrays.asList(2, 3, 5)));
    }
}
