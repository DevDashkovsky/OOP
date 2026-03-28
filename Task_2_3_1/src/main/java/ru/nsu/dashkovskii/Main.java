package ru.nsu.dashkovskii;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.nsu.dashkovskii.view.GameViewController;

/**
 * Точка входа в игровое приложение «Змейка».
 */
public class Main extends Application {
    private static final int WINDOW_WIDTH = 700;
    private static final int WINDOW_HEIGHT = 580;
    private static final int MIN_WIDTH = 400;
    private static final int MIN_HEIGHT = 350;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/ru/nsu/dashkovskii/game.fxml"));
        Parent root = loader.load();
        GameViewController viewController = loader.getController();

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.setOnKeyPressed(viewController::handleKeyPress);

        primaryStage.setTitle("Snake Game");
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Метод запуска JavaFX-приложения.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        launch(args);
    }
}
