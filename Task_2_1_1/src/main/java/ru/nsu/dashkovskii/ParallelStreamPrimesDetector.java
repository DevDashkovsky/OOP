package ru.nsu.dashkovskii;

import java.util.List;

/**
 * Реализация PrimesDetector, использующая Parallel Stream API Java.
 * Эта реализация делегирует распараллеливание и распределение нагрузки Stream API.
 */
public class ParallelStreamPrimesDetector implements PrimesDetector{
    @Override
    public boolean containsComposite(List<Integer> numbers) {
        return numbers.parallelStream()
                .anyMatch(number -> !PrimeUtils.isPrime(number));
    }
}
