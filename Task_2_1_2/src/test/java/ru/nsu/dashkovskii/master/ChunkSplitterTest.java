package ru.nsu.dashkovskii.master;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class ChunkSplitterTest {

    private final ChunkSplitter splitter = new ChunkSplitter();

    @Test
    void splitsEvenly() {
        List<int[]> chunks = splitter.split(new int[] {1, 2, 3, 4}, 2);
        assertEquals(2, chunks.size());
        assertArrayEquals(new int[] {1, 2}, chunks.get(0));
        assertArrayEquals(new int[] {3, 4}, chunks.get(1));
    }

    @Test
    void remainderGoesToHeadChunks() {
        List<int[]> chunks = splitter.split(new int[] {1, 2, 3, 4, 5}, 3);
        assertEquals(3, chunks.size());
        assertArrayEquals(new int[] {1, 2}, chunks.get(0));
        assertArrayEquals(new int[] {3, 4}, chunks.get(1));
        assertArrayEquals(new int[] {5}, chunks.get(2));
    }

    @Test
    void preservesAllElements() {
        int[] src = {6, 8, 7, 13, 5, 9, 4};
        List<int[]> chunks = splitter.split(src, 4);
        int sum = 0;
        int count = 0;
        for (int[] c : chunks) {
            for (int v : c) {
                sum += v;
                count++;
            }
        }
        assertEquals(src.length, count);
        assertEquals(6 + 8 + 7 + 13 + 5 + 9 + 4, sum);
    }

    @Test
    void moreChunksThanElementsCaps() {
        List<int[]> chunks = splitter.split(new int[] {1, 2, 3}, 10);
        assertEquals(3, chunks.size());
        for (int[] c : chunks) {
            assertEquals(1, c.length);
        }
    }

    @Test
    void emptyArrayProducesEmptyList() {
        assertTrue(splitter.split(new int[0], 4).isEmpty());
    }
}
