package com.bakery.bakeryms_group.model;

public class Product {
    private String name;
    private double price;
    private String category;
    private String subCategory;
    private String description;
    private int quantity;
    private boolean isActive;
    private String image;

    public Product() {
    }

    public Product(String name, double price, String category, String subCategory,
        this.name = name;
        this.price = price;
        this.category = category;
        this.subCategory = subCategory;
        this.description = description;
        this.quantity = quantity;
        this.isActive = isActive;
        this.image = image;
    }

    // --- Getters  ---

    public String getName() { return name; }

    public double getPrice() { return price; }

    public String getCategory() { return category; }

    public String getSubCategory() { return subCategory; }

    public String getDescription() { return description; }

    public int getQuantity() { return quantity; }

    public boolean isActive() { return isActive; }

    public String getImage() { return image; }

    public double getAverageRating() { return averageRating; }

    // --- Setters  ---

    public void setName(String name) { this.name = name; }

    public void setPrice(double price) { this.price = price; }

    public void setCategory(String category) { this.category = category; }

    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }

    public void setDescription(String description) { this.description = description; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public void setActive(boolean active) { isActive = active; }

    public void setImage(String image) { this.image = image; }

    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    /**
     * To check if the data was received correctly (Debugging)
     */
    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", subCategory='" + subCategory + '\'' +
                ", quantity=" + quantity +
                ", isActive=" + isActive +
                '}';
    }
}
