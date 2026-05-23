package ru.nsu.dashkovskii.worker;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PrimeCheckerTest {

    private final PrimeChecker checker = new PrimeChecker();

    @Test
    void taskExampleFirst() {
        assertTrue(checker.hasComposite(new int[] {6, 8, 7, 13, 5, 9, 4}));
    }

    @Test
    void taskExampleSecondAllPrime() {
        int[] primes = {
                20319251, 6997901, 6997927, 6997937, 17858849, 6997967,
                6998009, 6998029, 6998039, 20165149, 6998051, 6998053
        };
        assertFalse(checker.hasComposite(primes));
    }

    @Test
    void emptyChunkIsAllPrime() {
        assertFalse(checker.hasComposite(new int[0]));
    }

    @Test
    void singlePrime() {
        assertFalse(checker.hasComposite(new int[] {7}));
    }

    @Test
    void singleComposite() {
        assertTrue(checker.hasComposite(new int[] {9}));
    }

    @Test
    void numbersBelowTwoAreNotPrime() {
        assertTrue(checker.hasComposite(new int[] {0}));
        assertTrue(checker.hasComposite(new int[] {1}));
        assertTrue(checker.hasComposite(new int[] {-5}));
    }

    @Test
    void twoAndThreeArePrime() {
        assertFalse(checker.hasComposite(new int[] {2, 3}));
    }

    @Test
    void firstCompositeShortCircuits() {
        assertTrue(checker.hasComposite(new int[] {4, 7, 11, 13}));
    }
}
