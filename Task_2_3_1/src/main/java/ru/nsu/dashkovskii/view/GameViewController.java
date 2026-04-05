package ru.nsu.dashkovskii.view;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import ru.nsu.dashkovskii.model.Direction;
import ru.nsu.dashkovskii.model.GameConfig;
import ru.nsu.dashkovskii.model.GameEngine;
import ru.nsu.dashkovskii.model.GameState;

/**
 * FXML-контроллер, связывающий ввод, игровой цикл и отрисовку.
 */
public class GameViewController {
    private static final long NANOS_PER_SECOND = 1_000_000_000L;

    @FXML
    private Canvas gameCanvas;
    @FXML
    private Pane canvasPane;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label levelLabel;
    @FXML
    private Label lengthLabel;
    @FXML
    private Label stateLabel;

    private GameEngine gameEngine;
    private GameView gameView;
    private AnimationTimer timer;
    private long lastUpdate;

    /**
     * Вызывается после инициализации FXML-полей.
     */
    @FXML
    public void initialize() {
        gameCanvas.widthProperty().bind(canvasPane.widthProperty());
        gameCanvas.heightProperty().bind(canvasPane.heightProperty());

        gameView = new GameView(gameCanvas);
        startNewGame();

        canvasPane.widthProperty().addListener((obs, o, n) ->
                gameView.render(gameEngine));
        canvasPane.heightProperty().addListener((obs, o, n) ->
                gameView.render(gameEngine));
    }

    private void startNewGame() {
        GameConfig config = GameConfig.load(
                "/ru/nsu/dashkovskii/config.json");
        gameEngine = new GameEngine(config);
        gameEngine.addListener(gameView);
        gameEngine.addListener(engine -> updateLabels());
        lastUpdate = 0;

        if (timer != null) {
            timer.stop();
        }

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long interval = NANOS_PER_SECOND
                        / gameEngine.getCurrentSpeed();
                if (now - lastUpdate >= interval) {
                    gameEngine.tick();
                    lastUpdate = now;
                }
            }
        };
        timer.start();
        gameView.render(gameEngine);
        updateLabels();
    }

    /**
     * Обрабатывает события нажатия клавиш
     * для управления змейкой и игровыми действиями.
     *
     * @param event событие нажатия клавиши
     */
    public void handleKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();
        switch (code) {
            case UP:
            case W:
                gameEngine.setPlayerDirection(Direction.UP);
                break;
            case DOWN:
            case S:
                gameEngine.setPlayerDirection(Direction.DOWN);
                break;
            case LEFT:
            case A:
                gameEngine.setPlayerDirection(Direction.LEFT);
                break;
            case RIGHT:
            case D:
                gameEngine.setPlayerDirection(Direction.RIGHT);
                break;
            case P:
                gameEngine.togglePause();
                break;
            case R:
                if (gameEngine.getState() == GameState.WON
                        || gameEngine.getState() == GameState.LOST) {
                    startNewGame();
                }
                break;
            default:
                break;
        }
    }

    private void updateLabels() {
        scoreLabel.setText("Счёт: " + gameEngine.getScore());
        levelLabel.setText("Уровень: " + gameEngine.getLevel());
        lengthLabel.setText("Длина: "
                + gameEngine.getPlayerSnake().length());

        switch (gameEngine.getState()) {
            case WON:
                stateLabel.setText("Победа!");
                timer.stop();
                break;
            case LOST:
                stateLabel.setText("Игра окончена");
                timer.stop();
                break;
            case PAUSED:
                stateLabel.setText("Пауза");
                break;
            default:
                stateLabel.setText("");
                break;
        }
    }
}
