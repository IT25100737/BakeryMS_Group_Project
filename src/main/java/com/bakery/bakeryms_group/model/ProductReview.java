package com.bakery.bakeryms_group.model;

import java.time.LocalDate;

public class ProductReview {
    private String productName;
    private String userName;
    private String userImage;
    private int rating;
    private String comment;
    private LocalDate reviewDate;

    public ProductReview() {
    }

    public ProductReview(String productName, String userName, String userImage, int rating, String comment, LocalDate reviewDate) {
        this.productName = productName;
        this.userName = userName;
        this.userImage = userImage;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
    }

    // Getters and Setters
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserImage() { return userImage; } // Getter
    public void setUserImage(String userImage) { this.userImage = userImage; } // Setter

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDate getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDate reviewDate) { this.reviewDate = reviewDate; }
}
