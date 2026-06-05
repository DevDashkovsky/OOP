package ru.nsu.dashkovskii.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.nsu.dashkovskii.master.Master;
import ru.nsu.dashkovskii.worker.Worker;

/**
 * Интеграционный тест: поднимает реальный Master и один/несколько Worker'ов
 * на localhost через TCP-сокеты и прогоняет оба примера из TASK.md.
 *
 * <p>Тесты ограничены таймаутом: если протокол сломан и master/worker
 * зависнут, тест упадёт по таймауту, а не будет ждать вечно.
 */
class DistributedPrimeIntegrationTest {

    private static final int CHUNKS = 4;
    private static final long TIMEOUT_SECONDS = 20;

    @Test
    @Timeout(TIMEOUT_SECONDS)
    void taskExampleFirstReturnsTrue() throws Exception {
        int[] input = {6, 8, 7, 13, 5, 9, 4};
        assertTrue(runWithWorkers(input, 1), "массив содержит составные числа (6, 8, 9, 4)");
    }

    @Test
    @Timeout(TIMEOUT_SECONDS)
    void taskExampleSecondAllPrimesReturnsFalse() throws Exception {
        int[] input = {
                20319251, 6997901, 6997927, 6997937, 17858849, 6997967,
                6998009, 6998029, 6998039, 20165149, 6998051, 6998053
        };
        assertEquals(false, runWithWorkers(input, 2),
                "в массиве только простые числа");
    }

    @Test
    @Timeout(TIMEOUT_SECONDS)
    void singleWorkerHandlesAllChunks() throws Exception {
        int[] input = {2, 3, 5, 7, 11, 13, 17, 19};
        assertEquals(false, runWithWorkers(input, 1));
    }

    @Test
    @Timeout(TIMEOUT_SECONDS)
    void multipleWorkersFindCompositeFast() throws Exception {
        int[] input = {2, 3, 5, 7, 11, 13, 17, 19, 25};
        assertTrue(runWithWorkers(input, 3), "25 — составное");
    }

    private boolean runWithWorkers(int[] input, int workerCount) throws Exception {
        int port = pickFreePort();
        Master master = new Master(port, input, CHUNKS);

        ExecutorService runners = Executors.newCachedThreadPool(runnable -> {
            Thread thread = new Thread(runnable, "test-runner");
            thread.setDaemon(true);
            return thread;
        });

        Future<Boolean> masterFuture = runners.submit(master::run);

        waitForPortBound(port);

        List<Future<?>> workerFutures = new ArrayList<>();
        for (int i = 0; i < workerCount; i++) {
            workerFutures.add(runners.submit(() -> {
                new Worker("localhost", port).run();
                return null;
            }));
        }

        Boolean answer;
        try {
            answer = masterFuture.get(TIMEOUT_SECONDS - 2, TimeUnit.SECONDS);
        } catch (TimeoutException te) {
            runners.shutdownNow();
            throw te;
        }

        for (Future<?> wf : workerFutures) {
            try {
                wf.get(5, TimeUnit.SECONDS);
            } catch (Exception ignored) {
                // worker мог не успеть выйти по STOP — это не интересует тест
            }
        }
        runners.shutdownNow();
        runners.awaitTermination(2, TimeUnit.SECONDS);
        return answer;
    }

    private int pickFreePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    private void waitForPortBound(int port) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 5000;
        while (System.currentTimeMillis() < deadline) {
            try (java.net.Socket probe = new java.net.Socket("localhost", port)) {
                return;
            } catch (IOException ex) {
                Thread.sleep(50);
            }
        }
        throw new IllegalStateException("master не открыл порт " + port + " за 5 секунд");
    }
}
