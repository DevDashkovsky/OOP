package ru.nsu.dashkovskii;

import java.util.List;

public class ParallelStreamPrimesDetector implements PrimesDetector{
    @Override
    public boolean containsComposite(List<Integer> numbers) {
        return numbers.parallelStream()
                .anyMatch(number -> !PrimeUtils.isPrime(number));
    }
}
