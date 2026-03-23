package ru.nsu.dashkovskii.strategy;

import java.util.List;
import ru.nsu.dashkovskii.service.PizzeriaClient;

/**
 * Реализация корректного завершения работы пиццерии.
 */
public class GracefulShutdown implements ShutdownStrategy {

    @Override
    public void shutdown(PizzeriaClient client, Thread clientThread, List<Thread> workers) {
        System.out.println("Выполняется мягкое завершение работы...");

        if (client != null) {
            client.stop();
        }
        if (clientThread != null) {
            clientThread.interrupt();
        }

        for (Thread worker : workers) {
            worker.interrupt();
        }

        System.out.println("Все процессы остановлены.");
    }
}
