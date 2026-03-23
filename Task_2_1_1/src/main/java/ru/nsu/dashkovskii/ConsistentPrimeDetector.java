package ru.nsu.dashkovskii;

import java.util.List;

/**
 * Реализация PrimesDetector, обрабатывающая список последовательно.
 * Этот класс перебирает список и проверяет каждое число на простоту по одному.
 */
public class ConsistentPrimeDetector implements PrimesDetector {
    @Override
    public boolean containsComposite(List<Integer> numbers) {
        for (int number : numbers) {
            if (!PrimeUtils.isPrime(number)) {
                return true;
            }
        }
        return false;
    }
}
