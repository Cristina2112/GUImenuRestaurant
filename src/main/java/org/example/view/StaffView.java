package org.example.view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.controller.StaffController;
import org.example.model.*;

import java.time.LocalDateTime;
import java.util.List;

public class StaffView {

    private final StaffController controller;

    private TableView<Product> menuTable = new TableView<>();
    private ListView<String> receiptList = new ListView<>();
    private Label totalLabel = new Label("Total: 0.00 RON");
    private Label selectedTableLabel = new Label("MASA: NESELECTATĂ");

    private Label detailName = new Label("Selectează un produs");
    private Label detailInfo = new Label("-");

    public StaffView(User user) {
        this.controller = new StaffController(user);
    }

    public void start(Stage stage) {
        stage.setTitle("POS Ospătar - " + stage.getTitle());

        BorderPane mainLayout = new BorderPane();

        // LOGOUT
        HBox header = new HBox(10);
        header.setPadding(new Insets(10));
        header.setAlignment(Pos.CENTER_RIGHT);
        header.setStyle("-fx-background-color: #1976D2;");

        Label titleLabel = new Label("SISTEM GESTIUNE COMENZI  ");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Button logoutBtn = new Button("Deconectare 🚪");
        logoutBtn.setStyle("-fx-background-color: #ffcccb;");
        logoutBtn.setOnAction(e -> {
            controller.startNewOrder();
            stage.close();
            new LoginView().start(new Stage());
        });
        header.getChildren().addAll(titleLabel, spacer, logoutBtn);
        mainLayout.setTop(header);

        // TABS
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        Tab posTab = new Tab("Marcare Comandă", createPosView());
        Tab historyTab = new Tab("Istoricul Meu", createHistoryView());
        tabPane.getTabs().addAll(posTab, historyTab);

        mainLayout.setCenter(tabPane);

        Scene scene = new Scene(mainLayout, 1200, 700);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }


    private BorderPane createPosView() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // STANGA: MESE
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ccc;");
        leftPanel.setPrefWidth(200);

        Label tablesTitle = new Label("1. Selectează Masa");
        tablesTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        selectedTableLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: red; -fx-font-size: 14px;");

        FlowPane tablesGrid = new FlowPane(10, 10);
        for (int i = 1; i <= 9; i++) {
            Button tableBtn = new Button("M" + i);
            tableBtn.setPrefSize(50, 50);
            tableBtn.setStyle("-fx-background-color: #90caf9;");
            int tableNum = i;
            tableBtn.setOnAction(e -> {
                controller.setTable(tableNum);
                selectedTableLabel.setText("MASA: " + tableNum);
                selectedTableLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-font-size: 14px;");
            });
            tablesGrid.getChildren().add(tableBtn);
        }
        leftPanel.getChildren().addAll(tablesTitle, tablesGrid, new Separator(), selectedTableLabel);
        root.setLeft(leftPanel);

        // CENTRU: MENIU
        VBox centerPanel = new VBox(10);
        centerPanel.setPadding(new Insets(10));
        Label menuTitle = new Label("2. Adaugă Produse");
        menuTitle.setStyle("-fx-font-weight: bold;");

        TableColumn<Product, String> nameCol = new TableColumn<>("Produs");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Product, Double> priceCol = new TableColumn<>("Preț");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setStyle("-fx-alignment: CENTER-RIGHT;");

        menuTable.getColumns().clear();
        menuTable.getColumns().addAll(nameCol, priceCol);
        menuTable.setItems(FXCollections.observableArrayList(controller.getMenu()));

        // Listener pentru detalii read-only (Sus Dreapta
        menuTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                detailName.setText(newVal.getName());
                if (newVal instanceof Food) detailInfo.setText(((Food)newVal).getMeasureValue() + "g");
                else if (newVal instanceof Drinks) detailInfo.setText(((Drinks)newVal).getMeasureValue() + "ml");
            }
        });

        Button addButton = new Button("Adaugă în Coș >>");
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        addButton.setMaxWidth(Double.MAX_VALUE);

        // Validare MASA
        addButton.setOnAction(e -> {
            if (controller.getCurrentOrder().getTableNumber() == 0) {
                new Alert(Alert.AlertType.WARNING, "TREBUIE SĂ SELECTAȚI O MASĂ ÎNTÂI!").show();
                return;
            }
            Product selected = menuTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                controller.addToOrder(selected, 1);
                updateReceipt();
            }
        });

        centerPanel.getChildren().addAll(menuTitle, menuTable, addButton);
        root.setCenter(centerPanel);

        // 3 DREAPTA: SPLIT PANE
        SplitPane rightSplit = new SplitPane();
        rightSplit.setOrientation(Orientation.VERTICAL);
        rightSplit.setPrefWidth(320);

        // 3A. SUS: Detalii Produs Selectat
        VBox detailsBox = new VBox(10);
        detailsBox.setPadding(new Insets(10));
        detailsBox.setAlignment(Pos.CENTER);
        detailsBox.setStyle("-fx-background-color: #e3f2fd;");
        Label detTitle = new Label("Detalii (Read-Only)");
        detTitle.setStyle("-fx-font-style: italic;");
        detailName.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        detailsBox.getChildren().addAll(detTitle, detailName, detailInfo);

        // 3B. JOS: Coșul de Cumpărături + Controale Editare
        VBox cartBox = new VBox(10);
        cartBox.setPadding(new Insets(10));
        cartBox.setStyle("-fx-background-color: #fff8dc;");

        Label cartTitle = new Label("3. Comanda Curentă");
        cartTitle.setStyle("-fx-font-weight: bold;");

        // Controale Editare Cantitate
        HBox editControls = new HBox(5);
        editControls.setAlignment(Pos.CENTER);
        Button minusBtn = new Button("[-]");
        Button plusBtn = new Button("[+]");
        Button delBtn = new Button("Șterge");
        delBtn.setStyle("-fx-background-color: #ef5350; -fx-text-fill: white;");

        // Logică butoane editare
        Runnable editAction = () -> {
            int idx = receiptList.getSelectionModel().getSelectedIndex();
            List<OrderItem> items = controller.getCurrentOrder().getItems();
            // Verificăm dacă indexul e în zona produselor reale (primele N rânduri)
            if (idx >= 0 && idx < items.size()) {
                OrderItem item = items.get(idx);
                // Determinăm acțiunea pe baza butonului apăsat (tratat jos în setOnAction)
            } else {
                new Alert(Alert.AlertType.WARNING, "Selectați un produs real (nu reducere)!").show();
            }
        };

        minusBtn.setOnAction(e -> handleEdit(-1));
        plusBtn.setOnAction(e -> handleEdit(1));
        delBtn.setOnAction(e -> handleEdit(-999)); // Cod pt ștergere totală

        editControls.getChildren().addAll(minusBtn, plusBtn, delBtn);

        Button submitBtn = new Button("✅ Finalizează Comanda");
        submitBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        submitBtn.setMaxWidth(Double.MAX_VALUE);
        submitBtn.setOnAction(e -> {
            if (controller.getCurrentOrder().getTableNumber() == 0 || controller.getCurrentOrder().getItems().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Comandă invalidă! (Masă lipsă sau coș gol)").show();
                return;
            }
            try {
                controller.saveOrder();
                new Alert(Alert.AlertType.INFORMATION, "Comandă Salvată! Masa eliberată.").show();
                controller.startNewOrder();
                updateReceipt();
                selectedTableLabel.setText("MASA: NESELECTATĂ");
                selectedTableLabel.setStyle("-fx-text-fill: black;");
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        cartBox.getChildren().addAll(cartTitle, receiptList, editControls, new Separator(), totalLabel, submitBtn);

        rightSplit.getItems().addAll(detailsBox, cartBox);
        rightSplit.setDividerPositions(0.3); // 30% sus, 70% jos
        root.setRight(rightSplit);

        return root;
    }

    private void handleEdit(int change) {
        int idx = receiptList.getSelectionModel().getSelectedIndex();
        List<OrderItem> items = controller.getCurrentOrder().getItems();

        if (idx >= 0 && idx < items.size()) {
            OrderItem item = items.get(idx);
            if (change == -999) {
                controller.removeFromOrder(item);
            } else {
                controller.updateItemQuantity(item, change);
            }
            updateReceipt();
            // reselectăm indexul pentru UX
            if (!items.isEmpty()) receiptList.getSelectionModel().select(Math.min(idx, items.size()-1));
        }
    }

    private VBox createHistoryView() {
        VBox root = new VBox(15); root.setPadding(new Insets(20));
        TableView<Order> table = new TableView<>();

        TableColumn<Order, Long> idCol = new TableColumn<>("ID"); idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Order, Double> totCol = new TableColumn<>("Total"); totCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        TableColumn<Order, LocalDateTime> dateCol = new TableColumn<>("Data"); dateCol.setCellValueFactory(new PropertyValueFactory<>("orderDate"));

        table.getColumns().addAll(idCol, totCol, dateCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button refBtn = new Button("Reîmprospătează");
        refBtn.setOnAction(e -> table.setItems(FXCollections.observableArrayList(controller.getMyOrderHistory())));

        try { table.setItems(FXCollections.observableArrayList(controller.getMyOrderHistory())); } catch (Exception e) {}

        root.getChildren().addAll(new Label("Istoricul Comenzilor Mele"), refBtn, table);
        return root;
    }

    private void updateReceipt() {
        receiptList.getItems().clear();
        List<OrderItem> displayItems = controller.getOrderItemsWithDiscounts();

        for (OrderItem item : displayItems) {
            String line;
            if (item.getProduct().getPrice() < 0) {
                // E REDUCERE
                line = String.format("   🎉 %s: %.2f RON", item.getProduct().getName(), item.getProduct().getPrice());
            } else {
                // E PRODUS NORMAL
                line = String.format("%dx %s  =  %.2f RON", item.getQuantity(), item.getProduct().getName(), item.getSubtotal());
            }
            receiptList.getItems().add(line);
        }
        totalLabel.setText(String.format("Total: %.2f RON", controller.calculateFinalTotal()));
    }
}