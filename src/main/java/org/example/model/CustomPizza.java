package org.example.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

import java.util.ArrayList;
import java.util.List;


@Entity
@DiscriminatorValue("CUSTOM")
public class CustomPizza extends Product {

    //topping-urile sunt complexe pt DB simplu, le marcam @Transient (nu se salveaza in DB)
    //    pentru a evita erorile de Hibernate la rulare.
    @Transient
    private List<String> toppings;

    @Transient
    private String crust;

    @Transient
    private String sauce;

    public CustomPizza() {
    }

    // constructor privat folosit de Builder
    private CustomPizza(Builder builder) {
        // apelam constructorul parintelui (Product)
        super(builder.name, builder.price);
        this.toppings = builder.toppings;
        this.crust = builder.crust;
        this.sauce = builder.sauce;
    }


    @Override
    public String getUnitSymbol() {
        return " buc";
    }

    @Override
    public double getMeasureValue() {
        return 1.0;
    }

    @Override
    public void displayDetails() {
        String toppingsList = (toppings == null || toppings.isEmpty()) ?
                "Fără topping-uri" :
                String.join(", ", toppings);

        System.out.printf("> %s (%.2f RON) | Blat: %s, Sos: %s\n",
                getName(), getPrice(), crust, sauce);
        System.out.printf("  - Topping-uri: %s\n", toppingsList);
    }

    public static class Builder {
        private final String crust;
        private final String sauce;

        private String name = "Pizza Customizată";
        private double basePrice = 30.0;
        private List<String> toppings = new ArrayList<>();
        private double price = basePrice;

        public Builder(String crust, String sauce) {
            this.crust = crust;
            this.sauce = sauce;
            this.price = basePrice;
        }

        public Builder withTopping(String topping, double toppingPrice) {
            this.toppings.add(topping);
            this.price += toppingPrice;
            return this;
        }

        public Builder withBasePrice(double price) {
            this.basePrice = price;
            this.price = price;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public CustomPizza build() {
            return new CustomPizza(this);
        }
    }
}