package ru.nsu.dashkovskii.service;

import java.util.List;
import ru.nsu.dashkovskii.model.Order;
import ru.nsu.dashkovskii.model.OrderState;

/**
 * Представляет курьера, доставляющего пиццы.
 */
public class Courier implements Runnable {
    private final Warehouse warehouse;
    private final int trunkCapacity;
    private final int deliveryTime;

    /**
     * Создает нового курьера.
     *
     * @param warehouse склад, откуда забирать пиццы
     * @param trunkCapacity максимальное количество пицц, которое может везти курьер
     * @param deliveryTime время доставки заказа
     */
    public Courier(Warehouse warehouse, int trunkCapacity, int deliveryTime) {
        this.warehouse = warehouse;
        this.trunkCapacity = trunkCapacity;
        this.deliveryTime = deliveryTime;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                List<Order> orders = warehouse.takePizzas(trunkCapacity);
                orders.forEach(o -> o.setState(OrderState.DELIVERING));
                Thread.sleep(deliveryTime);
                orders.forEach(o -> o.setState(OrderState.DELIVERED));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
