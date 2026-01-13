package org.example.controller;

import org.example.model.Order;
import org.example.model.OrderItem;
import org.example.model.Product;
import org.example.model.User;
import org.example.repository.OrderRepository;
import org.example.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

public class StaffController {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OfferService offerService;

    private Order currentOrder;
    private final User loggedUser;

    public StaffController(User user) {
        this.loggedUser = user;
        this.productRepository = new ProductRepository();
        this.orderRepository = new OrderRepository();
        this.offerService = new OfferService();
        startNewOrder();
    }

    public void startNewOrder() {
        this.currentOrder = new Order();
        this.currentOrder.setUser(loggedUser);
        this.currentOrder.setTableNumber(0);
    }

    public List<Product> getMenu() {
        return productRepository.getAllProducts();
    }

    public void addToOrder(Product product, int quantity) {
        boolean found = false;
        for (OrderItem item : currentOrder.getItems()) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + quantity);
                found = true;
                break;
            }
        }
        if (!found) {
            OrderItem newItem = new OrderItem(product, quantity);
            currentOrder.addItem(newItem);
        }
    }

    public void removeFromOrder(OrderItem item) {
        currentOrder.getItems().remove(item);
    }

   //modif cantitate
    public void updateItemQuantity(OrderItem item, int change) {
        int newQty = item.getQuantity() + change;
        if (newQty <= 0) {
            removeFromOrder(item);
        } else {
            item.setQuantity(newQty);
        }
    }

    public void setTable(int tableNumber) {
        currentOrder.setTableNumber(tableNumber);
    }

    public Order getCurrentOrder() {
        return currentOrder;
    }

    public List<OrderItem> getOrderItemsWithDiscounts() {
        List<OrderItem> discounts = offerService.applyOffers(currentOrder);
        List<OrderItem> displayList = new ArrayList<>(currentOrder.getItems());
        displayList.addAll(discounts);
        return displayList;
    }

    public double calculateFinalTotal() {
        offerService.applyOffers(currentOrder);
        return currentOrder.getTotalAmount();
    }

    public void saveOrder() {
        if (currentOrder.getItems().isEmpty()) return;
        calculateFinalTotal();
        currentOrder.setStatus("PAID");
        orderRepository.saveOrder(currentOrder);
    }

    public List<Order> getMyOrderHistory() {
        return orderRepository.getOrdersByUser(loggedUser.getId());
    }
}