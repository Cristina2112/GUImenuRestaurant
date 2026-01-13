package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    // stocam prețul la momentul vânzării.
    // dacă e produs normal -> prețul din meniu.
    // dacă e reducere -> prețul negativ calculat de OfferService.
    private double priceAtSale;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;


    public OrderItem() {}


    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        // ingheță prețul la momentul adăugării în coș
        if (product != null) {
            this.priceAtSale = product.getPrice();
        }
    }

    // calcul subtotalul (Cantitate * Preț salvat)
    public double getSubtotal() {
        return priceAtSale * quantity;
    }


    public Long getId() { return id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPriceAtSale() { return priceAtSale; }
    public void setPriceAtSale(double priceAtSale) { this.priceAtSale = priceAtSale; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
}