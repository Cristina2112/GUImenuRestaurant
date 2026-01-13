package org.example.controller;

import org.example.model.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class OfferService {

    public static boolean HAPPY_HOUR_ACTIVE = true;
    public static boolean MEAL_DEAL_ACTIVE = true;
    public static boolean PARTY_PACK_ACTIVE = true;

    public List<OrderItem> applyOffers(Order order) {
        List<OrderItem> discountItems = new ArrayList<>();
        double totalDiscountValue = 0.0;
        List<OrderItem> items = order.getItems();

        // happy hour
        if (HAPPY_HOUR_ACTIVE) {
            List<OrderItem> drinks = new ArrayList<>();
            for (OrderItem item : items) {
                if (item.getProduct() instanceof Drinks) {
                    for (int i = 0; i < item.getQuantity(); i++) {
                        drinks.add(new OrderItem(item.getProduct(), 1));
                    }
                }
            }
            // la fiecare a 2-a băutura aplicăm reducerea
            for (int i = 1; i < drinks.size(); i += 2) {
                double discount = drinks.get(i).getProduct().getPrice() * 0.5;
                totalDiscountValue += discount;
                Product fakeProduct = new Drinks("Happy Hour (-50%)", -discount, 0, null);
                discountItems.add(new OrderItem(fakeProduct, 1));
            }
        }

        // meal deal (Pizza + Desert -> Desert -25%)
        if (MEAL_DEAL_ACTIVE) {
            long pizzaCount = items.stream()
                    .filter(i -> i.getProduct().getName().toLowerCase().contains("pizza"))
                    .mapToInt(OrderItem::getQuantity).sum();

            List<Product> desserts = new ArrayList<>();
            for (OrderItem item : items) {
                if (item.getProduct() instanceof Food && item.getProduct().getName().toLowerCase().contains("desert")) {
                    for(int k=0; k<item.getQuantity(); k++) desserts.add(item.getProduct());
                }
            }
            desserts.sort(Comparator.comparingDouble(Product::getPrice)); // Ieftinele primele

            int pairs = (int) Math.min(pizzaCount, desserts.size());
            for (int i = 0; i < pairs; i++) {
                double discount = desserts.get(i).getPrice() * 0.25;
                totalDiscountValue += discount;
                Product fakeProduct = new Food("Meal Deal (-25% Desert)", -discount, 0);
                discountItems.add(new OrderItem(fakeProduct, 1));
            }
        }

        // party pack (4 Pizza -> Cea mai ieftină Gratuită)
        if (PARTY_PACK_ACTIVE) {
            List<Product> allPizzas = new ArrayList<>();
            for (OrderItem item : items) {
                if (item.getProduct().getName().toLowerCase().contains("pizza")) {
                    for(int k=0; k<item.getQuantity(); k++) allPizzas.add(item.getProduct());
                }
            }

            if (allPizzas.size() >= 4) {
                // sortare cresc
                allPizzas.sort(Comparator.comparingDouble(Product::getPrice));

                int freeCount = allPizzas.size() / 4;

                for (int i = 0; i < freeCount; i++) {
                    double discount = allPizzas.get(i).getPrice();
                    totalDiscountValue += discount;

                    Product fakeProduct = new Food("Party Pack (Pizza Gratis)", -discount, 0);
                    discountItems.add(new OrderItem(fakeProduct, 1));
                }
            }
        }

        order.setDiscountAmount(totalDiscountValue);
        order.setTotalAmount(order.getSubtotal() - totalDiscountValue);
        return discountItems;
    }
}