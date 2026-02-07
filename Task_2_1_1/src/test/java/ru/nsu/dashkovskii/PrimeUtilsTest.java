package ru.nsu.dashkovskii;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;


class PrimeUtilsTest {

    @Test
    void testIsPrime() {
        assertTrue(PrimeUtils.isPrime(2));
        assertTrue(PrimeUtils.isPrime(3));
        assertTrue(PrimeUtils.isPrime(5));
        assertTrue(PrimeUtils.isPrime(7));
        assertTrue(PrimeUtils.isPrime(11));
        assertTrue(PrimeUtils.isPrime(13));
        assertTrue(PrimeUtils.isPrime(1000000007));

        assertFalse(PrimeUtils.isPrime(1));
        assertFalse(PrimeUtils.isPrime(0));
        assertFalse(PrimeUtils.isPrime(-1));
        assertFalse(PrimeUtils.isPrime(4));
        assertFalse(PrimeUtils.isPrime(6));
        assertFalse(PrimeUtils.isPrime(8));
        assertFalse(PrimeUtils.isPrime(9));
        assertFalse(PrimeUtils.isPrime(10));
    }
}
