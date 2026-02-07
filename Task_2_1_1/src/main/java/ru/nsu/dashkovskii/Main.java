package ru.nsu.dashkovskii;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        runBenchmark(2_000);
    }

    public static void runBenchmark(int listSize) {
        // Подготовка данных
        // Используем большое простое число для нагрузки CPU
        // 1000000007 is a prime number.
        int largePrime = 1000000007;

        System.out.println("Generating test data (" + listSize + " large primes)...");
        List<Integer> numbers = new ArrayList<>(Collections.nCopies(listSize, largePrime));

        // "Прогрев" JVM
        new ConsistentPrimeDetector().containsComposite(numbers.subList(0, Math.min(100, numbers.size())));

        System.out.println("Starting Benchmark...\n");
        System.out.println("Strategy | Threads | Time (ms)");
        System.out.println("------------------------------");

        // 1. Последовательное исполнение
        PrimesDetector sequential = new ConsistentPrimeDetector();
        long start = System.nanoTime();
        boolean resSeq = sequential.containsComposite(numbers);
        long durationSeq = (System.nanoTime() - start) / 1_000_000;

        System.out.printf("Sequential      | 1       | %d%n", durationSeq);

        // 2. Параллельное исполнение (Thread) с разным количеством потоков
        int cores = Runtime.getRuntime().availableProcessors();

        for (int i = 1; i <= cores; i++) {
            PrimesDetector threadDetector = new ThreadPrimesDetector(i);
            start = System.nanoTime();
            boolean resThread = threadDetector.containsComposite(numbers);
            long durationThread = (System.nanoTime() - start) / 1_000_000;

            System.out.printf("ThreadPol       | %d       | %d%n", i, durationThread);
        }

        // 3. Параллельное исполнение (ParallelStream)
        PrimesDetector streamDetector = new ParallelStreamPrimesDetector();
        start = System.nanoTime();
        boolean resStream = streamDetector.containsComposite(numbers);
        long durationStream = (System.nanoTime() - start) / 1_000_000;

        System.out.printf("ParallelStream  | ?       | %d%n", durationStream);

        if (resSeq || resStream) {
            System.err.println("Error: Detectors found a composite number in a list of primes!");
        }
    }
}
