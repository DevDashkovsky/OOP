package ru.nsu.dashkovskii;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.nsu.dashkovskii.model.Direction;
import ru.nsu.dashkovskii.model.Food;
import ru.nsu.dashkovskii.model.GameConfig;
import ru.nsu.dashkovskii.model.GameField;
import ru.nsu.dashkovskii.model.Point;
import ru.nsu.dashkovskii.model.Snake;

/**
 * Тесты для класса GameField.
 */
class GameFieldTest {
    private GameConfig config;

    @BeforeEach
    void setUp() {
        config = GameConfig.load("/nonexistent.json");
    }

    @Test
    void testWrapPoint() {
        GameField field = new GameField(config);
        assertEquals(new Point(0, 0),
                field.wrapPoint(new Point(20, 15)));
        assertEquals(new Point(19, 14),
                field.wrapPoint(new Point(-1, -1)));
    }

    @Test
    void testSpawnFood() {
        GameField field = new GameField(config, new Random(42));
        Snake snake = new Snake(new Point(10, 7), Direction.RIGHT);
        field.spawnFood(Collections.singletonList(snake));
        assertEquals(config.getFoodCount(), field.getFoods().size());
    }

    @Test
    void testEatFood() {
        GameField field = new GameField(config, new Random(42));
        Snake snake = new Snake(new Point(10, 7), Direction.RIGHT);
        field.spawnFood(Collections.singletonList(snake));

        List<Food> foods = field.getFoods();
        assertFalse(foods.isEmpty());
        Point foodPos = foods.get(0).getPosition();
        Food eaten = field.eatFoodAt(foodPos);
        assertNotNull(eaten);
        assertEquals(foodPos, eaten.getPosition());

        assertNull(field.eatFoodAt(new Point(-1, -1)));
    }

}
