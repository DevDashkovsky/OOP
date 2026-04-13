package ru.nsu.dashkovskii;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import ru.nsu.dashkovskii.model.GameConfig;
import ru.nsu.dashkovskii.model.GameEngine;
import ru.nsu.dashkovskii.view.GameView;
import ru.nsu.dashkovskii.view.GameViewController;

/**
 * Точка входа в игровое приложение «Змейка».
 * Создаёт основные компоненты (модель и view) и передаёт
 * их в контроллер.
 */
public class Main extends Application {
    private static final int WINDOW_WIDTH = 700;
    private static final int WINDOW_HEIGHT = 580;
    private static final int MIN_WIDTH = 400;
    private static final int MIN_HEIGHT = 350;

    @Override
    public void start(Stage primaryStage) {
        try {
            java.net.URL fxmlUrl = getClass().getResource(
                    "/ru/nsu/dashkovskii/game.fxml");
            if (fxmlUrl == null) {
                throw new IllegalStateException(
                        "Файл разметки интерфейса 'game.fxml' не найден "
                        + "по пути /ru/nsu/dashkovskii/game.fxml.\n"
                        + "Возможно, файл был переименован или удалён.");
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            GameViewController controller =
                    loader.getController();

            boolean configMissing = getClass().getResourceAsStream(
                    "/ru/nsu/dashkovskii/config.json") == null;
            GameConfig config = GameConfig.load(
                    "/ru/nsu/dashkovskii/config.json");
            if (configMissing) {
                Alert warn = new Alert(Alert.AlertType.WARNING);
                warn.setTitle("Конфигурация не найдена");
                warn.setHeaderText("Файл 'config.json' не найден");
                warn.setContentText(
                        "Файл конфигурации config.json не найден "
                        + "по пути /ru/nsu/dashkovskii/config.json.\n"
                        + "Возможно, файл был переименован или удалён.\n"
                        + "Игра запустится с настройками по умолчанию.");
                warn.showAndWait();
            }
            GameEngine engine = new GameEngine(config);
            GameView view = new GameView(
                    controller.getGameCanvas());

            controller.init(engine, view);

            Scene scene = new Scene(
                    root, WINDOW_WIDTH, WINDOW_HEIGHT);
            scene.setOnKeyPressed(controller::handleKeyPress);

            primaryStage.setTitle("Snake Game");
            primaryStage.setMinWidth(MIN_WIDTH);
            primaryStage.setMinHeight(MIN_HEIGHT);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Не удалось запустить игру");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
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
