package ru.nsu.dashkovskii.service;

import java.util.List;
import ru.nsu.dashkovskii.model.Order;
import ru.nsu.dashkovskii.model.OrderState;
import ru.nsu.dashkovskii.util.CustomQueue;

/**
 * Представляет склад для хранения готовых пицц.
 */
public class Warehouse {
    private final CustomQueue<Order> storage;

    /**
     * Создает новый экземпляр склада.
     *
     * @param capacity вместимость склада
     */
    public Warehouse(int capacity) {
        this.storage = new CustomQueue<>(capacity);
    }

    /**
     * Добавляет пиццу на склад.
     *
     * @param order заказ (пицца) для добавления
     * @throws InterruptedException если ожидание прервано
     */
    public void addPizza(Order order) throws InterruptedException {
        storage.enqueue(order, () -> order.setState(OrderState.IN_WAREHOUSE));
    }

    /**
     * Забирает партию пицц со склада.
     *
     * @param capacity максимальное количество пицц, которые можно забрать
     * @return список заказов
     * @throws InterruptedException если ожидание прервано
     */
    public List<Order> takePizzas(int capacity) throws InterruptedException {
        return storage.dequeueBatch(capacity);
    }
}
