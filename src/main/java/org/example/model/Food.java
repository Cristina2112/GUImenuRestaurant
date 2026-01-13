package org.example.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MANCARE")
public class Food extends Product {

    private double grams;

    public Food() {
    }

    public Food(String name, double price, double grams) {
        super(name, price);
        this.grams = grams;
    }

    @Override
    public String getUnitSymbol() { return " g"; }

    @Override
    public double getMeasureValue() { return grams; }

    @Override
    public void displayDetails() {
        System.out.println("Mancare: " + name + " - " + price + " RON");
    }
}