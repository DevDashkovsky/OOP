package ru.nsu.dashkovskii.view;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import ru.nsu.dashkovskii.controller.GameController;
import ru.nsu.dashkovskii.model.Direction;
import ru.nsu.dashkovskii.model.GameConfig;
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

    private GameController gameController;
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
                gameView.render(gameController));
        canvasPane.heightProperty().addListener((obs, o, n) ->
                gameView.render(gameController));
    }

    private void startNewGame() {
        GameConfig config = GameConfig.load("/ru/nsu/dashkovskii/config.json");
        gameController = new GameController(config);
        lastUpdate = 0;

        if (timer != null) {
            timer.stop();
        }

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long interval = NANOS_PER_SECOND
                        / gameController.getCurrentSpeed();
                if (now - lastUpdate >= interval) {
                    gameController.tick();
                    gameView.render(gameController);
                    updateLabels();
                    lastUpdate = now;
                }
            }
        };
        timer.start();
        gameView.render(gameController);
        updateLabels();
    }

    /**
     * Обрабатывает события нажатия клавиш для управления змейкой и игровыми действиями.
     *
     * @param event событие нажатия клавиши
     */
    public void handleKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();
        switch (code) {
            case UP:
            case W:
                gameController.setPlayerDirection(Direction.UP);
                break;
            case DOWN:
            case S:
                gameController.setPlayerDirection(Direction.DOWN);
                break;
            case LEFT:
            case A:
                gameController.setPlayerDirection(Direction.LEFT);
                break;
            case RIGHT:
            case D:
                gameController.setPlayerDirection(Direction.RIGHT);
                break;
            case P:
                gameController.togglePause();
                if (gameController.getState() == GameState.PAUSED) {
                    gameView.render(gameController);
                    updateLabels();
                }
                break;
            case R:
                if (gameController.getState() == GameState.WON
                        || gameController.getState() == GameState.LOST) {
                    startNewGame();
                }
                break;
            default:
                break;
        }
    }

    private void updateLabels() {
        scoreLabel.setText("Счёт: " + gameController.getScore());
        levelLabel.setText("Уровень: " + gameController.getLevel());
        lengthLabel.setText("Длина: "
                + gameController.getPlayerSnake().length());

        switch (gameController.getState()) {
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
