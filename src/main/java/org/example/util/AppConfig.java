package org.example.util;

public record AppConfig(double tvaRate, String restaurantName) {

    public AppConfig {
        if (tvaRate <= 0 || tvaRate >= 1) {
            throw new IllegalArgumentException("Rata TVA citita (" + tvaRate + ") trebuie sa fie intre 0 si 1.");
        }
    }
}