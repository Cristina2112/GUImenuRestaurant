package org.example.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurant_orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime orderDate;
    private double totalAmount;
    private int tableNumber;

    //iteratia 7
    private double discountAmount;
    private String status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItem> items = new ArrayList<>();

    public Order() {
        this.orderDate = LocalDateTime.now();
        this.status = "NEW"; // setam default ca fiind comandă nouă
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    // calcul subtotal (fara reduceri)
    public double getSubtotal() {
        double sum = 0;
        for (OrderItem item : items) {
            sum += item.getSubtotal();
        }
        return sum;
    }

    public Long getId() { return id; }

    public void setTotalAmount(double total) { this.totalAmount = total; }
    public double getTotalAmount() { return totalAmount; }

    public void setUser(User user) { this.user = user; }
    public User getUser() { return user; }

    public void setTableNumber(int tableNumber) { this.tableNumber = tableNumber; }
    public int getTableNumber() { return tableNumber; }

    public List<OrderItem> getItems() { return items; }
    public LocalDateTime getOrderDate() { return orderDate; }

    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}