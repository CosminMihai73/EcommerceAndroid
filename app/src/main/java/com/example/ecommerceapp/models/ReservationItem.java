package com.example.ecommerceapp.models;

public class ReservationItem {
    private String productName;
    private int quantity;
    private String expirationDate;


    public ReservationItem(String productName, int quantity, String expirationDate) {
        this.productName = productName;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
    }

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
