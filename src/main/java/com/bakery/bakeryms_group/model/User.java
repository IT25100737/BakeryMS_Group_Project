package com.bakery.bakeryms_group.model;

import java.util.Date;

/**
 * User Model class for Bakery Management System.
 */
public class User {
    private String fullName;
    private String username;
    private String email;
    private String mobile;
    private String address;
    private String postCode;
    private String password;
    private String image;
    private String role;
    private Date createdAt;

    public User() {
        this.createdAt = new Date();
    }

    public User(String fullName, String username, String email, String mobile,
                String address, String postCode, String password,
                String image, String role) {
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.mobile = mobile;
        this.address = address;
        this.postCode = postCode;
        this.password = password;
        this.image = image;
        this.role = role;
        this.createdAt = new Date();
    }

    // Getters and Setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPostCode() { return postCode; }
    public void setPostCode(String postCode) { this.postCode = postCode; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "User{" +
                "fullName='" + fullName + '\'' +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}