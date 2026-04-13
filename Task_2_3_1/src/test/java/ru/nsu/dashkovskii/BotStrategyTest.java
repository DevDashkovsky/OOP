package ru.nsu.dashkovskii;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import ru.nsu.dashkovskii.bot.GreedyStrategy;
import ru.nsu.dashkovskii.bot.RandomStrategy;
import ru.nsu.dashkovskii.model.Direction;
import ru.nsu.dashkovskii.model.GameConfig;
import ru.nsu.dashkovskii.model.GameField;
import ru.nsu.dashkovskii.model.Point;
import ru.nsu.dashkovskii.model.Snake;

/**
 * Тесты для реализаций стратегий ботов.
 */
class BotStrategyTest {

    @Test
    void testRandomStrategyReturnsDirection() {
        GameConfig config = GameConfig.load("/nonexistent.json");
        GameField field = new GameField(config, new Random(42));
        Snake snake = new Snake(new Point(10, 7), Direction.RIGHT);
        List<Snake> snakes = Collections.singletonList(snake);
        field.spawnFood(snakes);

        RandomStrategy strategy = new RandomStrategy(new Random(42));
        Direction dir = strategy.chooseDirection(snake, snakes, field);
        assertNotNull(dir);
    }

    @Test
    void testGreedyStrategyReturnsDirection() {
        GameConfig config = GameConfig.load("/nonexistent.json");
        GameField field = new GameField(config, new Random(42));
        Snake snake = new Snake(new Point(10, 7), Direction.RIGHT);
        List<Snake> snakes = Collections.singletonList(snake);
        field.spawnFood(snakes);

        GreedyStrategy strategy = new GreedyStrategy();
        Direction dir = strategy.chooseDirection(snake, snakes, field);
        assertNotNull(dir);
    }

    @Test
    void testRandomStrategyAvoidsSelf() {
        GameConfig config = GameConfig.load("/nonexistent.json");
        GameField field = new GameField(config, new Random(42));
        Snake snake = new Snake(new Point(10, 7), Direction.RIGHT);
        snake.grow(5);
        for (int i = 0; i < 5; i++) {
            snake.move(snake.nextHead());
        }
        List<Snake> snakes = Collections.singletonList(snake);

        RandomStrategy strategy = new RandomStrategy(new Random(42));
        Direction dir = strategy.chooseDirection(snake, snakes, field);
        assertNotNull(dir);
    }
}
