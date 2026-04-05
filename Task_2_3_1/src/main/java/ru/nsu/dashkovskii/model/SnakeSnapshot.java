package ru.nsu.dashkovskii.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Неизменяемый снимок состояния змейки для отрисовки.
 */
public class SnakeSnapshot {
    private final List<Point> body;
    private final Direction direction;
    private final boolean alive;

    /**
     * Создаёт снимок змейки.
     *
     * @param snake змейка для снимка
     */
    public SnakeSnapshot(Snake snake) {
        this.body = new ArrayList<>(snake.getBody());
        this.direction = snake.getDirection();
        this.alive = snake.isAlive();
    }

    public List<Point> getBody() {
        return body;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isAlive() {
        return alive;
    }

    public int length() {
        return body.size();
    }

    /**
     * Возвращает позицию головы змейки.
     *
     * @return точка-голова
     */
    public Point getHead() {
        return body.get(0);
    }
}
