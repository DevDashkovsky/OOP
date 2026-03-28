package ru.nsu.dashkovskii.controller;

import java.util.ArrayList;
import java.util.List;
import ru.nsu.dashkovskii.bot.BotSnake;
import ru.nsu.dashkovskii.bot.GreedyStrategy;
import ru.nsu.dashkovskii.bot.RandomStrategy;
import ru.nsu.dashkovskii.model.Direction;
import ru.nsu.dashkovskii.model.Food;
import ru.nsu.dashkovskii.model.FoodType;
import ru.nsu.dashkovskii.model.GameConfig;
import ru.nsu.dashkovskii.model.GameField;
import ru.nsu.dashkovskii.model.GameState;
import ru.nsu.dashkovskii.model.Point;
import ru.nsu.dashkovskii.model.Snake;

/**
 * Управляет игровой логикой: движением, столкновениями, едой, ботами и уровнями.
 */
public class GameController {
    private final GameField field;
    private final GameConfig config;
    private final Snake playerSnake;
    private final List<BotSnake> bots;
    private GameState state;
    private int currentSpeed;
    private int level;
    private int score;

    /**
     * Инициализирует новую игру с заданной конфигурацией.
     *
     * @param config конфигурация игры
     */
    public GameController(GameConfig config) {
        this.config = config;
        this.field = new GameField(config);
        this.currentSpeed = config.getInitialSpeed();
        this.level = 1;
        this.score = 0;

        Point center = new Point(config.getFieldWidth() / 2,
                config.getFieldHeight() / 2);
        this.playerSnake = new Snake(center, Direction.RIGHT);

        this.bots = new ArrayList<>();
        initBots();

        this.state = GameState.RUNNING;
        field.spawnFood(getAllSnakes());
    }

    /**
     * Конструктор для тестирования с пользовательским игровым полем.
     *
     * @param config конфигурация игры
     * @param field игровое поле для использования
     */
    public GameController(GameConfig config, GameField field) {
        this.config = config;
        this.field = field;
        this.currentSpeed = config.getInitialSpeed();
        this.level = 1;
        this.score = 0;

        Point center = new Point(config.getFieldWidth() / 2,
                config.getFieldHeight() / 2);
        this.playerSnake = new Snake(center, Direction.RIGHT);

        this.bots = new ArrayList<>();
        this.state = GameState.RUNNING;
    }

    private void initBots() {
        int botCount = config.getBotCount();
        int width = config.getFieldWidth();
        int height = config.getFieldHeight();
        Point[] starts = {
            new Point(2, 2),
            new Point(width - 3, height - 3),
            new Point(2, height - 3),
            new Point(width - 3, 2)
        };
        for (int i = 0; i < botCount && i < starts.length; i++) {
            if (i % 2 == 0) {
                bots.add(new BotSnake(starts[i], Direction.RIGHT,
                        new GreedyStrategy()));
            } else {
                bots.add(new BotSnake(starts[i], Direction.LEFT,
                        new RandomStrategy()));
            }
        }
    }

    public GameField getField() {
        return field;
    }

    public Snake getPlayerSnake() {
        return playerSnake;
    }

    public List<BotSnake> getBots() {
        return bots;
    }

    public GameState getState() {
        return state;
    }

    public int getCurrentSpeed() {
        return currentSpeed;
    }

    public int getLevel() {
        return level;
    }

    public int getScore() {
        return score;
    }

    /**
     * Устанавливает направление движения змейки игрока.
     *
     * @param dir желаемое направление
     */
    public void setPlayerDirection(Direction dir) {
        if (state == GameState.RUNNING) {
            playerSnake.setDirection(dir);
        }
    }

    /**
     * Переключает паузу.
     */
    public void togglePause() {
        if (state == GameState.RUNNING) {
            state = GameState.PAUSED;
        } else if (state == GameState.PAUSED) {
            state = GameState.RUNNING;
        }
    }

    /**
     * Продвигает игру на один шаг.
     * Возвращает текущее состояние игры после обновления.
     *
     * @return состояние игры после этого шага
     */
    public GameState tick() {
        if (state != GameState.RUNNING) {
            return state;
        }

        updateBotDirections();
        moveSnake(playerSnake);
        for (BotSnake bot : bots) {
            if (bot.getSnake().isAlive()) {
                moveSnake(bot.getSnake());
            }
        }

        checkCollisions();

        if (!playerSnake.isAlive()) {
            state = GameState.LOST;
            return state;
        }

        if (playerSnake.length() >= field.getWinLength()) {
            state = GameState.WON;
            return state;
        }

        field.spawnFood(getAllSnakes());
        updateLevel();

        return state;
    }

    private void moveSnake(Snake snake) {
        Point nextHead = snake.nextHead();
        Point wrapped = field.wrapPoint(nextHead);
        snake.move(wrapped);

        Food eaten = field.eatFoodAt(wrapped);
        if (eaten != null) {
            snake.grow(eaten.getType().getGrowthAmount());
            if (snake == playerSnake) {
                score += eaten.getType().getGrowthAmount();
                if (eaten.getType() == FoodType.SPEED_UP) {
                    increaseSpeed(1);
                }
            }
        }
    }

    private void checkCollisions() {
        List<Snake> all = getAllSnakes();
        for (Snake snake : all) {
            if (!snake.isAlive()) {
                continue;
            }
            Point head = snake.getHead();

            if (field.isObstacle(head)) {
                snake.kill();
                continue;
            }

            if (snake.collidesWith(head, true)) {
                snake.kill();
                continue;
            }

            for (Snake other : all) {
                if (other == snake || !other.isAlive()) {
                    continue;
                }
                if (other.collidesWith(head, false)) {
                    snake.kill();
                    break;
                }
            }
        }
    }

    private void updateBotDirections() {
        List<Snake> all = getAllSnakes();
        for (BotSnake bot : bots) {
            bot.updateDirection(all, field);
        }
    }

    private void updateLevel() {
        int newLevel = 1 + (playerSnake.length() - 1) / config.getLevelUpEvery();
        if (newLevel > level) {
            level = newLevel;
            increaseSpeed(1);
        }
    }

    private void increaseSpeed(int amount) {
        currentSpeed = Math.min(currentSpeed + amount, config.getMaxSpeed());
    }

    /**
     * Возвращает список всех змеек (игрок + боты).
     *
     * @return все змейки на поле
     */
    public List<Snake> getAllSnakes() {
        List<Snake> all = new ArrayList<>();
        all.add(playerSnake);
        for (BotSnake bot : bots) {
            all.add(bot.getSnake());
        }
        return all;
    }
}
