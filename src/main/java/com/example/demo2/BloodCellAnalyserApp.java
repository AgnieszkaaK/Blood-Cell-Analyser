package com.example.demo2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BloodCellAnalyserApp extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("bloodanalyse.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Blood Cell Analyser");
        stage.setScene(scene);
        stage.show();
    }
}