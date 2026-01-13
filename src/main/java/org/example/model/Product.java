package org.example.model;

import jakarta.persistence.*;
import java.io.Serializable;

//ytansf din interface în clasa abstracta
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tip_produs", discriminatorType = DiscriminatorType.STRING)
public abstract class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id; // pk

    protected String name;
    protected double price;

    public Product() {
    }

    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }


    public Long getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public void setPrice(double price) {
        this.price = price;
    }
    public abstract String getUnitSymbol();
    public abstract double getMeasureValue();
    public abstract void displayDetails();
}