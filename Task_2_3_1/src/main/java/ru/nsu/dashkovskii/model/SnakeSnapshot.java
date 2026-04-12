package ru.nsu.dashkovskii.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Неизменяемый снимок состояния змейки для отрисовки.
 */
public record SnakeSnapshot(List<Point> body,
                             Direction direction,
                             boolean alive) {

    /**
     * Создаёт снимок змейки.
     *
     * @param snake змейка для снимка
     * @return снимок
     */
    public static SnakeSnapshot of(Snake snake) {
        return new SnakeSnapshot(
                new ArrayList<>(snake.getBody()),
                snake.getDirection(),
                snake.isAlive());
    }

}
