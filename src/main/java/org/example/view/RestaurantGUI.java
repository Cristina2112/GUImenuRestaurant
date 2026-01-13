package org.example.view;

import com.google.gson.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.model.Drinks;
import org.example.model.Food;
import org.example.model.Product;
import org.example.repository.ProductRepository;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.util.List;

public class RestaurantGUI extends Application {

    // repository pentru a comunica cu PostgreSQL
    private ProductRepository repository = new ProductRepository();

    // lista vizuală și datele din ea
    private ListView<Product> productListView;
    private ObservableList<Product> productsObservable;

    // elemente formular (partea centrală)
    private TextField nameField = new TextField();
    private TextField priceField = new TextField();
    private TextField categoryField = new TextField();
    private TextField measureField = new TextField();

    // bara de status de jos
    private Label statusLabel = new Label("Sistem conectat la baza de date.");

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Restaurant La Andrei - GUI & Database");
        BorderPane root = new BorderPane();

        // MENU BAR (Import/Export JSON)
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");

        MenuItem exportItem = new MenuItem("Export JSON");
        exportItem.setOnAction(e -> exportToJson(primaryStage));

        MenuItem importItem = new MenuItem("Import JSON");
        importItem.setOnAction(e -> importFromJson(primaryStage));

        fileMenu.getItems().addAll(exportItem, importItem);
        menuBar.getMenus().add(fileMenu);

        // adaugăm meniul în partea de sus
        VBox topContainer = new VBox(menuBar);
        root.setTop(topContainer);

        // LISTA (Partea Stângă)
        // incărcăm datele din DB la pornire
        List<Product> dbProducts = repository.getAllProducts();
        productsObservable = FXCollections.observableArrayList(dbProducts);

        productListView = new ListView<>(productsObservable);
        productListView.setPrefWidth(250);


        productListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + item.getPrice() + " RON)");
                }
            }
        });

        // cand dai click pe un produs, completăm formularul
        productListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) updateFields(newVal);
        });

        VBox leftBox = new VBox(10, new Label("Meniu (din DB):"), productListView);
        leftBox.setPadding(new Insets(10));
        root.setLeft(leftBox);

        // FORMULAR DETALII (Centru)
        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(10);
        detailsGrid.setVgap(10);
        detailsGrid.setPadding(new Insets(20));

        detailsGrid.add(new Label("Nume:"), 0, 0);
        detailsGrid.add(nameField, 1, 0);
        nameField.setEditable(false); // Read-only

        detailsGrid.add(new Label("Preț (RON):"), 0, 1);
        detailsGrid.add(priceField, 1, 1);
        // Prețul este EDITABIL

        detailsGrid.add(new Label("Categorie:"), 0, 2);
        detailsGrid.add(categoryField, 1, 2);
        categoryField.setEditable(false);

        detailsGrid.add(new Label("Gramaj/Volum:"), 0, 3);
        detailsGrid.add(measureField, 1, 3);
        measureField.setEditable(false);

        root.setCenter(detailsGrid);

        // Status Bar jos
        root.setBottom(statusLabel);
        BorderPane.setMargin(statusLabel, new Insets(10));

        //salvare si reactivitate
        // validare vizuală (se face verde/roșu în timp ce scrii)
        priceField.textProperty().addListener((obs, oldVal, newVal) -> {
            Product selected = productListView.getSelectionModel().getSelectedItem();
            if (selected != null && priceField.isFocused()) {
                try {
                    Double.parseDouble(newVal);
                    // E valid, facem verde
                    priceField.setStyle("-fx-text-fill: green;");
                    statusLabel.setText("Editare preț: Apăsați ENTER pentru a salva.");
                } catch (NumberFormatException e) {
                    // E invalid, facem roșu
                    priceField.setStyle("-fx-text-fill: red;");
                }
            }
        });

        // salvare la enter
        priceField.setOnAction(event -> {
            Product selectedProduct = productListView.getSelectionModel().getSelectedItem();

            if (selectedProduct != null) {
                try {
                    //luam prețul nou din căsuță
                    double newPrice = Double.parseDouble(priceField.getText());

                    // actualizam obiectul din memorie
                    selectedProduct.setPrice(newPrice);

                    // salvam in bd
                    repository.updateProduct(selectedProduct);

                    // refresh lista vizuală ca să vedem prețul nou și în stânga
                    productListView.refresh();

                    statusLabel.setText("✅ Preț actualizat cu succes: " + newPrice + " RON");
                    priceField.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");

                    root.requestFocus();

                } catch (NumberFormatException e) {
                    statusLabel.setText("Eroare: Introduceți un număr valid (ex: 15.5)");
                }
            }
        });

        Scene scene = new Scene(root, 700, 450);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // completează câmpurile din dreapta
    private void updateFields(Product p) {
        // resetăm stilul
        priceField.setStyle("-fx-text-fill: black;");

        nameField.setText(p.getName());
        priceField.setText(String.valueOf(p.getPrice()));
        measureField.setText(p.getMeasureValue() + p.getUnitSymbol());

        if (p instanceof Food) categoryField.setText("Mâncare");
        else if (p instanceof Drinks) categoryField.setText("Băutură");
        else categoryField.setText("Specialitate");
    }

    // export JSON
    private void exportToJson(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvează Meniul JSON");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (Writer writer = Files.newBufferedWriter(file.toPath())) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(productsObservable, writer);

                statusLabel.setText("Export reușit în: " + file.getName());
                new Alert(Alert.AlertType.INFORMATION, "Export reușit!").show();
            } catch (IOException ex) {
                statusLabel.setText("Eroare la export!");
                ex.printStackTrace();
            }
        }
    }

    // import JSON (evita duplicatele)
    private void importFromJson(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importă Meniu JSON");
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try (Reader reader = Files.newBufferedReader(file.toPath())) {
                JsonElement jsonElement = JsonParser.parseReader(reader);

                if (!jsonElement.isJsonArray()) {
                    new Alert(Alert.AlertType.ERROR, "Fișierul nu conține o listă de produse!").show();
                    return;
                }

                JsonArray jsonArray = jsonElement.getAsJsonArray();
                int addedCount = 0;
                int skippedCount = 0;
                Gson gson = new Gson();

                for (JsonElement element : jsonArray) {
                    JsonObject obj = element.getAsJsonObject();
                    Product newProduct = null;

                    // detectam titlu
                    if (obj.has("grams")) {
                        newProduct = gson.fromJson(obj, Food.class);
                    } else if (obj.has("milliliters")) {
                        newProduct = gson.fromJson(obj, Drinks.class);
                    }

                    // verificăm dacă există deja în DB (după nume)
                    if (newProduct != null) {
                        // verificăm în lista locală (care e oglinda DB-ului) dacă avem deja numele
                        boolean exists = false;
                        for (Product existing : productsObservable) {
                            if (existing.getName().equalsIgnoreCase(newProduct.getName())) {
                                exists = true;
                                break;
                            }
                        }

                        if (!exists) {
                            repository.addProduct(newProduct);
                            addedCount++;
                        } else {
                            skippedCount++;
                        }
                    }
                }

                // refresh lista
                productsObservable.setAll(repository.getAllProducts());

                String message = "S-au importat " + addedCount + " produse noi.";
                if (skippedCount > 0) {
                    message += "\n(" + skippedCount + " produse existau deja și au fost ignorate)";
                }

                statusLabel.setText(message);
                new Alert(Alert.AlertType.INFORMATION, message).show();

            } catch (Exception ex) {
                statusLabel.setText("Eroare la import!");
                ex.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "JSON invalid sau format greșit.").show();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}