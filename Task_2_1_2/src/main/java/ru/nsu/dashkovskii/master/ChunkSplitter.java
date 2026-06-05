package ru.nsu.dashkovskii.master;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Разбивает входной массив на чанки максимально равной длины.
 *
 * <p>Если {@code array.length} не делится на {@code chunkCount} нацело,
 * первые {@code array.length % chunkCount} чанков получают на один элемент
 * больше. Если запрошено больше чанков, чем элементов, возвращается
 * по одному элементу на чанк.
 */
public final class ChunkSplitter {

    /**
     * Делит массив на чанки.
     *
     * @param array исходный массив
     * @param chunkCount желаемое число чанков (хинт)
     * @return список чанков; пустой список, если входной массив пуст
     */
    public List<int[]> split(int[] array, int chunkCount) {
        if (array.length == 0) {
            return Collections.emptyList();
        }
        int actualChunks = Math.min(chunkCount, array.length);
        int baseSize = array.length / actualChunks;
        int remainder = array.length % actualChunks;

        List<int[]> result = new ArrayList<>(actualChunks);
        int offset = 0;
        for (int idx = 0; idx < actualChunks; idx++) {
            int size = baseSize + (idx < remainder ? 1 : 0);
            result.add(Arrays.copyOfRange(array, offset, offset + size));
            offset += size;
        }
        return result;
    }
}
