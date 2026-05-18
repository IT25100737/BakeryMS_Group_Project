package com.bakery.bakeryms_group.model;

public class OrderItem {
    private String name;
    private String quantity;
    private String price;

    // 1. Default Constructor
    public OrderItem() {
    }

    // 2. Parameterized Constructor
    public OrderItem(String name, String quantity, String price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    // 3. Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
