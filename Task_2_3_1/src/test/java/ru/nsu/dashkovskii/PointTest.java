package ru.nsu.dashkovskii;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import ru.nsu.dashkovskii.model.Direction;
import ru.nsu.dashkovskii.model.Point;

/**
 * Тесты для класса Point.
 */
class PointTest {

    @Test
    void testMoveRight() {
        Point pp = new Point(3, 4);
        Point moved = pp.move(Direction.RIGHT);
        assertEquals(new Point(4, 4), moved);
    }

    @Test
    void testMoveLeft() {
        Point pp = new Point(3, 4);
        assertEquals(new Point(2, 4), pp.move(Direction.LEFT));
    }

    @Test
    void testMoveUp() {
        Point pp = new Point(3, 4);
        assertEquals(new Point(3, 3), pp.move(Direction.UP));
    }

    @Test
    void testMoveDown() {
        Point pp = new Point(3, 4);
        assertEquals(new Point(3, 5), pp.move(Direction.DOWN));
    }

    @Test
    void testEquality() {
        assertEquals(new Point(1, 2), new Point(1, 2));
        assertNotEquals(new Point(1, 2), new Point(2, 1));
    }

    @Test
    void testHashCode() {
        assertEquals(new Point(5, 5).hashCode(), new Point(5, 5).hashCode());
    }

    @Test
    void testToString() {
        assertEquals("(3, 4)", new Point(3, 4).toString());
    }
}
