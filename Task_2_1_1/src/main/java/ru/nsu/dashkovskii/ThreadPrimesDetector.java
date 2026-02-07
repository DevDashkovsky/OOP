package ru.nsu.dashkovskii;

import java.util.List;

public class ThreadPrimesDetector implements PrimesDetector {
    private final int threadCount;

    private volatile boolean foundComposite;

    public ThreadPrimesDetector(int threadCount) {
        this.threadCount = threadCount;
    }

    @Override
    public boolean containsComposite(List<Integer> numbers) {
        if (numbers.isEmpty()) return false;

        foundComposite = false; // Сброс состояния перед запуском
        int size = numbers.size();
        int actualThreads = Math.min(threadCount, size);

        Thread[] threads = new Thread[actualThreads];
        int chunkSize = (size + actualThreads - 1) / actualThreads;

        for (int i = 0; i < actualThreads; i++) {
            final int start = i * chunkSize;
            final int end = Math.min(start + chunkSize, size);

            threads[i] = new Thread(() -> {
                for (int j = start; j < end; j++) {
                    // Если другой поток уже нашел непростое число, выходим
                    if (foundComposite) return;

                    if (!PrimeUtils.isPrime(numbers.get(j))) {
                        foundComposite = true;
                        return;
                    }
                }
            });
            threads[i].start();
        }

        try {
            for (int i = 0; i < actualThreads; i++) {
                threads[i].join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Execution interrupted", e);
        }

        return foundComposite;
    }
}
