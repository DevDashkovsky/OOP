package ru.nsu.dashkovskii.model;

/**
 * Представляет заказ клиента на пиццу.
 */
public class Order {

    final int id;
    private OrderState state;

    /**
     * Создает новый заказ с указанным идентификатором.
     *
     * @param id уникальный идентификатор заказа
     */
    public Order(int id) {
        this.id = id;
        this.state = OrderState.IN_QUEUE;
        logState();
    }

    public int getId() {
        return id;
    }

    public OrderState getState() {
        return state;
    }

    /**
     * Обновляет состояние заказа и логирует изменение.
     *
     * @param newState новое состояние заказа
     */
    public void setState(OrderState newState) {
        this.state = newState;
        logState();
    }

    /**
     * Логирует текущее состояние заказа в консоль.
     */
    public void logState() {
        System.out.println("[" + id + "] [" + state + "]");
    }
}