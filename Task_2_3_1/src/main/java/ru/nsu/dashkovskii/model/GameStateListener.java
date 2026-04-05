package ru.nsu.dashkovskii.model;

/**
 * Слушатель изменений состояния игры.
 * Реализации получают неизменяемый снимок после каждого
 * обновления игровой модели.
 */
public interface GameStateListener {

    /**
     * Вызывается при изменении состояния игры.
     *
     * @param snapshot снимок текущего состояния игры
     */
    void onGameStateChanged(GameSnapshot snapshot);
}
