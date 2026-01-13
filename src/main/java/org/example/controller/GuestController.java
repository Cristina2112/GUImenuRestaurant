package org.example.controller;

import org.example.model.Drinks;
import org.example.model.Food;
import org.example.model.Product;
import org.example.repository.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GuestController {

    private final ProductRepository repository;
    private List<Product> allProducts; // evităm SQL la fiecare click

    public GuestController() {
        this.repository = new ProductRepository();
        loadData();
    }


    //incarcă datele din DB în memorie.
    public void loadData() {
        this.allProducts = repository.getAllProducts();
    }

    //logica de filtrare
    public List<Product> filterProducts(String type, boolean vegetarianOnly, double minPrice, double maxPrice, String searchText) {
        // Safety check: dacă baza de date e goală
        if (allProducts == null) return List.of();

        return allProducts.stream()
                // filtrare PREȚ
                .filter(p -> p.getPrice() >= minPrice)
                .filter(p -> maxPrice == 0 || p.getPrice() <= maxPrice)

                // filtrare TIP
                .filter(p -> {
                    if ("Toate".equals(type)) return true;
                    if ("Mâncare".equals(type)) return p instanceof Food;
                    if ("Băutură".equals(type)) return p instanceof Drinks;
                    return true;
                })

                // filtrare VEGETARIAN
                .filter(p -> !vegetarianOnly || isVegetarian(p))

                // caUTARE sigura optional
                .filter(p -> {
                    return Optional.ofNullable(searchText)
                            // ignora text gol
                            .filter(s -> !s.trim().isEmpty())
                            // trasnf in vool textul
                            .map(s -> p.getName().toLowerCase().contains(s.toLowerCase()))
                            .orElse(true);
                })
                .collect(Collectors.toList());
    }


    private boolean isVegetarian(Product p) {
        if (p instanceof Drinks) return true;

        if (p instanceof Food) {
            String name = p.getName().toLowerCase();
            return !name.contains("carne") &&
                    !name.contains("pui") &&
                    !name.contains("vita") &&
                    !name.contains("porc") &&
                    !name.contains("salam") &&
                    !name.contains("burger") &&
                    !name.contains("carbonara");
        }
        return false;
    }

    // cautare exactă folosind Optional
    public Optional<Product> findExactProduct(String name) {
        return allProducts.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst();
    }
}