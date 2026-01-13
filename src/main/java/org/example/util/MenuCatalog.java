package org.example.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.model.ProductCategory;
import org.example.model.Drinks;
import org.example.model.Food;
import org.example.model.Product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MenuCatalog {
    private final Map<String, Product> productCatalog = new TreeMap<>();

    // catalogul organizat pe categorii
    private final Map<ProductCategory, List<Product>> categorizedMenu;

    public MenuCatalog() {
        initializeMenu();

        // gruparea produselor pe categorii
        this.categorizedMenu = productCatalog.values().stream()
                .collect(Collectors.groupingBy(this::determineCategory, TreeMap::new, Collectors.toList()));
    }

    private void initializeMenu() {
        // produse vechi
        productCatalog.put("Pizza Margherita", new Food("Pizza Margherita", 45.0, 450));
        productCatalog.put("Paste Carbonara", new Food("Paste Carbonara", 52.5, 400));

        productCatalog.put("Limonada", new Drinks("Limonada", 15.0, 400, ""));
        productCatalog.put("Apa Plata", new Drinks("Apa Plata", 8.0, 500, null));
        productCatalog.put("Bere Corona", new Drinks("Bere Corona", 12.0, 330, "4.5% alc."));

        productCatalog.put("Desert Tiramisu", new Food("Desert Tiramisu", 35.0, 150));
        productCatalog.put("Vin Rosu", new Drinks("Vin Rosu", 25.0, 150, "13% alc."));
    }

    //determina categoria unui produs
    private ProductCategory determineCategory(Product p) {
        if (p instanceof Food) {
            if (p.getName().toLowerCase().contains("desert")) {
                return ProductCategory.DESSERT;
            }
            return ProductCategory.MAIN_COURSE;
        } else if (p instanceof Drinks) {
            Drinks drink = (Drinks) p;
            return drink.getAlcohol().isPresent() ?
                    ProductCategory.ALCOHOLIC_DRINK :
                    ProductCategory.NON_ALCOHOLIC_DRINK;
        }
        return ProductCategory.OTHER;
    }

    //meniu intial - iteratia 1
    public Collection<Product> getProductCatalogValues() {
        return productCatalog.values();
    }

    //meniul organizat pe categorii
    public Map<ProductCategory, List<Product>> getCategorizedMenu() {
        return categorizedMenu;
    }

    //cautare sigura cu optional
    public Optional<Product> findProductByName(String name) {
        return Optional.ofNullable(productCatalog.get(name));
    }

    //INTEROGARI
    public List<Product> getVegetarianProducts() {
        return productCatalog.values().stream()
                // Simulare de filtrare vegetariană
                .filter(p -> p instanceof Food && !p.getName().toLowerCase().contains("carbonara"))
                .sorted(Comparator.comparing(Product::getName)) // Sortare alfabetică
                .collect(Collectors.toList());
    }

    public OptionalDouble getAverageDessertPrice() {
        return categorizedMenu.getOrDefault(ProductCategory.DESSERT, List.of()).stream()
                .mapToDouble(Product::getPrice)
                .average();
    }

    public List<Product> getProductsCostingMoreThan(double maxPrice) {
        return productCatalog.values().stream()
                .filter(p -> p.getPrice() > maxPrice)
                .collect(Collectors.toList());
    }


    //returnează toate produsele dintr-o anumită categorie.
    public List<Product> getProductsByCategory(ProductCategory category) {
        return categorizedMenu.getOrDefault(category, List.of());
    }


    private static class ProductDTO {
        String name;
        double price;
        String type;
        String unit;
        double measure;
        String extra;

        ProductDTO(Product p) {
            this.name = p.getName();
            this.price = p.getPrice();
            this.unit = p.getUnitSymbol();
            this.measure = p.getMeasureValue();

            if (p instanceof Food) {
                this.type = "Food";
                this.extra = null;
            } else if (p instanceof Drinks) {
                Drinks d = (Drinks) p;
                this.type = "Drinks";
                this.extra = d.getAlcohol().orElse(null);
            } else {
                this.type = "Other";
                this.extra = null;
            }
        }
    }


    public void exportMenuToJson(String restaurantName) throws IOException {
        String baseName = restaurantName.replace(" ", "");
        String fileName = "menu_export_" + baseName + ".json";

        // convertim produsele la obiecte DTO care sunt serializabile
        List<ProductDTO> dtoList = productCatalog.values().stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());

        // gsonBuilder pentru 'pretty printing' (formatare lizibilă)
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // serializare lista DTO
        String jsonContent = gson.toJson(dtoList);

        Path filePath = Path.of(fileName);
        Files.writeString(filePath, jsonContent);
    }

    public List<Product> readProductsFromJson(java.io.File file) throws IOException {
        Gson gson = new Gson();
        String jsonContent = Files.readString(file.toPath());

        // deserializăm în vector de DTO-uri
        ProductDTO[] dtos = gson.fromJson(jsonContent, ProductDTO[].class);

        List<Product> resultList = new java.util.ArrayList<>();

        if (dtos != null) {
            for (ProductDTO dto : dtos) {
                Product product = null;

                // convertim DTO -> Entity (Food sau Drinks)
                if ("Food".equalsIgnoreCase(dto.type)) {
                    // Food(String name, double price, double grams)
                    product = new Food(dto.name, dto.price, dto.measure);
                } else if ("Drinks".equalsIgnoreCase(dto.type)) {
                    // Drinks(String name, double price, double milliliters, String alcoholContent)
                    product = new Drinks(dto.name, dto.price, dto.measure, dto.extra);
                } else {
                    // Fallback pentru tipuri necunoscute (le tratăm ca Food generic sau ignorăm)
                    product = new Food(dto.name, dto.price, dto.measure);
                }

                resultList.add(product);
            }
        }
        return resultList;
    }
}