package com.example.ecommerceapp;

public class CartItem {
    private String productName;
    private String productBrand;
    private double productPrice;
    private int quantity;
    private String imagePath;

    // Constructor fără ID
    public CartItem(String productName, String productBrand, double productPrice, int quantity, String imagePath) {
        this.productName = productName;
        this.productBrand = productBrand;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.imagePath = imagePath;
    }

    // Getter și Setter pentru câmpuri
    public String getProductName() {
        return productName;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
