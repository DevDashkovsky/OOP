package ru.nsu.dashkovskii.strategy;

import java.util.List;
import ru.nsu.dashkovskii.service.PizzeriaClient;

/**
 * Интерфейс стратегии завершения работы пиццерии.
 */
public interface ShutdownStrategy {
    /**
     * Выполняет процедуру завершения работы пиццерии.
     *
     * @param client генератор заказов
     * @param clientThread поток генератора заказов
     * @param workers список потоков работников (пекарей и курьеров)
     */
    void shutdown(PizzeriaClient client, Thread clientThread, List<Thread> workers);
}
