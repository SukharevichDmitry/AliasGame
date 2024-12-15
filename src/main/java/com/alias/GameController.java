package com.alias;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class GameController {
    private WordRepository wordRepository;
    private int leftTeamScore;
    private int rightTeamScore;
    private boolean isLeftTeamActive;
    private TimerService timer;
    private Label timerLabel;
    private Label leftScoreLabel;
    private Label rightScoreLabel;

    public GameController() {
        wordRepository = new WordRepository();
        leftTeamScore = 0;
        rightTeamScore = 0;
        isLeftTeamActive = true;
    }

    public void startGame(Stage stage) {
        BorderPane root = new BorderPane();

        // Устанавливаем фон в зависимости от команды, которая активна
        root.setStyle(isLeftTeamActive ? "-fx-background-color: lightblue;" : "-fx-background-color: lightcoral;");

        // Верхняя панель: Таймер
        timerLabel = new Label("60");
        timerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        root.setTop(timerLabel);
        BorderPane.setAlignment(timerLabel, javafx.geometry.Pos.CENTER);

        // Центральная часть: Слово
        Label wordLabel = new Label("Word: " + wordRepository.getRandomWord());
        wordLabel.setStyle("-fx-font-size: 24px;");
        root.setCenter(wordLabel);

        // Нижняя панель: Управление
        HBox controls = new HBox();
        controls.setSpacing(10);
        controls.setStyle("-fx-padding: 10px; -fx-alignment: center;");

        Button correctButton = new Button("Correct");
        correctButton.setOnAction(e -> {
            if (isLeftTeamActive) {
                leftTeamScore++;
            } else {
                rightTeamScore++;
            }
            updateScores();
            wordLabel.setText("Word: " + wordRepository.getRandomWord());
            checkForVictory(stage);
        });

        Button skipButton = new Button("Skip");
        skipButton.setOnAction(e -> wordLabel.setText("Word: " + wordRepository.getRandomWord()));

        controls.getChildren().addAll(correctButton, skipButton);
        root.setBottom(controls);

        // Левая и правая панели: Счёт
        leftScoreLabel = new Label("Left Team: 0");
        leftScoreLabel.setStyle("-fx-font-size: 18px;");
        rightScoreLabel = new Label("Right Team: 0");
        rightScoreLabel.setStyle("-fx-font-size: 18px;");
        root.setLeft(leftScoreLabel);
        root.setRight(rightScoreLabel);
        BorderPane.setAlignment(leftScoreLabel, javafx.geometry.Pos.CENTER_LEFT);
        BorderPane.setAlignment(rightScoreLabel, javafx.geometry.Pos.CENTER_RIGHT);

        // Таймер
        timer = new TimerService(60, () -> {
            // Переключаем команды по завершении таймера
            switchTeam(root);
            wordLabel.setText("Word: " + wordRepository.getRandomWord());  // Обновляем слово
            resetTimer(root, wordLabel); // Сбрасываем таймер на новое значение
        }, this::updateTimer);
        timer.start();

        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Alias Game");
        stage.show();
    }

    private void switchTeam(BorderPane root) {
        isLeftTeamActive = !isLeftTeamActive;
        root.setStyle(isLeftTeamActive ? "-fx-background-color: lightblue;" : "-fx-background-color: lightcoral;");
        resetTimer(root, null); // Сбрасываем таймер при переключении команды
    }

    private void updateScores() {
        leftScoreLabel.setText("Left Team: " + leftTeamScore);
        rightScoreLabel.setText("Right Team: " + rightTeamScore);
    }

    private void updateTimer() {
        Platform.runLater(() -> timerLabel.setText(String.valueOf(timer.getTimeRemaining())));
    }

    // Исправленный метод resetTimer с передачей root и wordLabel
    private void resetTimer(BorderPane root, Label wordLabel) {
        // Останавливаем старый таймер, если он существует
        if (timer != null) {
            timer.stop();
        }

        // Создаём новый таймер
        timer = new TimerService(60, () -> {
            // Переключаем команды по завершении таймера
            switchTeam(root);
            if (wordLabel != null) {
                wordLabel.setText("Word: " + wordRepository.getRandomWord());  // Обновляем слово
            }
            resetTimer(root, wordLabel); // Сбрасываем таймер на новое значение
        }, this::updateTimer);

        // Запускаем новый таймер
        timer.start();
    }

    private void checkForVictory(Stage stage) {
        if (leftTeamScore >= 50 || rightTeamScore >= 50) {
            String winner = leftTeamScore >= 50 ? "Left Team" : "Right Team";
            Alert alert = new Alert(Alert.AlertType.INFORMATION, winner + " wins!");
            alert.showAndWait();
            stage.close();
        }
    }
}
