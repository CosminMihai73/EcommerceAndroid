package com.example.ecommerceapp;

public class OrderItem {
    private String productName;
    private int quantity;

    // Constructor
    public OrderItem(String productName, int quantity) {
        this.productName = productName;
        this.quantity = quantity;
    }

    // Getters
    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    // Setters
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
