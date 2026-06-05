package ru.nsu.dashkovskii.master;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CsvIntArrayTest {

    @Test
    void emptyStringIsEmptyArray() {
        assertArrayEquals(new int[0], new CsvIntArray("").parse());
    }

    @Test
    void parsesPositive() {
        assertArrayEquals(new int[] {1, 2, 3}, new CsvIntArray("1,2,3").parse());
    }

    @Test
    void trimsWhitespace() {
        assertArrayEquals(new int[] {6, 8, 7}, new CsvIntArray("6, 8 , 7").parse());
    }

    @Test
    void parsesNegativeAndZero() {
        assertArrayEquals(new int[] {-5, 0, 7}, new CsvIntArray("-5,0,7").parse());
    }

    @Test
    void singleValue() {
        assertArrayEquals(new int[] {42}, new CsvIntArray("42").parse());
    }

    @Test
    void garbageThrows() {
        assertThrows(NumberFormatException.class, () -> new CsvIntArray("1,abc,3").parse());
    }
}
