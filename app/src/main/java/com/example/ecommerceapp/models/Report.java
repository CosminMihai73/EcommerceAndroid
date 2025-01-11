package com.example.ecommerceapp.models;

public class Report {
    private String productName;
    private int quantity;
    private double price;
    private double totalValue;


    public Report(String productName, int quantity, double price, double totalValue) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.totalValue = totalValue;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }
}
