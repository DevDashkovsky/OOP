package ru.nsu.dashkovskii;

import java.util.List;

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
