package ru.nsu.dashkovskii.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Неизменяемый снимок состояния игры.
 * Содержит только данные, необходимые для отрисовки и отображения.
 */
public record GameSnapshot(int fieldWidth,
                            int fieldHeight,
                            List<Food> foods,
                            SnakeSnapshot playerSnake,
                            List<SnakeSnapshot> botSnakes,
                            GameState state,
                            int score,
                            int level,
                            int currentSpeed) {

    /**
     * Создаёт снимок текущего состояния игры.
     *
     * @param engine движок игры
     * @return снимок
     */
    public static GameSnapshot of(GameEngine engine) {
        List<SnakeSnapshot> bots = new ArrayList<>();
        for (var bot : engine.getBots()) {
            bots.add(SnakeSnapshot.of(bot.getSnake()));
        }
        return new GameSnapshot(
                engine.getField().getWidth(),
                engine.getField().getHeight(),
                new ArrayList<>(engine.getField().getFoods()),
                SnakeSnapshot.of(engine.getPlayerSnake()),
                bots,
                engine.getState(),
                engine.getScore(),
                engine.getLevel(),
                engine.getCurrentSpeed());
    }
}
