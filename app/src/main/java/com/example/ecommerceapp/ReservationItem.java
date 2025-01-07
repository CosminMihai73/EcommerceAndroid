package com.example.ecommerceapp;

public class ReservationItem {
    private String productName;
    private int quantity;
    private String expirationDate;

    // Constructor
    public ReservationItem(String productName, int quantity, String expirationDate) {
        this.productName = productName;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
    }

    // Getters
    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getExpirationDate() {
        return expirationDate;
    }
}
