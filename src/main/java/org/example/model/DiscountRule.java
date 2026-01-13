package org.example.model;

//interfata functinala pentru a fi folosita sintaxa ->
@FunctionalInterface
public interface DiscountRule {
    double calculateDiscount(Order order);
}
