package com.bakery.bakeryms_group.controller;

import com.bakery.bakeryms_group .model.User;
import com.bakery.bakeryms_group .model.Order;
import com.bakery.bakeryms_group .model.OrderItem;
import com.bakery.bakeryms_group .model.Product;
import com.bakery.bakeryms_group .service.UserService;
import com.bakery.bakeryms_group .service.OrderService;
import com.bakery.bakeryms_group .service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    // ================= (USER PROFILE MANAGEMENT) =================

    @GetMapping("/profile")
    public String showUserProfile(Model model, HttpSession session) {
        // 1. Getting the User from the Session
        User currentUser = (User) session.getAttribute("user");

        // 2. Send to the login page if the user is not logged in.
        if (currentUser == null) {
            return "redirect:/login";
        }

        // 3. Retrieving user's order history
        List<String> rawOrders = orderService.getOrdersByUsername(currentUser.getUsername());
        List<Order> parsedOrders = new ArrayList<>();

        for (String raw : rawOrders) {
            try {
                String[] parts = raw.split(" \\| ");
                Order o = new Order();

                // Creating an Order ID (using a hash code)
                o.setOrderId("#" + (Math.abs(raw.hashCode() % 9000) + 1000));

                String orderType = parts[0].trim();
                o.setType(orderType);
                o.setUsername(extractValue(parts[1], "User:"));
                o.setStatus(extractValue(parts[2], "Status:"));

                if (orderType.equalsIgnoreCase("CUSTOM CAKE")) {
                    o.setMethod("Delivery Details");
                    o.setTotal("Contact for Price");

                    String foundDate = "N/A";
                    String foundMobile = "N/A";

                    for (String part : parts) {
                        String trimmedPart = part.trim();
                        if (trimmedPart.contains("Date:")) {
                            foundDate = trimmedPart.substring(trimmedPart.indexOf("Date:") + 5).trim();
                        }
                        if (trimmedPart.contains("Contact:")) {
                            foundMobile = trimmedPart.substring(trimmedPart.indexOf("Contact:") + 8).trim();
                        }
                    }

                    o.setDeliveryDate(foundDate);
                    o.setMobile(foundMobile);
                    parsedOrders.add(o);

                } else {
                    o.setMethod(extractValue(parts[3], "Method:"));
                    o.setAddress(extractValue(parts[4], "Address:"));
                    o.setTotal(extractValue(parts[6], "Total:"));
                    o.setDeliveryDate("N/A");
                    o.setMobile("N/A");

                    String rawItems = extractValue(parts[5], "Items:");
                    List<OrderItem> itemList = new ArrayList<>();

                    if (!rawItems.equals("N/A")) {
                        String[] itemArray = rawItems.split(",");
                        for (String itemStr : itemArray) {
                            if (itemStr.contains("(") && itemStr.contains(")")) {
                                String name = itemStr.substring(0, itemStr.indexOf("(")).trim();
                                String qty = itemStr.substring(itemStr.indexOf("(") + 1, itemStr.indexOf(")")).trim();

                                String unitPrice = "0.0";
                                Product p = productService.getProductByName(name);
                                if (p != null) {
                                    unitPrice = String.valueOf(p.getPrice());
                                }
                                itemList.add(new OrderItem(name, qty, unitPrice));
                            }
                        }
                    }
                    o.setParsedItems(itemList);
                    parsedOrders.add(o);
                }
            } catch (Exception e) {
                System.err.println("Error parsing order for profile: " + raw);
            }
        }

        model.addAttribute("product", new Product());

        // 5. Adding User Data and Order History to the Model
        model.addAttribute("user", currentUser);
        model.addAttribute("orderHistory", parsedOrders);
        model.addAttribute("product", new Product());

        return "profile";
    }

    // =================  (Update Profile) =================
    @PostMapping("/updateProfile")
    public String updateProfile(@RequestParam("fullName") String fullName,
                                @RequestParam("mobile") String mobile,
                                @RequestParam("address") String address,
                                @RequestParam("postCode") String postCode,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("user");

        if (user != null) {
            user.setFullName(fullName);
            user.setMobile(mobile);
            user.setAddress(address);
            user.setPostCode(postCode);

            List<User> allUsers = userService.getAllUsers();
            for (int i = 0; i < allUsers.size(); i++) {
                if (allUsers.get(i).getUsername().equals(user.getUsername())) {
                    allUsers.set(i, user);
                    break;
                }
            }
            userService.rewriteUserFile(allUsers);
            session.setAttribute("user", user);

            redirectAttributes.addFlashAttribute("message", "Profile updated successfully!");
            return "redirect:/profile";
        }
        return "redirect:/login";
    }

    // Method that helps separate data
    private String extractValue(String part, String prefix) {
        if (part == null || !part.contains(prefix)) return "N/A";
        return part.substring(part.indexOf(prefix) + prefix.length()).trim();
    }
}