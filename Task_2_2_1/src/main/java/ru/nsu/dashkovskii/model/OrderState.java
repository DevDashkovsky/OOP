package ru.nsu.dashkovskii.model;

/**
 * Представляет состояние заказа в системе пиццерии.
 */
public enum OrderState {
    /**
     * Заказ в очереди.
     */
    IN_QUEUE,
    /**
     * Заказ готовится.
     */
    COOKING,
    /**
     * Заказ готов и на складе.
     */
    IN_WAREHOUSE,
    /**
     * Заказ доставляется.
     */
    DELIVERING,
    /**
     * Заказ доставлен.
     */
    DELIVERED
}
