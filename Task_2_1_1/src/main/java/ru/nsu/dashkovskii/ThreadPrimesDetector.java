package ru.nsu.dashkovskii;

import java.util.List;

/**
 * Реализация PrimesDetector, использующая несколько потоков для обработки списка.
 * Список делится на части, и каждая часть обрабатывается отдельным потоком.
 */
public class ThreadPrimesDetector implements PrimesDetector {
    private final int threadCount;

    private volatile boolean foundComposite;

    /**
     * Создает экземпляр ThreadPrimesDetector с указанным количеством потоков.
     *
     * @param threadCount количество потоков для обработки
     */
    public ThreadPrimesDetector(int threadCount) {
        this.threadCount = threadCount;
    }

    @Override
    public boolean containsComposite(List<Integer> numbers) {
        if (numbers.isEmpty()) return false;

        foundComposite = false;
        int size = numbers.size();
        int actualThreads = Math.min(threadCount, size);

        Thread[] threads = new Thread[actualThreads];
        int chunkSize = (size + actualThreads - 1) / actualThreads;

        for (int i = 0; i < actualThreads; i++) {
            final int start = i * chunkSize;
            final int end = Math.min(start + chunkSize, size);

            threads[i] = new Thread(() -> {
                for (int j = start; j < end; j++) {
                    if (foundComposite) {
                        return;
                    }

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
