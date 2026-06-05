package ru.nsu.dashkovskii.master;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentMap;
import ru.nsu.dashkovskii.common.HeartbeatMessage;
import ru.nsu.dashkovskii.common.Message;
import ru.nsu.dashkovskii.common.MessageChannel;

/**
 * Поток, принимающий входящие подключения worker'ов.
 *
 * <p>На каждое подключение: создаёт {@link MessageChannel}, ждёт первый
 * {@link HeartbeatMessage} (он содержит workerId), регистрирует
 * {@link WorkerProxy} и запускает {@link ResultCollector}-поток для чтения
 * последующих сообщений.
 */
public final class WorkerAcceptor implements Runnable {

    private final ServerSocket serverSocket;
    private final ConcurrentMap<String, WorkerProxy> workers;
    private final TaskRegistry registry;

    /**
     * Создаёт acceptor поверх готового {@link ServerSocket}.
     *
     * @param serverSocket уже забинденный серверный сокет
     * @param workers разделяемая карта workerId → WorkerProxy
     * @param registry реестр задач (для пробрасывания в ResultCollector)
     */
    public WorkerAcceptor(
            ServerSocket serverSocket,
            ConcurrentMap<String, WorkerProxy> workers,
            TaskRegistry registry) {
        this.serverSocket = serverSocket;
        this.workers = workers;
        this.registry = registry;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted() && !serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                handleNewConnection(socket);
            } catch (IOException ex) {
                if (!serverSocket.isClosed()) {
                    System.out.println("[acceptor] accept failed: " + ex);
                }
                return;
            }
        }
    }

    private void handleNewConnection(Socket socket) {
        MessageChannel channel;
        try {
            channel = new MessageChannel(socket);
        } catch (IOException ex) {
            System.out.println("[acceptor] failed to wrap socket: " + ex);
            return;
        }

        String workerId;
        try {
            Message first = channel.receive();
            if (!(first instanceof HeartbeatMessage hb)) {
                System.out.println("[acceptor] first message is not heartbeat: " + first);
                channel.close();
                return;
            }
            workerId = hb.workerId();
        } catch (Exception ex) {
            System.out.println("[acceptor] failed to read identify: " + ex);
            channel.close();
            return;
        }

        WorkerProxy proxy = new WorkerProxy(workerId, channel);
        workers.put(workerId, proxy);
        System.out.println(
                "[acceptor] worker " + workerId + " joined from " + channel.remoteAddress());

        Thread receiver = new Thread(
                new ResultCollector(proxy, registry),
                "master-receiver-" + workerId);
        receiver.setDaemon(true);
        receiver.start();
    }
}
