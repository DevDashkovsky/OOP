package ru.nsu.dashkovskii.bot;

import java.util.List;
import ru.nsu.dashkovskii.model.Direction;
import ru.nsu.dashkovskii.model.GameField;
import ru.nsu.dashkovskii.model.Snake;

/**
 * Интерфейс стратегии поведения змейки-бота.
 */
public interface BotStrategy {

    /**
     * Выбирает следующее направление движения змейки-бота.
     *
     * @param self змейка бота
     * @param allSnakes все змейки на поле
     * @param field игровое поле
     * @return выбранное направление
     */
    Direction chooseDirection(Snake self, List<Snake> allSnakes, GameField field);
}
