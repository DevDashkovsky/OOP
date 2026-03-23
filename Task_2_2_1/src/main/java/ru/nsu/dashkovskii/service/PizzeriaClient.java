package ru.nsu.dashkovskii.service;

import java.util.concurrent.atomic.AtomicInteger;
import ru.nsu.dashkovskii.model.Order;
import ru.nsu.dashkovskii.util.CustomQueue;

/**
 * Симулирует клиента, генерирующего заказы на пиццу.
 */
public class PizzeriaClient implements Runnable {
    private final CustomQueue<Order> orderQueue;
    private final AtomicInteger orderIdGenerator = new AtomicInteger(0);
    private volatile boolean isRunning = true;

    public PizzeriaClient(CustomQueue<Order> orderQueue) {
        this.orderQueue = orderQueue;
    }

    public void stop() {
        isRunning = false;
    }

    @Override
    public void run() {
        try {
            while (isRunning && !Thread.currentThread().isInterrupted()) {
                Order order = new Order(orderIdGenerator.incrementAndGet());

                orderQueue.enqueue(order, () -> {});

                Thread.sleep((long) (Math.random() * 1000 + 500));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
