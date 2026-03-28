package ru.nsu.dashkovskii.bot;

import java.util.ArrayList;
import java.util.List;
import ru.nsu.dashkovskii.model.Direction;
import ru.nsu.dashkovskii.model.Food;
import ru.nsu.dashkovskii.model.GameField;
import ru.nsu.dashkovskii.model.Point;
import ru.nsu.dashkovskii.model.Snake;

/**
 * Стратегия бота, жадно движущегося к ближайшей еде.
 */
public class GreedyStrategy implements BotStrategy {

    @Override
    public Direction chooseDirection(Snake self, List<Snake> allSnakes, GameField field) {
        Point head = self.getHead();
        Food nearest = findNearestFood(head, field);
        Direction best = self.getDirection();
        int bestDist = Integer.MAX_VALUE;

        Direction[] allDirs = Direction.values();
        List<Direction> safeDirs = new ArrayList<>();

        for (Direction dir : allDirs) {
            if (dir == self.getDirection().opposite() && self.length() > 1) {
                continue;
            }
            Point next = field.wrapPoint(head.move(dir));
            if (!isBlocked(next, self, allSnakes, field)) {
                safeDirs.add(dir);
            }
        }

        if (safeDirs.isEmpty()) {
            return best;
        }

        if (nearest != null) {
            Point target = nearest.getPosition();
            for (Direction dir : safeDirs) {
                Point next = field.wrapPoint(head.move(dir));
                int dist = manhattanDistance(next, target, field);
                if (dist < bestDist) {
                    bestDist = dist;
                    best = dir;
                }
            }
        } else {
            best = safeDirs.get(0);
        }

        return best;
    }

    private Food findNearestFood(Point head, GameField field) {
        Food nearest = null;
        int minDist = Integer.MAX_VALUE;
        for (Food food : field.getFoods()) {
            int dist = manhattanDistance(head, food.getPosition(), field);
            if (dist < minDist) {
                minDist = dist;
                nearest = food;
            }
        }
        return nearest;
    }

    private int manhattanDistance(Point from, Point to, GameField field) {
        int dx = Math.abs(from.getX() - to.getX());
        int dy = Math.abs(from.getY() - to.getY());
        dx = Math.min(dx, field.getWidth() - dx);
        dy = Math.min(dy, field.getHeight() - dy);
        return dx + dy;
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
