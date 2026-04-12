package ru.nsu.dashkovskii;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import org.junit.jupiter.api.Test;
import ru.nsu.dashkovskii.model.Direction;
import ru.nsu.dashkovskii.model.GameConfig;
import ru.nsu.dashkovskii.model.GameEngine;
import ru.nsu.dashkovskii.model.GameField;
import ru.nsu.dashkovskii.model.GameState;
import ru.nsu.dashkovskii.model.Point;

/**
 * Тесты для класса GameEngine.
 */
class GameEngineTest {

    @Test
    void testInitialState() {
        GameConfig config = GameConfig.load("/nonexistent.json");
        GameEngine engine = new GameEngine(config);
        assertEquals(GameState.RUNNING, engine.getState());
        assertEquals(1, engine.getPlayerSnake().length());
        assertEquals(1, engine.getLevel());
    }

    @Test
    void testTickMovesSnake() {
        GameConfig config = GameConfig.load("/nonexistent.json");
        GameField field = new GameField(config, new Random(42));
        GameEngine engine = new GameEngine(config, field);
        field.spawnFood(engine.getAllSnakes());

        Point headBefore = engine.getPlayerSnake().getHead();
        engine.tick();
        Point headAfter = engine.getPlayerSnake().getHead();
        assertEquals(
                field.wrapPoint(headBefore.move(Direction.RIGHT)),
                headAfter
        );
    }

    @Test
    void testPauseToggle() {
        GameConfig config = GameConfig.load("/nonexistent.json");
        GameEngine engine = new GameEngine(config);
        engine.togglePause();
        assertEquals(GameState.PAUSED, engine.getState());
        engine.togglePause();
        assertEquals(GameState.RUNNING, engine.getState());
    }

    @Test
    void testDirectionChange() {
        GameConfig config = GameConfig.load("/nonexistent.json");
        GameEngine engine = new GameEngine(config);
        engine.setPlayerDirection(Direction.DOWN);
        assertEquals(Direction.DOWN,
                engine.getPlayerSnake().getDirection());
    }


    @Test
    void testBotCount() {
        GameConfig config = GameConfig.load("/nonexistent.json");
        GameEngine engine = new GameEngine(config);
        assertTrue(engine.getBots().size() <= config.getBotCount());
    }

    @Test
    void testListenerNotifiedOnTick() {
        GameConfig config = GameConfig.load("/nonexistent.json");
        GameEngine engine = new GameEngine(config);
        int[] callCount = {0};
        engine.addListener(e -> callCount[0]++);
        engine.tick();
        assertEquals(1, callCount[0]);
    }

    @Test
    void testListenerNotifiedOnPause() {
        GameConfig config = GameConfig.load("/nonexistent.json");
        GameEngine engine = new GameEngine(config);
        int[] callCount = {0};
        engine.addListener(e -> callCount[0]++);
        engine.togglePause();
        assertEquals(1, callCount[0]);
    }
}
