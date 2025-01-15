package com.example.stoper;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.animation.AnimationTimer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HelloApplication extends Application {
    private long startTime = 0;
    private boolean running = false;
    private AnimationTimer timer;
    private List<String> laps = new ArrayList<>();
    private Label timeLabel;
    private long elapsedTime = 0;

    @Override
    public void start(Stage stage) {
        timeLabel = new Label("00:00:00.000");
        Button buttonStart = new Button("Start");
        Button buttonStop = new Button("Stop");
        Button buttonLap = new Button("Dodaj punkt");
        Button buttonSave = new Button("Zapisz");
        Button buttonReset = new Button("Reset");

        VBox root = new VBox(10, timeLabel, buttonStart, buttonStop, buttonLap, buttonSave, buttonReset);
        root.setStyle("-fx-padding: 20; -fx-alignment: center;");
        Scene scene = new Scene(root, 300, 250);

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (running) {
                    long currentTime = System.nanoTime();
                    elapsedTime = currentTime - startTime;
                    timeLabel.setText(formatTime(elapsedTime));
                }
            }
        };

        buttonStart.setOnAction(e -> {
            if (!running) {
                startTime = System.nanoTime() - elapsedTime;
                running = true;
                timer.start();
            }
        });

        buttonStop.setOnAction(e -> {
            if (running) {
                running = false;
                timer.stop();
                laps.add("Stop: " + formatTime(elapsedTime));
            }
        });

        buttonLap.setOnAction(e -> {
            if (running) {
                laps.add("Punkt: " + formatTime(elapsedTime));
                Label c = new Label("Punkt: " + formatTime(elapsedTime));
                root.getChildren().add(c);

            }
        });

        buttonSave.setOnAction(e -> {
            try {
                saveToFile();
            } catch (IOException ex) {
                timeLabel.setText("Błąd zapisu do pliku");
            }
        });

        buttonReset.setOnAction(e -> {
            running = false;
            timer.stop();
            elapsedTime = 0;
            laps.clear();
            timeLabel.setText("00:00:00.000");
        });

        stage.setTitle("Stoper");
        stage.setScene(scene);
        stage.show();
    }

    private String formatTime(long nanoseconds) {
        long totalMillis = nanoseconds / 1_000_000;
        long millis = totalMillis % 1000;
        long seconds = (totalMillis / 1000) % 60;
        long minutes = (totalMillis / (1000 * 60)) % 60;
        long hours = (totalMillis / (1000 * 60 * 60)) % 24;

        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis);
    }

    private void saveToFile() throws IOException {
        try (FileWriter writer = new FileWriter("stoper_output.txt")) {
            writer.write("Start: 00:00:00.000\n");
            for (String lap : laps) {
                writer.write(lap + "\n");
            }
            writer.write("Stop: " + formatTime(elapsedTime) + "\n");
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
