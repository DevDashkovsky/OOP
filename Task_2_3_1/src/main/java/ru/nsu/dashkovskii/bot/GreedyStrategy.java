package ru.nsu.dashkovskii.bot;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import ru.nsu.dashkovskii.model.Direction;
import ru.nsu.dashkovskii.model.Food;
import ru.nsu.dashkovskii.model.GameField;
import ru.nsu.dashkovskii.model.Point;
import ru.nsu.dashkovskii.model.Snake;

/**
 * Стратегия бота, использующая BFS для поиска кратчайшего
 * безопасного пути к ближайшей еде.
 */
public class GreedyStrategy implements BotStrategy {
    private static final int MAX_BFS_STEPS = 200;

    @Override
    public Direction chooseDirection(Snake self,
                                     List<Snake> allSnakes,
                                     GameField field) {
        Point head = self.getHead();
        Direction current = self.getDirection();

        Direction[] allDirs = Direction.values();
        List<Direction> safeDirs = new ArrayList<>();

        for (Direction dir : allDirs) {
            if (dir == current.opposite() && self.length() > 1) {
                continue;
            }
            Point next = field.wrapPoint(head.move(dir));
            if (!isBlocked(next, self, allSnakes, field)) {
                safeDirs.add(dir);
            }
        }

        if (safeDirs.isEmpty()) {
            return current;
        }

        Direction bfsDir = bfsToFood(head, self, allSnakes,
                field, safeDirs);
        if (bfsDir != null) {
            return bfsDir;
        }

        return pickSafestDirection(head, self, allSnakes,
                field, safeDirs);
    }

    private Direction bfsToFood(Point start, Snake self,
                                 List<Snake> allSnakes,
                                 GameField field,
                                 List<Direction> safeDirs) {
        Queue<Point> queue = new ArrayDeque<>();
        Map<Point, Direction> firstStep = new HashMap<>();

        for (Direction dir : safeDirs) {
            Point next = field.wrapPoint(start.move(dir));
            if (!firstStep.containsKey(next)) {
                firstStep.put(next, dir);
                queue.add(next);
            }
        }

        Map<Point, Boolean> visited = new HashMap<>();
        for (Point p : firstStep.keySet()) {
            visited.put(p, true);
        }

        int steps = 0;
        while (!queue.isEmpty() && steps < MAX_BFS_STEPS) {
            Point cur = queue.poll();
            steps++;

            for (Food food : field.getFoods()) {
                if (food.getPosition().equals(cur)) {
                    return firstStep.get(cur);
                }
            }

            for (Direction dir : Direction.values()) {
                Point neighbor = field.wrapPoint(cur.move(dir));
                if (!visited.containsKey(neighbor)
                        && !isBlocked(neighbor, self,
                        allSnakes, field)) {
                    visited.put(neighbor, true);
                    firstStep.put(neighbor, firstStep.get(cur));
                    queue.add(neighbor);
                }
            }
        }
        return null;
    }

    private Direction pickSafestDirection(Point head, Snake self,
                                           List<Snake> allSnakes,
                                           GameField field,
                                           List<Direction> safeDirs) {
        Direction best = safeDirs.get(0);
        int bestSpace = -1;

        for (Direction dir : safeDirs) {
            Point next = field.wrapPoint(head.move(dir));
            int space = countReachable(next, self, allSnakes,
                    field);
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
        Map<Point, Boolean> visited = new HashMap<>();
        queue.add(start);
        visited.put(start, true);
        int count = 0;
        int limit = 50;

        while (!queue.isEmpty() && count < limit) {
            Point cur = queue.poll();
            count++;
            for (Direction dir : Direction.values()) {
                Point neighbor = field.wrapPoint(cur.move(dir));
                if (!visited.containsKey(neighbor)
                        && !isBlocked(neighbor, self,
                        allSnakes, field)) {
                    visited.put(neighbor, true);
                    queue.add(neighbor);
                }
            }
        }
        return count;
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
