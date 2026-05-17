package com.bakery.bakeryms_group.model;

public class CartItem {
    private String productName;
    private double price;
    private int quantity;
    private String image;

    // Constructors, Getters and Setters
    public CartItem(String productName, double price, int quantity, String image) {
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
    }

    public String getProductName() { return productName; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getImage() { return image; }
}
