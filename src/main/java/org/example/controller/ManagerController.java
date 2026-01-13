package org.example.controller;

import org.example.model.Order;
import org.example.model.Product;
import org.example.model.Role;
import org.example.model.User;
import org.example.repository.OrderRepository;
import org.example.repository.ProductRepository;
import org.example.repository.UserRepository;
import org.example.util.MenuCatalog;

import java.io.File;
import java.io.IOException;
import java.util.List;

//importuri pt concurenta
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javafx.concurrent.Task;


public class ManagerController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    // pt gestionarea thread-urilor de fundal
    private final ExecutorService executorService;

    public ManagerController() {
        this.userRepository = new UserRepository();
        this.productRepository = new ProductRepository();
        this.orderRepository = new OrderRepository();

        this.executorService = Executors.newCachedThreadPool();
    }

    // gestiune angajati
    public List<User> getAllStaffUsers() {
        return userRepository.getAllStaff();
    }

    public void addUser(String username, String password, Role role) {
        User newUser = new User(username, password, role);
        userRepository.addUser(newUser);
    }

    public void deleteUser(Long id) {
        userRepository.deleteUser(id);
    }

    // gestiune meniu
    public List<Product> getAllProducts() {
        return productRepository.getAllProducts();
    }

    public void addProduct(Product product) {
        productRepository.addProduct(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteProduct(id);
    }

    // istoric comenzi
    public List<Order> getAllOrders() {
        return orderRepository.getAllOrders();
    }

    // metoda pt iteratia 8
    public void getAllOrdersAsync(Consumer<List<Order>> onSuccess, Runnable onLoading) {
        // 1. Creăm un Task JavaFX
        Task<List<Order>> task = new Task<>() {
            @Override
            protected List<Order> call() throws Exception {
                // această linie rulează pe un thread secundar
                // aici se va executa și Thread.sleep(2000) din Repository fără să blocheze UI-ul
                return orderRepository.getAllOrders();
            }
        };

        // taskul reuseste
        task.setOnSucceeded(event -> {
            List<Order> result = task.getValue();
            onSuccess.accept(result); // trimitem datele înapoi în View
        });

        // taskul eșueaza
        task.setOnFailed(event -> {
            Throwable error = task.getException();
            error.printStackTrace(); //afisam eroare
        });

        // stare incarcare
        onLoading.run();

        // trm task in executie
        executorService.submit(task);
    }

    // gestiune oferte
    public void setHappyHour(boolean active) {
        OfferService.HAPPY_HOUR_ACTIVE = active;
    }

    public void setMealDeal(boolean active) {
        OfferService.MEAL_DEAL_ACTIVE = active;
    }

    public void setPartyPack(boolean active) {
        OfferService.PARTY_PACK_ACTIVE = active;
    }

    public boolean isHappyHourActive() { return OfferService.HAPPY_HOUR_ACTIVE; }
    public boolean isMealDealActive() { return OfferService.MEAL_DEAL_ACTIVE; }
    public boolean isPartyPackActive() { return OfferService.PARTY_PACK_ACTIVE; }

    // export json
    public void exportMenu(String restaurantName) {
        try {
            MenuCatalog catalog = new MenuCatalog();
            catalog.exportMenuToJson(restaurantName);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Eroare la export: " + e.getMessage());
        }
    }

    // import json
    public void importMenu(File file) {
        try {
            MenuCatalog catalog = new MenuCatalog();

            // citim prod din fisier
            List<Product> importedProducts = catalog.readProductsFromJson(file);

            int addedCount = 0;

            // le salvam în DB pe rand
            for (Product p : importedProducts) {
                productRepository.addProduct(p);
                addedCount++;
            }

            System.out.println("S-au importat " + addedCount + " produse.");

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Eroare la citirea fișierului: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Eroare la import (format invalid?): " + e.getMessage());
        }
    }

    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown(); // Oprește thread-urile background la închiderea aplicației
        }
    }
}