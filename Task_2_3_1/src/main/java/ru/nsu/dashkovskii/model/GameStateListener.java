package ru.nsu.dashkovskii.model;

/**
 * Слушатель изменений состояния игры.
 * Реализации получают уведомления после каждого обновления игровой модели.
 */
public interface GameStateListener {

    /**
     * Вызывается при изменении состояния игры.
     *
     * @param engine движок игры, состояние которого изменилось
     */
    void onGameStateChanged(GameEngine engine);
}
