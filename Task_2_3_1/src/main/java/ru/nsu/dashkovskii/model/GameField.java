package ru.nsu.dashkovskii.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Игровое поле, содержащее змеек и еду.
 * Управляет появлением еды и условиями победы/поражения.
 */
public class GameField {
    private final int width;
    private final int height;
    private final int foodCount;
    private final int winLength;
    private final double bonusFoodChance;
    private final double speedFoodChance;
    private final List<Food> foods;
    private final Random random;

    /**
     * Создаёт игровое поле с заданной конфигурацией.
     *
     * @param config конфигурация игры
     */
    public GameField(GameConfig config) {
        this(config, new Random());
    }

    /**
     * Создаёт игровое поле с заданной конфигурацией и генератором случайных чисел.
     *
     * @param config конфигурация игры
     * @param random генератор случайных чисел (для тестирования)
     */
    public GameField(GameConfig config, Random random) {
        this.width = config.getFieldWidth();
        this.height = config.getFieldHeight();
        this.foodCount = config.getFoodCount();
        this.winLength = config.getWinLength();
        this.bonusFoodChance = config.getBonusFoodChance();
        this.speedFoodChance = config.getSpeedFoodChance();
        this.foods = new ArrayList<>();
        this.random = random;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<Food> getFoods() {
        return foods;
    }

    public int getWinLength() {
        return winLength;
    }

    /**
     * Оборачивает точку по границам поля (тороидальное поле).
     *
     * @param point точка для обёртки
     * @return новая точка в пределах границ поля
     */
    public Point wrapPoint(Point point) {
        int nx = ((point.getX() % width) + width) % width;
        int ny = ((point.getY() % height) + height) % height;
        return new Point(nx, ny);
    }

    /**
     * Спавнит еду до достижения требуемого количества на поле.
     *
     * @param snakes список всех змеек, чтобы не размещать еду на них
     */
    public void spawnFood(List<Snake> snakes) {
        Set<Point> occupied = getOccupiedCells(snakes);
        while (foods.size() < foodCount) {
            Point pos = findFreeCell(occupied);
            if (pos == null) {
                break;
            }
            FoodType type = randomFoodType();
            foods.add(new Food(pos, type));
            occupied.add(pos);
        }
    }

    /**
     * Проверяет, есть ли еда в заданной точке, и удаляет её при наличии.
     *
     * @param point точка для проверки
     * @return съеденная еда или null, если еды в этой точке нет
     */
    public Food eatFoodAt(Point point) {
        for (int i = 0; i < foods.size(); i++) {
            if (foods.get(i).getPosition().equals(point)) {
                return foods.remove(i);
            }
        }
        return null;
    }

    private Set<Point> getOccupiedCells(List<Snake> snakes) {
        Set<Point> occupied = new HashSet<>();
        for (Snake snake : snakes) {
            occupied.addAll(snake.getBodySet());
        }
        for (Food food : foods) {
            occupied.add(food.getPosition());
        }
        return occupied;
    }

    private Point findFreeCell(Set<Point> occupied) {
        int totalCells = width * height;
        if (occupied.size() >= totalCells) {
            return null;
        }
        Point candidate;
        do {
            candidate = new Point(random.nextInt(width), random.nextInt(height));
        } while (occupied.contains(candidate));
        return candidate;
    }

    private FoodType randomFoodType() {
        double roll = random.nextDouble();
        if (roll < speedFoodChance) {
            return FoodType.SPEED_UP;
        } else if (roll < speedFoodChance + bonusFoodChance) {
            return FoodType.BONUS;
        }
        return FoodType.NORMAL;
    }
}
