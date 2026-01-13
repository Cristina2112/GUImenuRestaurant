package org.example.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.controller.LoginController;
import org.example.model.User;

public class LoginView {

    private final LoginController controller = new LoginController();

    public void start(Stage stage) {
        stage.setTitle("Login - Restaurant La Andrei");

        // layout Principal
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f0f0f0;");

        // titlu
        Label titleLabel = new Label("Bine ați venit!");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // formular
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        TextField userField = new TextField();
        userField.setPromptText("Utilizator");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Parolă");

        grid.add(new Label("User:"), 0, 0);
        grid.add(userField, 1, 0);
        grid.add(new Label("Parolă:"), 0, 1);
        grid.add(passField, 1, 1);

        // butoane
        Button loginButton = new Button("Autentificare");
        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        loginButton.setPrefWidth(200);

        Button guestButton = new Button("Continuați ca Vizitator (Guest)");
        guestButton.setPrefWidth(200);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

      //logica navigare

        // login angajați
        loginButton.setOnAction(e -> {
            String user = userField.getText();
            String pass = passField.getText();

            User foundUser = controller.login(user, pass);

            if (foundUser != null) {
                // dacă login-ul e ok, verificăm rolul și deschidem fereastra potrivită
                switch (foundUser.getRole()) {
                    case ADMIN:
                        new ManagerView(foundUser).start(stage);
                        break;
                    case STAFF:
                        new StaffView(foundUser).start(stage);
                        break;
                    default:
                        errorLabel.setText("Eroare: Rol necunoscut!");
                }
            } else {
                errorLabel.setText("User sau parolă incorectă!");
            }
        });

        // login Guest
        guestButton.setOnAction(e -> {
            // deschide direct fereastra de client
            new GuestView().start(stage);
        });

        root.getChildren().addAll(titleLabel, grid, loginButton, guestButton, errorLabel);

        Scene scene = new Scene(root, 400, 450);
        stage.setScene(scene);
        stage.show();
    }
}