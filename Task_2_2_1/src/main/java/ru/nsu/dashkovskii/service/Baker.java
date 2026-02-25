package ru.nsu.dashkovskii.service;

import ru.nsu.dashkovskii.model.Order;
import ru.nsu.dashkovskii.model.OrderState;
import ru.nsu.dashkovskii.util.CustomQueue;

/**
 * Представляет пекаря, который готовит пиццы.
 */
public class Baker implements Runnable {
    private final int id;
    private final int bakingTime;
    private final CustomQueue<Order> orderQueue;
    private final Warehouse warehouse;

    /**
     * Создает нового пекаря.
     *
     * @param id уникальный идентификатор пекаря
     * @param bakingTime время приготовления одной пиццы
     * @param orderQueue очередь заказов, откуда брать заказы
     * @param warehouse склад, куда класть готовые пиццы
     */
    public Baker(int id, int bakingTime, CustomQueue<Order> orderQueue, Warehouse warehouse) {
        this.id = id;
        this.bakingTime = bakingTime;
        this.orderQueue = orderQueue;
        this.warehouse = warehouse;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Order order = orderQueue.dequeue();
                order.setState(OrderState.COOKING);

                Thread.sleep(bakingTime);

                warehouse.addPizza(order);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
