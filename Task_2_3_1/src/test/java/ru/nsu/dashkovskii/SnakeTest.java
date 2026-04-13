package ru.nsu.dashkovskii;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import ru.nsu.dashkovskii.model.Direction;
import ru.nsu.dashkovskii.model.Point;
import ru.nsu.dashkovskii.model.Snake;

/**
 * Тесты для класса Snake.
 */
class SnakeTest {

    @Test
    void testInitialState() {
        Snake snake = new Snake(new Point(5, 5), Direction.RIGHT);
        assertEquals(1, snake.length());
        assertEquals(new Point(5, 5), snake.getHead());
        assertTrue(snake.isAlive());
    }

    @Test
    void testMove() {
        Snake snake = new Snake(new Point(5, 5), Direction.RIGHT);
        snake.move(new Point(6, 5));
        assertEquals(new Point(6, 5), snake.getHead());
        assertEquals(1, snake.length());
    }

    @Test
    void testGrowAndMove() {
        Snake snake = new Snake(new Point(5, 5), Direction.RIGHT);
        snake.grow(1);
        snake.move(new Point(6, 5));
        assertEquals(2, snake.length());
        assertEquals(new Point(6, 5), snake.getHead());
    }

    @Test
    void testCannotReverse() {
        Snake snake = new Snake(new Point(5, 5), Direction.RIGHT);
        snake.grow(1);
        snake.move(new Point(6, 5));
        snake.setDirection(Direction.LEFT);
        assertEquals(Direction.RIGHT, snake.getDirection());
    }

    @Test
    void testSingleSegmentCanReverse() {
        Snake snake = new Snake(new Point(5, 5), Direction.RIGHT);
        snake.setDirection(Direction.LEFT);
        assertEquals(Direction.LEFT, snake.getDirection());
    }

    @Test
    void testKill() {
        Snake snake = new Snake(new Point(5, 5), Direction.RIGHT);
        snake.kill();
        assertFalse(snake.isAlive());
    }

    @Test
    void testSelfCollision() {
        Snake snake = new Snake(new Point(5, 5), Direction.RIGHT);
        snake.grow(3);
        snake.move(new Point(6, 5));
        snake.move(new Point(7, 5));
        snake.move(new Point(8, 5));
        assertEquals(4, snake.length());
        assertTrue(snake.collidesWith(new Point(7, 5), false));
        assertFalse(snake.collidesWith(new Point(8, 5), true));
    }

    @Test
    void testNextHead() {
        Snake snake = new Snake(new Point(3, 3), Direction.DOWN);
        assertEquals(new Point(3, 4), snake.nextHead());
    }
}
