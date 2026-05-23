package ru.nsu.dashkovskii.master;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import ru.nsu.dashkovskii.common.StopMessage;

/**
 * Master-узел распределённой системы.
 *
 * <p>Запуск:
 * <pre>./gradlew runMaster -PappArgs="--port=5555 --array=6,8,7 --chunks=4"</pre>
 *
 * <p>Аргументы:
 * <ul>
 *   <li>{@code --port=N} — TCP-порт для приёма worker'ов (по умолчанию 5555)
 *   <li>{@code --array=a,b,c,...} — входной массив целых чисел
 *   <li>{@code --chunks=K} — желаемое число чанков (по умолчанию 32)
 * </ul>
 *
 * <p>Печатает на stdout одну строку: {@code true} если в массиве есть
 * составное число, иначе {@code false}.
 */
public final class Master {

    /** Порт по умолчанию. */
    public static final int DEFAULT_PORT = 5555;
    /** Желаемое число чанков по умолчанию. */
    public static final int DEFAULT_CHUNKS = 32;
    /** Период тика watchdog'а, мс. */
    public static final long WATCHDOG_TICK_MS = 1000L;
    /** Допустимая пауза между heartbeat'ами, мс. */
    public static final long HEARTBEAT_TIMEOUT_MS = 5000L;
    /** Дедлайн на выполнение одной задачи, мс. */
    public static final long TASK_DEADLINE_MS = 30_000L;
    /** Таймаут ожидания PENDING-задачи в Dispatcher'е, мс. */
    public static final long DISPATCHER_TAKE_MS = 200L;
    /** Пауза Dispatcher'а при отсутствии свободных worker'ов, мс. */
    public static final long DISPATCHER_BACKOFF_MS = 100L;

    private final int port;
    private final int[] array;
    private final int chunkCount;

    private final TaskRegistry registry = new TaskRegistry();
    private final ConcurrentMap<String, WorkerProxy> workers = new ConcurrentHashMap<>();

    /**
     * Создаёт master с указанными параметрами.
     *
     * @param port порт для приёма worker'ов
     * @param array входной массив
     * @param chunkCount желаемое число чанков
     */
    public Master(int port, int[] array, int chunkCount) {
        this.port = port;
        this.array = array.clone();
        this.chunkCount = chunkCount;
    }

    /**
     * Точка входа.
     *
     * @param args аргументы командной строки
     * @throws Exception при ошибке запуска
     */
    public static void main(String[] args) throws Exception {
        int parsedPort = DEFAULT_PORT;
        int parsedChunks = DEFAULT_CHUNKS;
        int[] parsedArray = new int[0];
        for (String arg : args) {
            if (arg.startsWith("--port=")) {
                parsedPort = Integer.parseInt(arg.substring("--port=".length()));
            } else if (arg.startsWith("--chunks=")) {
                parsedChunks = Integer.parseInt(arg.substring("--chunks=".length()));
            } else if (arg.startsWith("--array=")) {
                parsedArray = new CsvIntArray(arg.substring("--array=".length())).parse();
            }
        }
        if (parsedArray.length == 0) {
            System.err.println("usage: Master --port=N --array=a,b,c,... [--chunks=K]");
            System.exit(1);
        }
        boolean answer = new Master(parsedPort, parsedArray, parsedChunks).run();
        System.out.println(answer);
    }

    /**
     * Запускает master до получения ответа или обработки всех чанков.
     *
     * @return true, если в массиве найдено хотя бы одно непростое число
     * @throws IOException при ошибке открытия серверного сокета
     * @throws InterruptedException при прерывании ожидания завершения
     */
    public boolean run() throws IOException, InterruptedException {
        prepareChunks();

        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("[master] listening on port " + port + ", chunks=" + chunkCount);

        ExecutorService pool = Executors.newCachedThreadPool(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        });

        pool.submit(new WorkerAcceptor(serverSocket, workers, registry));
        pool.submit(new Dispatcher(
                registry, workers, TASK_DEADLINE_MS, DISPATCHER_TAKE_MS, DISPATCHER_BACKOFF_MS));
        pool.submit(new Watchdog(registry, workers, WATCHDOG_TICK_MS, HEARTBEAT_TIMEOUT_MS));

        waitForFinish();

        boolean answer = registry.answerFound();

        broadcastStop();
        closeQuietly(serverSocket);
        pool.shutdownNow();
        pool.awaitTermination(2, TimeUnit.SECONDS);
        closeAllWorkers();
        return answer;
    }

    private void prepareChunks() {
        ChunkSplitter splitter = new ChunkSplitter();
        List<int[]> chunks = splitter.split(array, chunkCount);
        for (int[] chunk : chunks) {
            registry.enqueue(chunk);
        }
        System.out.println("[master] enqueued " + chunks.size() + " chunks");
    }

    private void waitForFinish() throws InterruptedException {
        while (!registry.isFinished()) {
            Thread.sleep(50);
        }
    }

    private void broadcastStop() {
        StopMessage stop = new StopMessage();
        List<WorkerProxy> snapshot = new ArrayList<>(workers.values());
        for (WorkerProxy proxy : snapshot) {
            if (!proxy.isAlive()) {
                continue;
            }
            try {
                proxy.send(stop);
            } catch (IOException ex) {
                System.out.println(
                        "[master] failed to send STOP to " + proxy.workerId() + ": " + ex);
            }
        }
    }

    private void closeAllWorkers() {
        for (WorkerProxy proxy : workers.values()) {
            proxy.markDead();
        }
    }

    private void closeQuietly(ServerSocket socket) {
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }
}
