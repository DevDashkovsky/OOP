package ru.nsu.dashkovskii.model;

import com.google.gson.Gson;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * Конфигурация игры, загружаемая из JSON-файла.
 */
public class GameConfig {
    private int fieldWidth = 20;
    private int fieldHeight = 15;
    private int foodCount = 3;
    private int winLength = 20;
    private int initialSpeed = 5;
    private int maxSpeed = 15;
    private int botCount = 2;
    private int levelUpEvery = 5;
    private double bonusFoodChance = 0.2;
    private double speedFoodChance = 0.1;

    /**
     * Загружает конфигурацию из ресурсного JSON-файла.
     *
     * @param resourcePath путь к JSON-ресурсу
     * @return загруженная конфигурация
     */
    public static GameConfig load(String resourcePath) {
        try (Reader reader = new InputStreamReader(
                GameConfig.class.getResourceAsStream(resourcePath),
                StandardCharsets.UTF_8)) {
            return new Gson().fromJson(reader, GameConfig.class);
        } catch (Exception ex) {
            return new GameConfig();
        }
    }

    public int getFieldWidth() {
        return fieldWidth;
    }

    public int getFieldHeight() {
        return fieldHeight;
    }

    public int getFoodCount() {
        return foodCount;
    }

    public int getWinLength() {
        return winLength;
    }

    public int getInitialSpeed() {
        return initialSpeed;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public int getBotCount() {
        return botCount;
    }

    public int getLevelUpEvery() {
        return levelUpEvery;
    }

    public double getBonusFoodChance() {
        return bonusFoodChance;
    }

    public double getSpeedFoodChance() {
        return speedFoodChance;
    }
}
