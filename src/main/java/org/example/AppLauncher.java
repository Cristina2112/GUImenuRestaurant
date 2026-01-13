package org.example;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.util.DatabaseSeeder;
import org.example.view.LoginView;

public class AppLauncher extends Application {

    public static void main(String[] args) {
        // pregătim baza de date (dacă e goală)
        new DatabaseSeeder().ensureDatabaseIsPopulated();

        // pornim JavaFX
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // spunem aplicației să deschidă LoginView, NU RestaurantGUI.
        new LoginView().start(primaryStage);
    }
}