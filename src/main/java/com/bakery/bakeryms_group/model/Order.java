package com.bakery.bakeryms_group.model;

import java.util.List;

public class Order {
    private int index;
    private String orderId;
    private String type;
    private String username;
    private String status;
    private String method;
    private String address;
    private String items;
    private String total;

    // --- Specific Variables for Custom Cake ---
    private String cakeDesign;
    private String imageName;
    private String flavor;
    private String size;
    private String greeting;
    private String gColor;
    private String description;
    private String specialDescription;
    private String mobile;
    private String deliveryDate;

    // List of items reserved to be displayed as rows in the table (for Web Orders)
    private List<OrderItem> parsedItems;

    public Order() {
    }

    // --- Getters and Setters ---

    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getItems() { return items; }
    public void setItems(String items) { this.items = items; }

    public String getTotal() { return total; }
    public void setTotal(String total) { this.total = total; }

    public String getCakeDesign() { return cakeDesign; }
    public void setCakeDesign(String cakeDesign) { this.cakeDesign = cakeDesign; }

    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }

    public String getFlavor() { return flavor; }
    public void setFlavor(String flavor) { this.flavor = flavor; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getGreeting() { return greeting; }
    public void setGreeting(String greeting) { this.greeting = greeting; }

    public String getGColor() { return gColor; }
    public void setGColor(String gColor) { this.gColor = gColor; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSpecialDescription() { return specialDescription; }
    public void setSpecialDescription(String specialDescription) { this.specialDescription = specialDescription; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(String deliveryDate) { this.deliveryDate = deliveryDate; }

    public List<OrderItem> getParsedItems() { return parsedItems; }
    public void setParsedItems(List<OrderItem> parsedItems) { this.parsedItems = parsedItems; }
}
