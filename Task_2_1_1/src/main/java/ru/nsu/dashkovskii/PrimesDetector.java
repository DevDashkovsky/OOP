package ru.nsu.dashkovskii;

import java.util.List;

public interface PrimesDetector {
    /**
     * Проверяет, содержится ли в списке хотя бы одно непростое число.
     * @param numbers список чисел для проверки
     * @return true, если найдено непростое число, иначе false
     */
    boolean containsComposite(List<Integer> numbers);
}
