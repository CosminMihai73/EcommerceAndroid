package com.example.ecommerceapp.models;

public class Order {
    private int orderId;
    private String fullName;
    private String address;
    private String paymentMethod;
    private String deliveryMethod;
    private double totalPrice;
    private String createdAt;

    public Order(int orderId, String fullName, String address, String paymentMethod, String deliveryMethod, double totalPrice, String createdAt) {
        this.orderId = orderId;
        this.fullName = fullName;
        this.address = address;
        this.paymentMethod = paymentMethod;
        this.deliveryMethod = deliveryMethod;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAddress() {
        return address;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
