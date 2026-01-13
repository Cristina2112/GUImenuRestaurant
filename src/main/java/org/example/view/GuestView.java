package org.example.view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.controller.GuestController;
import org.example.model.Drinks;
import org.example.model.Food;
import org.example.model.Product;

import java.util.List;

public class GuestView {

    private final GuestController controller = new GuestController();

    // elemente UI pentru filtrare
    private TextField searchField;
    private ComboBox<String> typeCombo;
    private CheckBox vegCheck;
    private TextField minPriceField;
    private TextField maxPriceField;

    private TableView<Product> table = new TableView<>();

    // elemente pentru Panoul de Detalii (Dreapta)
    private Label detailsName = new Label("Selectează un produs");
    private Label detailsPrice = new Label("-");
    private Label detailsType = new Label("-");
    private Label detailsExtra = new Label("-"); // Gramaj sau Alcool

    public void start(Stage stage) {
        stage.setTitle("Meniu Client - Restaurant La Andrei");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f4f4f4;");

        // ZONA DE SUS (FILTRE + EXIT)
        root.setTop(createTopBar(stage));

        // CENTRU (TABEL PRODUSE)
        TableColumn<Product, String> nameCol = new TableColumn<>("Nume Produs");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(300);

        TableColumn<Product, Double> priceCol = new TableColumn<>("Preț");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setStyle("-fx-alignment: CENTER-RIGHT;");

        table.getColumns().addAll(nameCol, priceCol);
        table.setPlaceholder(new Label("Niciun produs găsit conform filtrelor."));

        // Listener pentru selecție
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) showDetails(newVal);
        });

        root.setCenter(table);

        // DREAPTA (PANOU DETALII)
        root.setRight(createDetailsPanel());

        // incărcare inițială
        refreshTable();

        Scene scene = new Scene(root, 1100, 650);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    private HBox createTopBar(Stage stage) {
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0, 0, 15, 0));
        topBar.setStyle("-fx-border-color: #cccccc; -fx-border-width: 0 0 1 0;");

        // A. Căutare
        Label searchLbl = new Label("🔍 Caută:");
        searchField = new TextField();
        searchField.setPromptText("ex: Pizza...");
        searchField.textProperty().addListener(e -> refreshTable());

        // B. Tip
        Label typeLbl = new Label("Tip:");
        typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Toate", "Mâncare", "Băutură");
        typeCombo.setValue("Toate");
        typeCombo.setOnAction(e -> refreshTable());

        // C. Interval Preț
        Label priceLbl = new Label("Preț (Min-Max):");
        minPriceField = new TextField();
        minPriceField.setPromptText("0");
        minPriceField.setPrefWidth(60);

        maxPriceField = new TextField();
        maxPriceField.setPromptText("Max");
        maxPriceField.setPrefWidth(60);

        minPriceField.textProperty().addListener(e -> refreshTable());
        maxPriceField.textProperty().addListener(e -> refreshTable());

        // D. Vegetarian
        vegCheck = new CheckBox("🌱 Doar Vegetarian");
        vegCheck.setOnAction(e -> refreshTable());

        // E. Exit
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button exitBtn = new Button("Ieșire (Login) 🚪");
        exitBtn.setStyle("-fx-background-color: #757575; -fx-text-fill: white; -fx-cursor: hand;");
        exitBtn.setOnAction(e -> {
            stage.close();
            new LoginView().start(new Stage());
        });

        topBar.getChildren().addAll(
                searchLbl, searchField,
                typeLbl, typeCombo,
                priceLbl, minPriceField, new Label("-"), maxPriceField,
                vegCheck,
                spacer, exitBtn
        );
        return topBar;
    }

    private VBox createDetailsPanel() {
        VBox detailsBox = new VBox(20);
        detailsBox.setPadding(new Insets(20));
        detailsBox.setPrefWidth(300);
        detailsBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dee2e6; -fx-border-width: 0 0 0 1;");
        detailsBox.setAlignment(Pos.TOP_CENTER);

        Label headerDetails = new Label("Detalii Produs");
        headerDetails.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: #343a40;");

        detailsName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-alignment: center;");
        detailsName.setWrapText(true);

        detailsPrice.setStyle("-fx-font-size: 16px; -fx-text-fill: #28a745; -fx-font-weight: bold;");
        detailsType.setStyle("-fx-font-style: italic; -fx-text-fill: #6c757d;");

        detailsBox.getChildren().addAll(headerDetails, new Separator(), detailsName, detailsType, detailsExtra, detailsPrice);
        return detailsBox;
    }

    private void showDetails(Product p) {
        detailsName.setText(p.getName());
        detailsPrice.setText(String.format("%.2f RON", p.getPrice()));

        if (p instanceof Food) {
            detailsType.setText("Categorie: Mâncare");
            detailsExtra.setText("Gramaj: " + p.getMeasureValue() + "g");
        } else if (p instanceof Drinks) {
            detailsType.setText("Categorie: Băutură");
            Drinks d = (Drinks) p;
            String alc = d.getAlcohol().orElse("Fără alcool");
            detailsExtra.setText("Volum: " + d.getMeasureValue() + "ml\n" + alc);
        } else {
            detailsType.setText("-");
            detailsExtra.setText("-");
        }
    }

    private void refreshTable() {
        String searchText = searchField.getText();
        String type = typeCombo.getValue();
        boolean veg = vegCheck.isSelected();

        double minPrice = 0;
        double maxPrice = 0;
        try {
            if (!minPriceField.getText().isEmpty()) minPrice = Double.parseDouble(minPriceField.getText());
            if (!maxPriceField.getText().isEmpty()) maxPrice = Double.parseDouble(maxPriceField.getText());
        } catch (NumberFormatException e) {
            // ignorăm erorile de format
        }

        List<Product> filtered = controller.filterProducts(type, veg, minPrice, maxPrice, searchText);
        table.setItems(FXCollections.observableArrayList(filtered));
    }
}