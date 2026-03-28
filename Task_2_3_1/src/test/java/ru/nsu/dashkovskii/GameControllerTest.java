package ru.nsu.dashkovskii;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import org.junit.jupiter.api.Test;
import ru.nsu.dashkovskii.controller.GameController;
import ru.nsu.dashkovskii.model.Direction;
import ru.nsu.dashkovskii.model.GameConfig;
import ru.nsu.dashkovskii.model.GameField;
import ru.nsu.dashkovskii.model.GameState;
import ru.nsu.dashkovskii.model.Point;

/**
 * Тесты для класса GameController.
 */
class GameControllerTest {

    @Test
    void testInitialState() {
        GameConfig config = GameConfig.load("/nonexistent.json");
        GameController controller = new GameController(config);
        assertEquals(GameState.RUNNING, controller.getState());
        assertEquals(1, controller.getPlayerSnake().length());
        assertEquals(1, controller.getLevel());
    }

    @Test
    void testTickMovesSnake() {
        GameConfig config = GameConfig.load("/nonexistent.json");
        GameField field = new GameField(config, new Random(42));
        GameController controller = new GameController(config, field);
        field.spawnFood(controller.getAllSnakes());

        Point headBefore = controller.getPlayerSnake().getHead();
        controller.tick();
        Point headAfter = controller.getPlayerSnake().getHead();
        assertEquals(
                field.wrapPoint(headBefore.move(Direction.RIGHT)),
                headAfter
        );
    }

    @Test
    void testPauseToggle() {
        GameConfig config = GameConfig.load("/nonexistent.json");
        GameController controller = new GameController(config);
        controller.togglePause();
        assertEquals(GameState.PAUSED, controller.getState());
        controller.togglePause();
        assertEquals(GameState.RUNNING, controller.getState());
    }

    @Test
    void testDirectionChange() {
        GameConfig config = GameConfig.load("/nonexistent.json");
        GameController controller = new GameController(config);
        controller.setPlayerDirection(Direction.DOWN);
        assertEquals(Direction.DOWN,
                controller.getPlayerSnake().getDirection());
    }

    @Test
    void testCollisionWithObstacle() {
        GameConfig config = GameConfig.load("/nonexistent.json");
        GameField field = new GameField(config, new Random(42));
        GameController controller = new GameController(config, field);

        Point nextHead = field.wrapPoint(
                controller.getPlayerSnake().getHead()
                        .move(Direction.RIGHT));
        field.addObstacle(nextHead);
        field.spawnFood(controller.getAllSnakes());

        controller.tick();
        assertEquals(GameState.LOST, controller.getState());
    }

    @Test
    void testBotCount() {
        GameConfig config = GameConfig.load("/nonexistent.json");
        GameController controller = new GameController(config);
        assertTrue(controller.getBots().size() <= config.getBotCount());
    }
}
