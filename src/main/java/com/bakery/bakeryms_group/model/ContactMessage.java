package com.bakery.bakeryms_group.model;

public class ContactMessage {
    private String name;
    private String email;
    private String subject;
    private String message;
    private String date;
    private int index;

    // 1. Default Constructor
    public ContactMessage() {
    }

    // 2. Parameterized Constructor
    public ContactMessage(String name, String email, String subject, String message, String date) {
        this.name = name;
        this.email = email;
        this.subject = subject;
        this.message = message;
        this.date = date;
    }

    // 3. Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    // 4. toString() method
    @Override
    public String toString() {
        return "ContactMessage{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", subject='" + subject + '\'' +
                ", message='" + message + '\'' +
                ", date='" + date + '\'' +
                ", index=" + index +
                '}';
    }

}