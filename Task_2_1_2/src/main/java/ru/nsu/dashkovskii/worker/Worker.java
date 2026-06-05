package ru.nsu.dashkovskii.worker;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import ru.nsu.dashkovskii.common.HeartbeatMessage;
import ru.nsu.dashkovskii.common.Message;
import ru.nsu.dashkovskii.common.MessageChannel;
import ru.nsu.dashkovskii.common.ResultMessage;
import ru.nsu.dashkovskii.common.StopMessage;
import ru.nsu.dashkovskii.common.TaskMessage;

/**
 * Воркер: подключается к master'у, получает {@link TaskMessage} с чанком
 * чисел, проверяет их через {@link PrimeChecker} и отвечает
 * {@link ResultMessage}. В фоне шлёт {@link HeartbeatMessage}.
 *
 * <p>Запуск:
 * <pre>./gradlew runWorker -PappArgs="host port"</pre>
 */
public final class Worker {

    /** Период между heartbeat'ами, мс. */
    public static final long HEARTBEAT_INTERVAL_MS = 1000L;

    private final String host;
    private final int port;
    private final String workerId;
    private final PrimeChecker checker = new PrimeChecker();

    private MessageChannel channel;
    private ExecutorService computationExec;
    private Future<?> currentTask;
    private Thread heartbeatThread;

    /**
     * Создаёт worker для подключения к указанному master'у.
     *
     * @param host адрес master'а
     * @param port TCP-порт master'а
     */
    public Worker(String host, int port) {
        this.host = host;
        this.port = port;
        this.workerId = generateWorkerId();
    }

    /**
     * Точка входа в worker.
     *
     * @param args [host, port]
     * @throws Exception при ошибке подключения
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("usage: Worker <host> <port>");
            System.exit(1);
        }
        new Worker(args[0], Integer.parseInt(args[1])).run();
    }

    /**
     * Запускает worker. Возвращается, когда master шлёт {@link StopMessage}
     * или когда соединение оборвалось.
     *
     * @throws IOException при ошибке подключения к master'у
     */
    public void run() throws IOException {
        System.out.println("[worker " + workerId + "] connecting to " + host + ":" + port);
        Socket socket = new Socket(host, port);
        this.channel = new MessageChannel(socket);

        channel.send(new HeartbeatMessage(workerId));

        this.computationExec = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable, "worker-compute-" + workerId);
            thread.setDaemon(true);
            return thread;
        });

        this.heartbeatThread = new Thread(
                new HeartbeatSender(channel, workerId, HEARTBEAT_INTERVAL_MS),
                "worker-heartbeat-" + workerId);
        this.heartbeatThread.setDaemon(true);
        this.heartbeatThread.start();

        receiveLoop();
        shutdown();
    }

    private void receiveLoop() {
        while (true) {
            Message msg;
            try {
                msg = channel.receive();
            } catch (Exception ex) {
                System.out.println("[worker " + workerId + "] channel closed: " + ex);
                return;
            }
            if (msg instanceof TaskMessage task) {
                submitTask(task);
            } else if (msg instanceof StopMessage) {
                System.out.println("[worker " + workerId + "] STOP received");
                return;
            } else {
                System.out.println("[worker " + workerId + "] unexpected message: " + msg);
            }
        }
    }

    private void submitTask(TaskMessage task) {
        this.currentTask = computationExec.submit(() -> processTask(task));
    }

    private void processTask(TaskMessage task) {
        try {
            boolean hasComposite = checker.hasComposite(task.chunk());
            channel.send(new ResultMessage(task.taskId(), hasComposite));
        } catch (PrimeChecker.InterruptedRuntimeException ire) {
            // Сигнал остановки — задачу не подтверждаем, master её переназначит.
            System.out.println("[worker " + workerId + "] task " + task.taskId() + " interrupted");
        } catch (IOException ioe) {
            System.out.println("[worker " + workerId + "] failed to send result: " + ioe);
        }
    }

    private void shutdown() {
        if (currentTask != null) {
            currentTask.cancel(true);
        }
        if (computationExec != null) {
            computationExec.shutdownNow();
        }
        if (heartbeatThread != null) {
            heartbeatThread.interrupt();
        }
        if (channel != null) {
            channel.close();
        }
    }

    private String generateWorkerId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
