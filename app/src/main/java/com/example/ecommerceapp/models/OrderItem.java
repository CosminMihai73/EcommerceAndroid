package com.example.ecommerceapp.models;

public class OrderItem {
    private String productName;
    private int quantity;


    public OrderItem(String productName, int quantity) {
        this.productName = productName;
        this.quantity = quantity;
    }


    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }


    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
