package org.example.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.Optional;

@Entity
@DiscriminatorValue("BAUTURA")
public class Drinks extends Product {

    private double milliliters;
    private String alcoholContent;

    public Drinks() {
    }

    public Drinks(String name, double price, double milliliters, String alcoholContent) {
        super(name, price);
        this.milliliters = milliliters;
        this.alcoholContent = alcoholContent;
    }

    public Optional<String> getAlcohol() {
        return Optional.ofNullable(alcoholContent).filter(s -> !s.isEmpty());
    }

    @Override
    public String getUnitSymbol() { return " ml"; }

    @Override
    public double getMeasureValue() { return milliliters; }

    @Override
    public void displayDetails() {
        System.out.println("Bautura: " + name + " - " + price + " RON");
    }
}