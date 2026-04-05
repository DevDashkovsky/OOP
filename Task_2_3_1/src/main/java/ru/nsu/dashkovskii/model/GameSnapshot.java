package ru.nsu.dashkovskii.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Неизменяемый снимок состояния игры.
 * Содержит только данные, необходимые для отрисовки и отображения.
 */
public class GameSnapshot {
    private final int fieldWidth;
    private final int fieldHeight;
    private final Set<Point> obstacles;
    private final List<Food> foods;
    private final SnakeSnapshot playerSnake;
    private final List<SnakeSnapshot> botSnakes;
    private final GameState state;
    private final int score;
    private final int level;
    private final int currentSpeed;

    /**
     * Создаёт снимок текущего состояния игры.
     *
     * @param engine движок игры
     */
    public GameSnapshot(GameEngine engine) {
        this.fieldWidth = engine.getField().getWidth();
        this.fieldHeight = engine.getField().getHeight();
        this.obstacles = new HashSet<>(
                engine.getField().getObstacles());
        this.foods = new ArrayList<>(
                engine.getField().getFoods());
        this.playerSnake = new SnakeSnapshot(
                engine.getPlayerSnake());
        this.botSnakes = new ArrayList<>();
        for (var bot : engine.getBots()) {
            this.botSnakes.add(
                    new SnakeSnapshot(bot.getSnake()));
        }
        this.state = engine.getState();
        this.score = engine.getScore();
        this.level = engine.getLevel();
        this.currentSpeed = engine.getCurrentSpeed();
    }

    public int getFieldWidth() {
        return fieldWidth;
    }

    public int getFieldHeight() {
        return fieldHeight;
    }

    public Set<Point> getObstacles() {
        return obstacles;
    }

    public List<Food> getFoods() {
        return foods;
    }

    public SnakeSnapshot getPlayerSnake() {
        return playerSnake;
    }

    public List<SnakeSnapshot> getBotSnakes() {
        return botSnakes;
    }

    public GameState getState() {
        return state;
    }

    public int getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }

    public int getCurrentSpeed() {
        return currentSpeed;
    }
}
