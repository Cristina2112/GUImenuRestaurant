package org.example.view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.controller.ManagerController;
import org.example.model.*;

import java.io.File;
import java.time.LocalDateTime;

public class ManagerView {

    private final ManagerController controller;
    private final User loggedUser;

    public ManagerView(User user) {
        this.loggedUser = user;
        this.controller = new ManagerController();
    }

    public void start(Stage stage) {
        stage.setTitle("Panou ADMIN - " + loggedUser.getUsername());

        BorderPane root = new BorderPane();

        // HEADER
        HBox header = new HBox(10);
        header.setPadding(new Insets(10));
        header.setAlignment(Pos.CENTER_RIGHT);
        header.setStyle("-fx-background-color: #333;");

        Label userLabel = new Label("Logat ca: " + loggedUser.getUsername() + " (ADMIN)");
        userLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Button logoutBtn = new Button("Deconectare 🚪");
        logoutBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        logoutBtn.setOnAction(e -> {
            controller.shutdown(); // Oprim executor service la logout
            stage.close();
            new LoginView().start(new Stage());
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(userLabel, spacer, logoutBtn);
        root.setTop(header);

        // TABURI
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab menuTab = new Tab("Meniu", createMenuTab());
        Tab offersTab = new Tab("Setări Oferte", createOffersTab());
        Tab historyTab = new Tab("Istoric Comenzi", createHistoryTab());
        Tab staffTab = new Tab("Angajați", createStaffTab());

        tabPane.getTabs().addAll(menuTab, offersTab, historyTab, staffTab);
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 950, 650);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    private VBox createMenuTab() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        TableView<Product> table = new TableView<>();
        TableColumn<Product, Long> idCol = new TableColumn<>("ID"); idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Product, String> nameCol = new TableColumn<>("Nume"); nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Product, Double> priceCol = new TableColumn<>("Preț"); priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        table.getColumns().addAll(idCol, nameCol, priceCol);
        table.setItems(FXCollections.observableArrayList(controller.getAllProducts()));

        HBox crudControls = new HBox(10);
        TextField nameField = new TextField(); nameField.setPromptText("Nume");
        TextField priceField = new TextField(); priceField.setPromptText("Preț");
        TextField gramField = new TextField(); gramField.setPromptText("Grame/Ml");
        ComboBox<String> typeBox = new ComboBox<>(); typeBox.getItems().addAll("Mâncare", "Băutură"); typeBox.setValue("Mâncare");

        Button addBtn = new Button("Adaugă");
        addBtn.setOnAction(e -> {
            try {
                String name = nameField.getText();
                double price = Double.parseDouble(priceField.getText());
                double grams = Double.parseDouble(gramField.getText());
                Product p = typeBox.getValue().equals("Mâncare") ? new Food(name, price, grams) : new Drinks(name, price, grams, null);
                controller.addProduct(p);
                table.setItems(FXCollections.observableArrayList(controller.getAllProducts()));
                nameField.clear(); priceField.clear(); gramField.clear();
            } catch (Exception ex) { new Alert(Alert.AlertType.ERROR, "Date invalide!").show(); }
        });

        Button deleteBtn = new Button("Șterge");
        deleteBtn.setStyle("-fx-background-color: #ffcccc;");
        deleteBtn.setOnAction(e -> {
            Product selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                controller.deleteProduct(selected.getId());
                table.setItems(FXCollections.observableArrayList(controller.getAllProducts()));
            }
        });
        crudControls.getChildren().addAll(nameField, priceField, gramField, typeBox, addBtn, deleteBtn);

        HBox jsonControls = new HBox(10);
        jsonControls.setAlignment(Pos.CENTER_RIGHT);
        jsonControls.setPadding(new Insets(10, 0, 0, 0));

        Button exportBtn = new Button("💾 Export Meniu JSON");
        exportBtn.setOnAction(e -> {
            try {
                controller.exportMenu("LaAndrei");
                new Alert(Alert.AlertType.INFORMATION, "Meniu exportat cu succes!").show();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Eroare la export: " + ex.getMessage()).show();
            }
        });

        Button importBtn = new Button("📂 Import JSON");
        importBtn.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white;");
        importBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Selectează fișierul JSON");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
            File selectedFile = fileChooser.showOpenDialog(importBtn.getScene().getWindow());

            if (selectedFile != null) {
                try {
                    controller.importMenu(selectedFile);
                    table.setItems(FXCollections.observableArrayList(controller.getAllProducts()));
                    new Alert(Alert.AlertType.INFORMATION, "Import realizat cu succes!").show();
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, "Eroare import: " + ex.getMessage()).show();
                }
            }
        });

        jsonControls.getChildren().addAll(importBtn, exportBtn);
        root.getChildren().addAll(table, crudControls, new Separator(), jsonControls);
        return root;
    }

    private VBox createOffersTab() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #f0f8ff;");

        Label title = new Label("Configurare Oferte Active");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        CheckBox hhCheck = new CheckBox("Activează HAPPY HOUR (-50% la a 2-a băutură)");
        hhCheck.setSelected(controller.isHappyHourActive());
        hhCheck.setOnAction(e -> controller.setHappyHour(hhCheck.isSelected()));

        CheckBox mealCheck = new CheckBox("Activează MEAL DEAL (Pizza + Desert redus)");
        mealCheck.setSelected(controller.isMealDealActive());
        mealCheck.setOnAction(e -> controller.setMealDeal(mealCheck.isSelected()));

        CheckBox partyCheck = new CheckBox("Activează PARTY PACK (4 Pizza -> 1 Gratis)");
        partyCheck.setSelected(controller.isPartyPackActive());
        partyCheck.setOnAction(e -> controller.setPartyPack(partyCheck.isSelected()));

        root.getChildren().addAll(title, hhCheck, mealCheck, partyCheck);
        return root;
    }

    //it 8
    private VBox createHistoryTab() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        TableView<Order> table = new TableView<>();

        TableColumn<Order, Long> idCol = new TableColumn<>("ID Comandă");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Order, Double> totalCol = new TableColumn<>("Total (RON)");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        TableColumn<Order, LocalDateTime> dateCol = new TableColumn<>("Data");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        TableColumn<Order, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(idCol, totalCol, dateCol, statusCol);

        // element vizual de încărcare (Spinner)
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setVisible(false);
        spinner.setPrefSize(25, 25);

        Button refreshBtn = new Button("🔄 Reîmprospătează Lista");
        refreshBtn.setOnAction(e -> {
            refreshBtn.setDisable(true); // dezactivăm butonul să nu se apese de 2 ori

            controller.getAllOrdersAsync(
                    // OnSuccess (cand vin datele)
                    (orders) -> {
                        table.setItems(FXCollections.observableArrayList(orders));
                        spinner.setVisible(false);  // Ascundem spinner
                        refreshBtn.setDisable(false); // Reactivăm butonul
                    },
                    // OnLoading (cand începe așteptarea)
                    () -> {
                        spinner.setVisible(true); // Arătăm spinner
                        // Opțional: table.getItems().clear();
                    }
            );
        });


        HBox controls = new HBox(10, refreshBtn, spinner);
        controls.setAlignment(Pos.CENTER_LEFT);

        root.getChildren().addAll(new Label("Istoric Vânzări (Async Load)"), controls, table);
        return root;
    }

    private VBox createStaffTab() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));

        Label title = new Label("Gestiune Personal (Ospătari)");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        TableView<User> table = new TableView<>();
        TableColumn<User, String> userCol = new TableColumn<>("Username");
        userCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        TableColumn<User, String> roleCol = new TableColumn<>("Rol");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        table.getColumns().addAll(userCol, roleCol);
        table.setItems(FXCollections.observableArrayList(controller.getAllStaffUsers()));

        HBox addBox = new HBox(10);
        TextField userField = new TextField(); userField.setPromptText("User nou");
        PasswordField passField = new PasswordField(); passField.setPromptText("Parola");

        Button createBtn = new Button("Adaugă Ospătar");
        createBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        createBtn.setOnAction(e -> {
            if (!userField.getText().isEmpty() && !passField.getText().isEmpty()) {
                controller.addUser(userField.getText(), passField.getText(), Role.STAFF);
                table.setItems(FXCollections.observableArrayList(controller.getAllStaffUsers()));
                userField.clear(); passField.clear();
            }
        });
        addBox.getChildren().addAll(userField, passField, createBtn);

        Button deleteBtn = new Button("Șterge Ospătar Selectat");
        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> {
            User selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmare Ștergere");
                alert.setHeaderText("Ștergere utilizator: " + selected.getUsername());
                alert.setContentText("ATENȚIE! Comenzile vor fi șterse (Cascade).\nSunteți sigur?");

                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        controller.deleteUser(selected.getId());
                        table.setItems(FXCollections.observableArrayList(controller.getAllStaffUsers()));
                    }
                });
            } else {
                new Alert(Alert.AlertType.WARNING, "Selectați un ospătar!").show();
            }
        });

        root.getChildren().addAll(title, table, addBox, new Separator(), deleteBtn);
        return root;
    }
}