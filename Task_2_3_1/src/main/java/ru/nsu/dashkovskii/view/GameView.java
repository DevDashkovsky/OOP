package ru.nsu.dashkovskii.view;

import java.util.List;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import ru.nsu.dashkovskii.model.Direction;
import ru.nsu.dashkovskii.model.Food;
import ru.nsu.dashkovskii.model.FoodType;
import ru.nsu.dashkovskii.model.GameSnapshot;
import ru.nsu.dashkovskii.model.GameState;
import ru.nsu.dashkovskii.model.GameStateListener;
import ru.nsu.dashkovskii.model.Point;
import ru.nsu.dashkovskii.model.SnakeSnapshot;

/**
 * Отрисовывает состояние игры на JavaFX Canvas в стиле Nokia.
 */
public class GameView implements GameStateListener {
    private static final Color BG_LIGHT = Color.web("#9bbc0f");
    private static final Color BG_DARK = Color.web("#8bac0f");
    private static final Color PIXEL_ON = Color.web("#0f380f");
    private static final Color PIXEL_DIM = Color.web("#306230");
    private static final Color PIXEL_MID = Color.web("#4a6a2a");

    private final Canvas canvas;
    private double cellSize;
    private double offsetX;
    private double offsetY;

    public GameView(Canvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public void onGameStateChanged(GameSnapshot snapshot) {
        render(snapshot);
    }

    /**
     * Отрисовывает всё игровое состояние.
     *
     * @param snapshot снимок состояния игры для отрисовки
     */
    public void render(GameSnapshot snapshot) {
        int fw = snapshot.getFieldWidth();
        int fh = snapshot.getFieldHeight();
        cellSize = Math.min(canvas.getWidth() / fw,
                canvas.getHeight() / fh);
        cellSize = Math.floor(cellSize);
        offsetX = Math.floor(
                (canvas.getWidth() - fw * cellSize) / 2);
        offsetY = Math.floor(
                (canvas.getHeight() - fh * cellSize) / 2);

        final GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(BG_LIGHT);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.save();
        gc.translate(offsetX, offsetY);

        drawField(gc, fw, fh);
        drawObstacles(gc, snapshot);
        drawFood(gc, snapshot);
        drawSnake(gc, snapshot.getPlayerSnake(), PIXEL_ON);

        for (SnakeSnapshot bot : snapshot.getBotSnakes()) {
            drawSnake(gc, bot, PIXEL_DIM);
        }

        if (snapshot.getState() != GameState.RUNNING) {
            drawOverlay(gc, snapshot, fw, fh);
        }

        gc.restore();
    }

    private void drawField(GraphicsContext gc, int fw, int fh) {
        gc.setFill(BG_LIGHT);
        gc.fillRect(0, 0, fw * cellSize, fh * cellSize);

        gc.setStroke(BG_DARK);
        gc.setLineWidth(1);
        for (int x = 0; x <= fw; x++) {
            gc.strokeLine(x * cellSize, 0,
                    x * cellSize, fh * cellSize);
        }
        for (int y = 0; y <= fh; y++) {
            gc.strokeLine(0, y * cellSize,
                    fw * cellSize, y * cellSize);
        }
    }

    private void drawObstacles(GraphicsContext gc,
                                GameSnapshot snapshot) {
        for (Point pp : snapshot.getObstacles()) {
            fillCell(gc, pp.getX(), pp.getY(), PIXEL_DIM);
        }
    }

    private void drawFood(GraphicsContext gc,
                           GameSnapshot snapshot) {
        for (Food food : snapshot.getFoods()) {
            Color color;
            if (food.getType() == FoodType.BONUS) {
                color = PIXEL_ON;
            } else if (food.getType() == FoodType.SPEED_UP) {
                color = PIXEL_MID;
            } else {
                color = PIXEL_ON;
            }
            int fx = food.getPosition().getX();
            int fy = food.getPosition().getY();
            double px = fx * cellSize;
            double py = fy * cellSize;
            double m = cellSize * 0.25;
            gc.setFill(color);
            gc.fillRect(px + m, py + m,
                    cellSize - 2 * m, cellSize - 2 * m);
        }
    }

    private void drawSnake(GraphicsContext gc,
                           SnakeSnapshot snake,
                           Color color) {
        if (!snake.isAlive()) {
            return;
        }
        List<Point> body = snake.getBody();
        for (Point pp : body) {
            fillCell(gc, pp.getX(), pp.getY(), color);
        }

        if (!body.isEmpty()) {
            drawEyes(gc, body.get(0),
                    snake.getDirection(), color);
        }
    }

    private void fillCell(GraphicsContext gc, int cx, int cy,
                          Color color) {
        double px = cx * cellSize;
        double py = cy * cellSize;
        double m = 1;
        gc.setFill(color);
        gc.fillRect(px + m, py + m,
                cellSize - 2 * m, cellSize - 2 * m);
    }

    private void drawEyes(GraphicsContext gc, Point head,
                          Direction dir, Color snakeColor) {
        double px = head.getX() * cellSize;
        double py = head.getY() * cellSize;
        double eyeSize = Math.max(2, cellSize * 0.2);

        double lx;
        double ly;
        double rx;
        double ry;

        switch (dir) {
            case UP:
                lx = px + cellSize * 0.25;
                ly = py + cellSize * 0.2;
                rx = px + cellSize * 0.75 - eyeSize;
                ry = py + cellSize * 0.2;
                break;
            case DOWN:
                lx = px + cellSize * 0.25;
                ly = py + cellSize * 0.8 - eyeSize;
                rx = px + cellSize * 0.75 - eyeSize;
                ry = py + cellSize * 0.8 - eyeSize;
                break;
            case LEFT:
                lx = px + cellSize * 0.2;
                ly = py + cellSize * 0.25;
                rx = px + cellSize * 0.2;
                ry = py + cellSize * 0.75 - eyeSize;
                break;
            default:
                lx = px + cellSize * 0.8 - eyeSize;
                ly = py + cellSize * 0.25;
                rx = px + cellSize * 0.8 - eyeSize;
                ry = py + cellSize * 0.75 - eyeSize;
                break;
        }

        gc.setFill(BG_LIGHT);
        gc.fillRect(lx, ly, eyeSize, eyeSize);
        gc.fillRect(rx, ry, eyeSize, eyeSize);
    }

    private void drawOverlay(GraphicsContext gc,
                              GameSnapshot snapshot,
                              int fw, int fh) {
        gc.setFill(Color.color(
                BG_LIGHT.getRed(), BG_LIGHT.getGreen(),
                BG_LIGHT.getBlue(), 0.85));
        gc.fillRect(0, 0, fw * cellSize, fh * cellSize);

        String message;
        String sub;
        switch (snapshot.getState()) {
            case WON:
                message = "VICTORY!";
                sub = "Score: " + snapshot.getScore()
                        + "  R=restart";
                break;
            case LOST:
                message = "GAME OVER";
                sub = "Score: " + snapshot.getScore()
                        + "  R=restart";
                break;
            case PAUSED:
                message = "PAUSED";
                sub = "P=continue";
                break;
            default:
                message = "";
                sub = "";
                break;
        }

        double cx = fw * cellSize / 2;
        double cy = fh * cellSize / 2;

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(PIXEL_ON);
        gc.setFont(Font.font("Monospace", 28));
        gc.fillText(message, cx, cy - 6);

        gc.setFill(PIXEL_DIM);
        gc.setFont(Font.font("Monospace", 14));
        gc.fillText(sub, cx, cy + 22);
        gc.setTextAlign(TextAlignment.LEFT);
    }
}
