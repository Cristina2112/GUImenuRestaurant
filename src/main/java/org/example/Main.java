package org.example;

import javafx.application.Application;
import org.example.model.*;
import org.example.repository.ProductRepository;
import org.example.util.*;


import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   SISTEM DE GESTIUNE RESTAURANT");
        System.out.println("==========================================");
        System.out.println("Selectează modul de rulare:");
        System.out.println("1. Interfață Grafică (Recomandat: Login View)");
        System.out.println("2. Mod Consolă (Testare Iterațiile 1-4 + DB)");
        System.out.print("Opțiunea ta (1 sau 2): ");

        Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            System.out.println("Se pornește interfața grafică...");


            Application.launch(AppLauncher.class, args);
        } else {
            System.out.println("Se pornește modul consolă...\n");
            runConsoleMode();
        }
    }

    private static void runConsoleMode() {
        // configurare Externă și Gestionare Erori (Iterația 4)
        AppConfig config;
        try {
            // se încarcă configurația din "config.json". Poate arunca ConfigException
            config = ConfigLoader.loadConfig();

            PriceCalculator.setTvaRate(config.tvaRate());

        } catch (ConfigException e) {
            // tratare erori
            System.err.println("\n\n################################################################");
            System.err.println("EROARE CRITICĂ DE CONFIGURARE: Aplicatia se va închide controlat.");
            System.err.println(e.getMessage());
            System.err.println("################################################################\n");
            return;
        }

        // inițializăm noul catalog de produse.
        MenuCatalog menuCatalog = new MenuCatalog();

        System.out.println("--- Iteraria 1+2--- ");
        System.out.println("--- Meniul Restaurantului \"" + config.restaurantName() + "\" ---");


        Collection<Product> menuValues = menuCatalog.getProductCatalogValues();

        for (Product product : menuValues) {
            System.out.println(product.getName() + " - " + product.getPrice());
        }
        System.out.println("----------------------------------------");

        Order order = new Order();

        // Preluarea elementelor din catalogul nou.
        // Order-ul nou cere un obiect OrderItem, nu (Produs, cantitate) direct
        menuCatalog.findProductByName("Pizza Margherita")
                .ifPresent(p -> order.addItem(new OrderItem(p, 1)));

        menuCatalog.findProductByName("Paste Carbonara")
                .ifPresent(p -> order.addItem(new OrderItem(p, 2)));

        menuCatalog.findProductByName("Bere Corona")
                .ifPresent(p -> order.addItem(new OrderItem(p, 3)));

        menuCatalog.findProductByName("Apa Plata")
                .ifPresent(p -> order.addItem(new OrderItem(p, 1)));


        /*
        // Definirea regulii de discount folosind o expresie lambda
        DiscountRule happyHourDiscount = (Order currentOrder) -> {
            final double DISCOUNT_PERCENTAGE = 0.20; // 20% reducere
            double drinksSubtotal = 0;

            for (OrderItem item : currentOrder.getItems()) {
                if (item.getProduct() instanceof Drinks) {
                    drinksSubtotal += item.getSubtotal();
                }
            }
            return drinksSubtotal * DISCOUNT_PERCENTAGE;
        };

        //10% reducere la totalul comenzii
        DiscountRule newPromotion = (Order currentOrder) -> {
            final double FLAT_DISCOUNT = 0.10; // 10%
            double subtotal = currentOrder.getSubtotalBeforeDiscount();

            // Regula calculeaza 10% din totalul inainte de TVA
            return subtotal * FLAT_DISCOUNT;
        };

        order.setDiscountRule(happyHourDiscount);

        double subtotal = order.getSubtotalBeforeDiscount();
        double discount = order.calculateTotalDiscount();
        double finalTotal = order.calculateFinalTotal();
        */

        //calcul
        double subtotal = order.getSubtotal();
        // Reducerile se aplică acum prin Controller, nu aici.

        System.out.println("\n--- DETALII COMANDĂ ---");
        for (OrderItem item : order.getItems()) {
            System.out.printf("%d x %s (%.2f RON/buc) = %.2f RON\n",
                    item.getQuantity(), item.getProduct().getName(),
                    item.getProduct().getPrice(), item.getSubtotal());
        }
        System.out.printf("\n");

        //variabilele vechi care nu mai există
        System.out.printf("Subtotal (fără TVA și Discount): %.2f RON\n", subtotal);
        // System.out.printf("Discount aplicat (Happy Hour -20%% la băuturi): -%.2f RON\n", discount);

        // double priceAfterDiscount = subtotal - discount;
        // double totalTVA = finalTotal - priceAfterDiscount;

        // System.out.printf("Total după Discount:             %.2f RON\n", priceAfterDiscount);

        //tva formatat -> 9%. Rata TVA este preluată din PriceCalculator
        // System.out.printf("TVA aplicat (%.0f%%):                 %.2f RON\n", PriceCalculator.getTvaRate() * 100, totalTVA);
        // System.out.printf("TOTAL DE PLATĂ FINAL:            %.2f RON\n", finalTotal);
        System.out.println("----------------------------------------");



        // APELAREA METODELOR PENTRU ITERAȚIA 3
        System.out.println("\n\n");
        System.out.println("--- ITERAȚIA 3 ---");

        // 1. Organizarea Meniului pe Categorii
        System.out.println("\n--- 1. MENIU PE CATEGORII ---");
        // Această logică depindea de MenuCatalog vechi, posibil să necesite ajustări
        // Am lăsat-o activă, dar dacă MenuCatalog nu mai are getCategorizedMenu, va da eroare.
        // Momentan pare OK în util.MenuCatalog.
        for (Map.Entry<ProductCategory, List<Product>> entry : menuCatalog.getCategorizedMenu().entrySet()) {
            System.out.println("\n## " + entry.getKey().getDisplayName() + ":");
            for (Product product : entry.getValue()) {
                System.out.println(product.getName());
            }
        }

        // 2. Interogări Complexe
        System.out.println("\n--- 2. INTEROGĂRI ---");

        // 1. Produse Vegetale (sortate alfabetic)
        List<Product> vegetarianProducts = menuCatalog.getVegetarianProducts();
        System.out.println("\n1. Produse Vegetale (sortate alfabetic):");
        vegetarianProducts.forEach(p -> System.out.println("-> " + p.getName()));

        // 2. Prețul Mediu al Deserturilor
        OptionalDouble avgDessertPrice = menuCatalog.getAverageDessertPrice();
        if (avgDessertPrice.isPresent()) {
            System.out.printf("\n2. Prețul mediu al deserturilor: %.2f RON\n", avgDessertPrice.getAsDouble());
        } else {
            System.out.println("\n2. Nu există deserturi definite în meniu.");
        }

        // 3. Preparat mai scump de 100 RON
        List<Product> expensiveProducts = menuCatalog.getProductsCostingMoreThan(100.0);
        System.out.print("\n3. Preparate care costă mai mult de 100 RON: ");
        if (expensiveProducts.isEmpty()) {
            System.out.println("Niciunul.");
        } else {
            expensiveProducts.forEach(p -> System.out.println(p.getName() + " (%.2f RON)".formatted(p.getPrice())));
        }

        // 4. Produse dintr-o Anumită Categorie
        System.out.println("\n4. Produse dintr-o anumită categorie:");

        // afisare produsen dupa categorii -desertuti
        List<Product> desserts = menuCatalog.getProductsByCategory(ProductCategory.DESSERT);
        System.out.println("\n   -> Desserturi:");
        if (desserts.isEmpty()) {
            System.out.println("      Niciun desert disponibil.");
        } else {
            desserts.forEach(p -> System.out.println("      " + p.getName() + " - " + p.getPrice() + " RON"));
        }

        // 5. Căutare Sigură
        System.out.println("\n--- 3. CĂUTARE SIGURĂ  ---");

        // Căutare 1: Succes (Limonada)
        System.out.println("\nCăutare 'Limonada':");
        menuCatalog.findProductByName("Limonada")
                .ifPresentOrElse(
                        p -> System.out.println("-> Găsit: " + p.getName() + " | Pret: " + p.getPrice() + " RON"),
                        () -> System.out.println("-> Produsul nu există.")
                );

        // Căutare 2: Eșec (Produs Inexistent)
        System.out.println("\nCăutare 'Sos de Usturoi':");
        menuCatalog.findProductByName("Sos de Usturoi")
                .ifPresentOrElse(
                        p -> System.out.println("-> Găsit: " + p.getName() + " | Pret: " + p.getPrice() + " RON"),
                        () -> System.out.println("-> Produsul nu există.")
                );

        // 4.Pizza Custom
        System.out.println("\n--- 4. PIZZA CUSTOMIZABILĂ  ---");

        CustomPizza myPizza = new CustomPizza.Builder("Blat Subtire", "Sos Pesto")
                .withBasePrice(55.0)
                .withTopping("Roșii uscate", 4.0)
                .withTopping("Rucola", 2.0)
                .withTopping("Mozzarella ", 6.0)
                .withName("Pizza Vegetariană Deluxe")
                .build();

        System.out.println("Pizza Custom: " + myPizza.getName());
        System.out.printf("Pret final: %.2f RON\n", myPizza.getPrice());
        System.out.println("----------------------------------------");

        //export meniu
        System.out.println("\n--- 5. EXPORT MENIU JSON ---");
        try {
            // apelez metoda de export, folosind numele restaurantului din configurație
            menuCatalog.exportMenuToJson(config.restaurantName());
            System.out.println("Export reusit! Fisierul de meniu JSON a fost creat.");
        } catch (IOException e) {
            // Tratarea erorilor I/O la export
            System.err.println("Eroare la exportul meniului: Nu s-a putut scrie fisierul de export.");
            e.printStackTrace();
        }


        System.out.println("Se conectează la baza de date...");
        ProductRepository repo = new ProductRepository();

        // creăm câteva produse de test
        Product p1 = new Food("Pizza Quatro Formaggi", 45.0, 500);
        Product p2 = new Drinks("Cola Zero", 12.0, 330, null);
        Product p3 = new Drinks("Vin Rosu", 25.0, 750, "12%");

        // le salvăm în PostgreSQL
        // repo.addProduct(p1);
        // repo.addProduct(p2);
        // repo.addProduct(p3);
        System.out.println("Produse salvate");

        // le citim înapoi
        System.out.println("\n--- Produse din Baza de Date ---");
        var produse = repo.getAllProducts();
        produse.forEach(p -> System.out.println(p.getName() + " - " + p.getPrice()));
    }
}