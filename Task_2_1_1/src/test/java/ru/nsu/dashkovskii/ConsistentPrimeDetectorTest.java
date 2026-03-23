package ru.nsu.dashkovskii;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;


class ConsistentPrimeDetectorTest {

    private final PrimesDetector detector = new ConsistentPrimeDetector();

    @Test
    void testEmptyList() {
        assertFalse(detector.containsComposite(Collections.emptyList()));
    }

    @Test
    void testOnlyPrimes() {
        List<Integer> primes = Arrays.asList(2, 3, 5, 7, 11, 13, 1000000007);
        assertFalse(detector.containsComposite(primes));
    }

    @Test
    void testHasComposite() {
        List<Integer> numbers = Arrays.asList(2, 3, 4, 5);
        assertTrue(detector.containsComposite(numbers));
    }

    @Test
    void testHasUnit() {
        List<Integer> numbers = Arrays.asList(2, 3, 5, 1);
        assertTrue(detector.containsComposite(numbers));
    }

    @Test
    void testCompositeAtStart() {
        List<Integer> numbers = Arrays.asList(4, 5, 7);
        assertTrue(detector.containsComposite(numbers));
    }

    @Test
    void testCompositeAtEnd() {
        List<Integer> numbers = Arrays.asList(5, 7, 4);
        assertTrue(detector.containsComposite(numbers));
    }
}
