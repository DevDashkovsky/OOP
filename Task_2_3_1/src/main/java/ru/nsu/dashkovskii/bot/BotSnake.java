package ru.nsu.dashkovskii.bot;

import java.util.List;
import ru.nsu.dashkovskii.model.Direction;
import ru.nsu.dashkovskii.model.GameField;
import ru.nsu.dashkovskii.model.Point;
import ru.nsu.dashkovskii.model.Snake;

/**
 * Змейка-бот с подключаемой стратегией ИИ.
 */
public class BotSnake {
    private final Snake snake;
    private final BotStrategy strategy;

    public BotSnake(Point start, Direction direction, BotStrategy strategy) {
        this.snake = new Snake(start, direction);
        this.strategy = strategy;
    }

    public Snake getSnake() {
        return snake;
    }

    public BotStrategy getStrategy() {
        return strategy;
    }

    /**
     * Обновляет направление движения бота согласно его стратегии.
     *
     * @param allSnakes все змейки на поле
     * @param field игровое поле
     */
    public void updateDirection(List<Snake> allSnakes, GameField field) {
        if (!snake.isAlive()) {
            return;
        }
        Direction chosen = strategy.chooseDirection(snake, allSnakes, field);
        snake.setDirection(chosen);
    }
}
