package ru.nsu.dashkovskii.bot;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import ru.nsu.dashkovskii.model.Direction;
import ru.nsu.dashkovskii.model.Food;
import ru.nsu.dashkovskii.model.GameField;
import ru.nsu.dashkovskii.model.Point;
import ru.nsu.dashkovskii.model.Snake;

/**
 * Стратегия бота, движущегося полуслучайно: с вероятностью
 * стремится к еде, иначе выбирает направление с наибольшим
 * свободным пространством, чтобы не загнать себя в тупик.
 */
public class RandomStrategy implements BotStrategy {
    private static final double FOOD_CHASE_CHANCE = 0.6;
    private static final int FLOOD_LIMIT = 50;

    private final Random random;

    public RandomStrategy() {
        this(new Random());
    }

    public RandomStrategy(Random random) {
        this.random = random;
    }

    @Override
    public Direction chooseDirection(Snake self,
                                     List<Snake> allSnakes,
                                     GameField field) {
        Direction current = self.getDirection();
        Point head = self.getHead();
        List<Direction> safeDirs = getSafeDirs(
                head, current, self, allSnakes, field);

        if (safeDirs.isEmpty()) {
            return current;
        }

        if (random.nextDouble() < FOOD_CHASE_CHANCE) {
            Direction toFood = dirTowardFood(
                    head, self, allSnakes, field, safeDirs);
            if (toFood != null) {
                return toFood;
            }
        }

        return pickSafestDirection(
                head, self, allSnakes, field, safeDirs);
    }

    private List<Direction> getSafeDirs(Point head,
                                         Direction current,
                                         Snake self,
                                         List<Snake> allSnakes,
                                         GameField field) {
        List<Direction> safe = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            if (dir == current.opposite()
                    && self.length() > 1) {
                continue;
            }
            Point next = field.wrapPoint(head.move(dir));
            if (!isBlocked(next, self, allSnakes, field)) {
                safe.add(dir);
            }
        }
        return safe;
    }

    private Direction dirTowardFood(Point head,
                                     Snake self,
                                     List<Snake> allSnakes,
                                     GameField field,
                                     List<Direction> safeDirs) {
        Food nearest = null;
        int minDist = Integer.MAX_VALUE;
        for (Food food : field.getFoods()) {
            int dist = manhattanDistance(
                    head, food.getPosition(), field);
            if (dist < minDist) {
                minDist = dist;
                nearest = food;
            }
        }
        if (nearest == null) {
            return null;
        }
        Point target = nearest.getPosition();
        Direction best = null;
        int bestDist = Integer.MAX_VALUE;
        for (Direction dir : safeDirs) {
            Point next = field.wrapPoint(head.move(dir));
            int dist = manhattanDistance(next, target, field);
            if (dist < bestDist) {
                bestDist = dist;
                best = dir;
            }
        }
        return best;
    }

    private Direction pickSafestDirection(Point head,
                                           Snake self,
                                           List<Snake> allSnakes,
                                           GameField field,
                                           List<Direction> safeDirs) {
        Direction best = safeDirs.get(0);
        int bestSpace = -1;

        for (Direction dir : safeDirs) {
            Point next = field.wrapPoint(head.move(dir));
            int space = countReachable(
                    next, self, allSnakes, field);
            if (space > bestSpace) {
                bestSpace = space;
                best = dir;
            }
        }
        return best;
    }

    private int countReachable(Point start, Snake self,
                                List<Snake> allSnakes,
                                GameField field) {
        Queue<Point> queue = new ArrayDeque<>();
        Set<Point> visited = new HashSet<>();
        queue.add(start);
        visited.add(start);
        int count = 0;

        while (!queue.isEmpty() && count < FLOOD_LIMIT) {
            Point cur = queue.poll();
            count++;
            for (Direction dir : Direction.values()) {
                Point nb = field.wrapPoint(cur.move(dir));
                if (!visited.contains(nb)
                        && !isBlocked(nb, self,
                        allSnakes, field)) {
                    visited.add(nb);
                    queue.add(nb);
                }
            }
        }
        return count;
    }

    private int manhattanDistance(Point from, Point to,
                                  GameField field) {
        int dx = Math.abs(from.getX() - to.getX());
        int dy = Math.abs(from.getY() - to.getY());
        dx = Math.min(dx, field.getWidth() - dx);
        dy = Math.min(dy, field.getHeight() - dy);
        return dx + dy;
    }

    private boolean isBlocked(Point point, Snake self,
                              List<Snake> allSnakes,
                              GameField field) {
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
