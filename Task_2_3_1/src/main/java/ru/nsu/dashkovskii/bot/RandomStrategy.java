package ru.nsu.dashkovskii.bot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import ru.nsu.dashkovskii.model.Direction;
import ru.nsu.dashkovskii.model.GameField;
import ru.nsu.dashkovskii.model.Point;
import ru.nsu.dashkovskii.model.Snake;

/**
 * Стратегия бота, движущегося случайным образом и избегающего немедленных столкновений.
 */
public class RandomStrategy implements BotStrategy {
    private final Random random;

    public RandomStrategy() {
        this(new Random());
    }

    public RandomStrategy(Random random) {
        this.random = random;
    }

    @Override
    public Direction chooseDirection(Snake self, List<Snake> allSnakes, GameField field) {
        Direction current = self.getDirection();
        Direction[] allDirs = Direction.values();
        List<Direction> safe = new ArrayList<>();
        for (Direction dir : allDirs) {
            if (dir == current.opposite() && self.length() > 1) {
                continue;
            }
            Point next = field.wrapPoint(self.getHead().move(dir));
            if (!isBlocked(next, self, allSnakes, field)) {
                safe.add(dir);
            }
        }
        if (safe.isEmpty()) {
            return current;
        }
        return safe.get(random.nextInt(safe.size()));
    }

    private boolean isBlocked(Point point, Snake self,
                              List<Snake> allSnakes, GameField field) {
        if (field.isObstacle(point)) {
            return true;
        }
        for (Snake snake : allSnakes) {
            if (snake == self) {
                if (snake.collidesWith(point, true)) {
                    return true;
                }
            } else if (snake.collidesWith(point, false)) {
                return true;
            }
        }
        return false;
    }
}
