package ru.nsu.dashkovskii;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Главный класс для запуска бенчмарка различных реализаций PrimesDetector.
 * Может использоваться для сравнения производительности последовательного, многопоточного
 * подходов и использования параллельных потоков (Stream API).
 */
public class Main {
    /**
     * Точка входа в приложение.
     * Запускает бенчмарк с размером списка по умолчанию.
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        runBenchmark(2_000);
    }

    /**
     * Запускает бенчмарк для сравнения различных стратегий поиска простых чисел.
     * Генерирует список больших простых чисел и измеряет время выполнения для каждой стратегии.
     *
     * @param listSize размер списка простых чисел для генерации и теста
     */
    public static void runBenchmark(int listSize) {
        int largePrime = 1000000007;

        System.out.println("Generating test data (" + listSize + " large primes)...");
        List<Integer> numbers = new ArrayList<>(Collections.nCopies(listSize, largePrime));

        new ConsistentPrimeDetector().containsComposite(numbers.subList(0, Math.min(100, numbers.size())));

        System.out.println("Starting Benchmark...\n");
        System.out.println("Strategy | Threads | Time (ms)");
        System.out.println("------------------------------");

        PrimesDetector sequential = new ConsistentPrimeDetector();
        long start = System.nanoTime();
        boolean resSeq = sequential.containsComposite(numbers);
        long durationSeq = (System.nanoTime() - start) / 1_000_000;

        System.out.printf("Sequential      | 1       | %d%n", durationSeq);

        int cores = Runtime.getRuntime().availableProcessors();
        boolean threadError = false;

        for (int i = 1; i <= cores; i++) {
            PrimesDetector threadDetector = new ThreadPrimesDetector(i);
            start = System.nanoTime();
            boolean resThread = threadDetector.containsComposite(numbers);
            if (resThread) {
                threadError = true;
            }
            long durationThread = (System.nanoTime() - start) / 1_000_000;

            System.out.printf("ThreadPol       | %d       | %d%n", i, durationThread);
        }

        PrimesDetector streamDetector = new ParallelStreamPrimesDetector();
        start = System.nanoTime();
        boolean resStream = streamDetector.containsComposite(numbers);
        long durationStream = (System.nanoTime() - start) / 1_000_000;

        System.out.printf("ParallelStream  | ?       | %d%n", durationStream);

        if (resSeq || resStream || threadError) {
            System.err.println("Error: Detectors found a composite number in a list of primes!");
        }
    }
}
